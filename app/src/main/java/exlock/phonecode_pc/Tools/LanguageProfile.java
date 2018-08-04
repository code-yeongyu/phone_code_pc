package exlock.phonecode_pc.Tools;

import java.util.ArrayList;

public class LanguageProfile {
    private String json, name, version, wayToCreateVar;
    private ArrayList<String> categories, symbols, reserved;
    private ArrayList<String> reservedObject;

    public LanguageProfile(String json){
        this.json = json;
        try {
            String informsJson = JsonManager.getJsonOBJByKey(this.json, "lang_informs");
            String symbolsJson = JsonManager.getJsonOBJByKey(json, "symbols");
            String reservedJson = JsonManager.getJsonOBJByKey(json, "reserved");
            this.name = JsonManager.getJsonStrByKey(informsJson, "name");
            this.version = JsonManager.getJsonStrByKey(informsJson, "version");
            this.wayToCreateVar = JsonManager.getJsonStrByKey(informsJson, "way_to_create_var");
            this.categories = JsonManager.getJsonAllkeys
                    (JsonManager.getJsonOBJByKey(json, "functions"));
            this.symbols = JsonManager.getJsonArrByKey(symbolsJson, "normal");
            this.reserved = JsonManager.getJsonArrByKey(reservedJson, "normal");
            this.reservedObject = JsonManager.getJsonArrByKey(reservedJson, "object");
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
    public String getWayToCreateVar(){
        return this.wayToCreateVar;
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
    public ArrayList<String> getReservedObject() {
        return this.reservedObject;
    }
    public ArrayList<String> getFunctions(String categoryName){
        String jsonFunctions = JsonManager.getJsonOBJByKey(this.json, "functions");//json->function
        return JsonManager.getJsonArrByKey(jsonFunctions, categoryName);
    }
}