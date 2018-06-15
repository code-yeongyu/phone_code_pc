package exlock.phonecode_pc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

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
                SharedPreferences sp = getSharedPreferences("json", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("profileJson", buffer.toString());
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }//get file
        }
    }
    private void createTestFile(){
        String path = Environment.getExternalStorageDirectory() + "/PhoneCode/helloworld.py";//set path
        File saveFile = new File(path);
        try{
            FileOutputStream fos = new FileOutputStream(saveFile);
            fos.write("".getBytes());
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
