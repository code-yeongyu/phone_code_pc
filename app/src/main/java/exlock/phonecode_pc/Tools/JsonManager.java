package exlock.phonecode_pc.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonManager {
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
    static public ArrayList<String> getJsonAllkeys(String jsonString){
        ArrayList<String> keys_array = new ArrayList<>();
        try {
            JSONArray jarray = new JSONArray("[" + jsonString + "]");
            JSONObject json_array = jarray.optJSONObject(0);
            Iterator<?> keys = json_array.keys();
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
}
