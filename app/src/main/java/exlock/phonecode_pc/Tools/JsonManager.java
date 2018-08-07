package exlock.phonecode_pc.Tools;

import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

public class JsonManager {
    static public String getJsonFromPath(String path) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//if accessing to storage is available
            StringBuilder builder = new StringBuilder();
            String data;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));//get profile json from set path
                data = reader.readLine();
                while (data != null) {
                    builder.append(data);
                    data = reader.readLine();
                }//get json string from file
                reader.close();
                return builder.toString();
            } catch (FileNotFoundException e) {
                return "noFile";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static public String modifyJsonByKey(String jsonString, String position, String key, String value) {
        String jsonStringResult = "";
        try{
            JSONArray jarray;
            JSONObject jObject;
            jarray = new JSONArray("["+jsonString+"]");
            for(int i=0; i < jarray.length(); i++){
                jObject = jarray.getJSONObject(i);
                jObject.getJSONObject(position).put(key, value);
                jsonStringResult = jObject.toString();
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonStringResult;
    }

    static public String getJsonOBJByKey(String jsonString, String key){
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
            e.printStackTrace();
        }
        return jsonStringResult;
    }
    static public String getJsonStrByKey(String jsonString, String key){
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
            e.printStackTrace();
        }
        return jsonStringResult;
    }
    static public ArrayList<String> getJsonArrByKey(String jsonString, String key){
        ArrayList<String> result = new ArrayList<>();
        try{
            JSONArray jarray;
            jarray = new JSONArray(getJsonStrByKey(jsonString, key));
            for(int i=0; i < jarray.length(); i++){
                result.add(jarray.get(i).toString());
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    static public ArrayList<String> getJsonAllkeys(@NonNull @NotNull String jsonString){
        ArrayList<String> keys_array = new ArrayList<>();
        try {
            JSONArray jarray = new JSONArray("[" + jsonString + "]");
            JSONObject json_array = jarray.optJSONObject(0);
            if(json_array != null) {
                Iterator<?> keys = json_array.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    keys_array.add(key);
                }
            }//todo: if json_array == null, print wrong json file
        }catch (JSONException e){
            e.printStackTrace();
            return keys_array;
        }
        return keys_array;
    }
}