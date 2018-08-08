package exlock.phonecode_pc.Tools;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public class LanguageProfileJsonReader {
    private String json, name, indent, version, wayToCreateVar;
    private ArrayList<String> categories, symbols, reserved;
    private ArrayList<String> reservedObject;

    @Nullable
    public static LanguageProfileMember getProfileMembers(String json){
        String name, version, wayToCreateVar, indent;
        ArrayList<String> categories, symbols, reserved, reservedObject;
        LanguageProfileMember lpm = new LanguageProfileMember();
        try {
            String informsJson = JsonManager.getJsonOBJByKey(json, "lang_informs");
            String symbolsJson = JsonManager.getJsonOBJByKey(json, "symbols");
            String reservedJson = JsonManager.getJsonOBJByKey(json, "reserved");

            name = JsonManager.getJsonStrByKey(informsJson, "name");
            indent = JsonManager.getJsonStrByKey(informsJson, "indent");
            version = JsonManager.getJsonStrByKey(informsJson, "version");
            wayToCreateVar = JsonManager.getJsonStrByKey(informsJson, "way_to_create_var");
            categories = JsonManager.getJsonAllkeys
                    (JsonManager.getJsonOBJByKey(json, "functions"));
            symbols = JsonManager.getJsonArrByKey(symbolsJson, "normal");
            reserved = JsonManager.getJsonArrByKey(reservedJson, "normal");
            reservedObject = JsonManager.getJsonArrByKey(reservedJson, "object");

            lpm.json = json;
            lpm.name = name;
            lpm.indent = indent;
            lpm.version = version;
            lpm.wayToCreateVar = wayToCreateVar;
            lpm.categories = categories;
            lpm.symbols = symbols;
            lpm.reserved = reserved;
            lpm.reservedObject = reservedObject;

            return lpm;
        }catch (Exception e){//when got wrong json file
            return null;//not valid profile
        }
    }

    public LanguageProfileJsonReader(LanguageProfileMember lpm){
        this.json = lpm.json;
        this.name = lpm.name;
        this.indent = lpm.indent;
        this.version = lpm.version;
        this.wayToCreateVar = lpm.wayToCreateVar;
        this.categories = lpm.categories;
        this.symbols = lpm.symbols;
        this.reserved = lpm.reserved;
        this.reservedObject = lpm.reservedObject;
    }
    public String getLanguageName(){
        return this.name;
    }
    public String getIndent() { return this.indent; }
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