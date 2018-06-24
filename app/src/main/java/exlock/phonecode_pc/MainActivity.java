package exlock.phonecode_pc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import exlock.phonecode_pc.EditFeatures.EditActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadJson();
        Intent i = new Intent(this, EditActivity.class);
        startActivity(i);
    }
    private void loadJson(){
        SharedPreferences exe = getSharedPreferences("executeCount", MODE_PRIVATE);
        if(!exe.getBoolean("isFirstTime", false)){//if it's first time
            SharedPreferences.Editor editor = exe.edit();
            editor.putBoolean("isFirstTime", true);
            editor.apply();
        }else{
            return;
        }
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//if accessing to storage is available
            StringBuffer buffer = new StringBuffer();
            String data;
            try {
                String path = Environment.getExternalStorageDirectory() + "/PhoneCode/language1.json";//set path
                BufferedReader reader = new BufferedReader(new FileReader(path));//get profile json from set path
                data = reader.readLine();
                while (data != null) {
                    buffer.append(data);
                    data = reader.readLine();
                }//get json string from file
                reader.close();
                SharedPreferences sp = getSharedPreferences("json", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("profileJson", buffer.toString());
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }//get file
        }
    }
}
