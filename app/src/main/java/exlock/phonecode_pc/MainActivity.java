package exlock.phonecode_pc;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import exlock.phonecode_pc.Tools.JsonManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkAndAskPermission()) {
            this.loadJsonToSharedPreferences();
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
        }
    }
    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            this.loadJsonToSharedPreferences();
            Intent i = new Intent(this, EditActivity.class);
            startActivity(i);
        }
    }
    private boolean checkAndAskPermission(){
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        return false;
    }
    public void loadJsonToSharedPreferences(){
        String path = Environment.getExternalStorageDirectory() + "/PhoneCode/language1.json";//set path
        SharedPreferences sp = getSharedPreferences("json", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("profileJson", JsonManager.getJsonFromPath(path));
        editor.apply();
    }
}
