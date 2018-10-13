// language profile을 위한 json파일을 관리하는 클래스
package exlock.phonecode_pc.Tools;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonManager {
    static public String getJsonFromPath(final String path) {
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
    //json 파일을 반환
    static public String modifyJsonByKey(final String jsonString, final String position, final String key, final String value) {
        String jsonStringResult = "";
        try{
            JSONObject jObject = new JSONObject(jsonString); // jsonString을 jsonobject로 변환
            jObject.getJSONObject(position).put(key, value);
            jsonStringResult = jObject.toString();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonStringResult;
    }
    //key 에 해당하는 부분을 수정하여 jsonString 전체를 반환
    static public String getJsonOBJByKey(final String jsonString, final String key){
        String jsonStringResult = "";
        try{
            JSONObject jObject = new JSONObject(jsonString);
            jsonStringResult = jObject.getJSONObject(key).toString();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonStringResult;
    }
    //key 에 해당하는 부분을 jsonObject로 반환
    static public String getJsonStrByKey(final String jsonString, final String key){
        String jsonStringResult = "";
        try{
            JSONObject jObject = new JSONObject(jsonString);
            jsonStringResult = jObject.get(key).toString();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonStringResult;
    }
    //key 에 해당하는 부분을 string 으로 반환
    static public ArrayList<String> getJsonArrByKey(final String jsonString, final String key){
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
    //key 에 해당하는 부분을 arraylist 로 반환
    static public ArrayList<String> getJsonAllKeys(@NonNull @NotNull final String jsonString){
        ArrayList<String> keys_array = new ArrayList<>();
        try {
            JSONObject jObject = new JSONObject(jsonString);
            Iterator<?> keys = jObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                keys_array.add(key);
            }
        }catch (JSONException e){
            e.printStackTrace();
            return keys_array;
        }
        return keys_array;
    }
    //모든 Key를 반환
    @Nullable
    static public Object addJsonKeyToArray(@NonNull @NotNull final String jsonString, final String key, final ArrayList<String> value){
        try {
            JSONArray temp = new JSONArray();
            for(int i = 0;i<value.size();i++) {
                temp.put(value.get(i));
            }
            JSONObject jObject = new JSONObject(jsonString);
            jObject.put(key, temp);
            return jObject;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    //key를 array로 추가 한 뒤 object 형태로 반환
}