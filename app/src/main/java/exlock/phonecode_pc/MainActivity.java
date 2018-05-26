package exlock.phonecode_pc;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public String getJsonOBJByKey(String jsonString, String key){
        String jsonStringResult = "";
        try{
            JSONArray jarray;
            JSONObject jObject;
            jarray = new JSONArray("["+jsonString+"]");
            for(int i=0; i < jarray.length(); i++){
                jObject = jarray.getJSONObject(i);
                jsonStringResult = jObject.getJSONObject(key).toString();
            }
        }
        catch(JSONException e) {
            Log.d("오하우", "obj");
            e.printStackTrace();
        }
        return jsonStringResult;
    }
    public String getJsonStrByKey(String jsonString, String key){
        String jsonStringResult = "";
        try{
            JSONArray jarray;
            JSONObject jObject;
            jarray = new JSONArray("["+jsonString+"]");
            for(int i=0; i < jarray.length(); i++){
                jObject = jarray.getJSONObject(i);
                jsonStringResult = jObject.get(key).toString();
            }
        }
        catch(JSONException e) {
            Log.d("오하우", "str");
            e.printStackTrace();
        }
        return jsonStringResult;
    }
    public ArrayList<String> getJsonAllkeys(String jsonString){
        ArrayList<String> keys_array = new ArrayList<String>();
        ArrayList<String> values_array = new ArrayList<String>();
        try {
            JSONArray jarray = new JSONArray("[" + jsonString + "]");
            JSONObject json_array = jarray.optJSONObject(0);
            Iterator<?> keys = json_array.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                keys_array.add(key);
                values_array.add(json_array.get(key).toString());
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return keys_array;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView funcs = findViewById(R.id.funcs);
        TextView informs = findViewById(R.id.informs);

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//if accessing to storage is available
            StringBuffer buffer = new StringBuffer();
            String data;
            try {
                String path = Environment.getExternalStorageDirectory() + "/PhoneCode/language1.json";
                BufferedReader reader = new BufferedReader(new FileReader(path));
                data = reader.readLine();
                while(data != null){
                    buffer.append(data);
                    data = reader.readLine();
                }
                Log.d("테스트입니당", "파일 잘 불러왔어영");
            } catch(IOException e) {
                e.printStackTrace();
                Log.d("테스트입니당", "파일 으앙");
            }//get file
            String a1 = this.getJsonOBJByKey(buffer.toString(), "functions");
            funcs.setText("");
            String builtIn = this.getJsonOBJByKey(a1, "built-in");
            ArrayList<String> keys = this.getJsonAllkeys(builtIn);
            String values = "";
            String lang_informs = this.getJsonOBJByKey(buffer.toString(), "lang_informs");
            String lang_name = this.getJsonStrByKey(lang_informs, "name");
            String lang_version = this.getJsonStrByKey(lang_informs, "version");
            informs.setText("Language : " + lang_name+"\n"+
                            "Version : "  + lang_version);
            for(int i = 0; i < keys.size(); i++){
                values = values + keys.get(i) + " : " + getJsonStrByKey(builtIn, keys.get(i))+"\n";
            }
            funcs.setText(values);
        }

    }
}
