package exlock.phonecode_pc.Tools;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

public class LanguageProfile {
    private String json, name, version;
    private ArrayList<String> categories, symbols, reserved;

    public LanguageProfile(String json){
        try {
            this.json = json;
            String informsJson = JsonManager.getJsonOBJByKey(this.json, "lang_informs");
            String symbolsJson = JsonManager.getJsonOBJByKey(json, "symbols");
            String reservedJson = JsonManager.getJsonOBJByKey(json, "reserved");
            this.name = JsonManager.getJsonStrByKey(informsJson, "name");
            this.version = JsonManager.getJsonStrByKey(informsJson, "version");
            this.categories = JsonManager.getJsonAllkeys
                    (JsonManager.getJsonOBJByKey(json, "functions"));
            this.symbols = JsonManager.getJsonArrByKey(symbolsJson, "normal");
            this.reserved = JsonManager.getJsonArrByKey(reservedJson, "normal");
        }catch (Exception e){//when got wrong json file
            e.printStackTrace();
        }
    }
    public String getLanguageName(){
        return this.name;
    }
    public String getLanguageVersion(){
        return this.version;
    }
    public ArrayList<String> getCategories(){
        return this.categories;
    }
    public ArrayList<String> getSymbols() {
        return this.symbols;
    }
    public ArrayList<String> getReserved() {
        return this.reserved;
    }
    public ArrayList<String> getFunctions(String categoryName){
        String jsonFunctions = JsonManager.getJsonOBJByKey(this.json, "functions");//json->function
        return JsonManager.getJsonArrByKey(jsonFunctions, categoryName);
    }
}