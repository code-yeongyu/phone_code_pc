package exlock.phonecode_pc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//if accessing to storage is available
            StringBuilder builder = new StringBuilder();
            String data;
            try {
                String path = Environment.getExternalStorageDirectory() + "/PhoneCode/language1.json";//set path
                BufferedReader reader = new BufferedReader(new FileReader(path));//get profile json from set path
                data = reader.readLine();
                while (data != null) {
                    builder.append(data);
                    data = reader.readLine();
                }//get json string from file
                reader.close();
                SharedPreferences sp = getSharedPreferences("json", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("profileJson", builder.toString());
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }//get file
        }
    }
}
