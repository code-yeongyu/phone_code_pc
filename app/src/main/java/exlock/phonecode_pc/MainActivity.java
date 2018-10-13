package exlock.phonecode_pc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import exlock.phonecode_pc.Tools.FilePath;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openFileButton = findViewById(R.id.openFileButton);
        Button openSettingsButton = findViewById(R.id.openSettingButton);

        openFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndAskPermission()) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    i.setType("application/*");
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(i,"Select the file that you want to edit"), 30);
                }
            }
        });
        // 파일 열기 버튼
        openSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(i);
            }
        });
        // 설정 열기 버튼

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){ //접근 권한이 있을 시
            Intent i = new Intent();
            i.setAction(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("application/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(i,"Select the file that you want to edit"), 30);
            //파일 매니저 오픈
        }
    }

    private boolean checkAndAskPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        return false;
    }
    //파일 권한과 관련된 함수

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) { //
            if (requestCode == 30) {
                Uri uri;
                if (resultData != null) {
                    uri = resultData.getData();
                    String[] path = uri.getPath().split(":");
                    String absolutePath = Environment.getExternalStorageDirectory() + "/" + path[1];

                    this.addFile(absolutePath); //새 파일 열기

                    Intent i = new Intent(this, EditActivity.class);
                    i.putExtra("path", absolutePath);
                    startActivity(i);
                }

            }
        }
    }
    private void addFile(String absolutePath){
        SharedPreferences fileSP = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = fileSP.edit();

        JSONObject pathJObject = new JSONObject();
        JSONArray pathJarray = new JSONArray();

        pathJarray.put(absolutePath);

        try {
            pathJObject.put("paths", pathJarray);
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }
        editor.putString("paths", pathJObject.toString());
        editor.apply();
    }
}
