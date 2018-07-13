package exlock.phonecode_pc.Tools;

import java.util.ArrayList;

import exlock.phonecode_pc.Tools.JsonManager;

public class LanguageProfile {
    private String json, name, version;
    private ArrayList<String> categories, symbols, reserved;

    public LanguageProfile(String json){
        try {
            this.json = json;
            String informsJson = JsonManager.getJsonOBJByKey(this.json, "lang_informs");
            this.name = JsonManager.getJsonStrByKey(informsJson, "name");
            this.version = JsonManager.getJsonStrByKey(informsJson, "version");
            this.categories = JsonManager.getJsonAllkeys
                    (JsonManager.getJsonOBJByKey(json, "functions"));
            this.symbols = JsonManager.getJsonAllkeys
                    (JsonManager.getJsonOBJByKey(json, "symbols"));
            this.reserved = JsonManager.getJsonAllkeys
                    (JsonManager.getJsonOBJByKey(json, "reserved"));
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
    public ArrayList<String> getReserved() {
        return this.reserved;
    }
    public ArrayList<String> getSymbols() {
        return this.symbols;
    }
    public ArrayList<String> getCategories(){
        return this.categories;
    }
    public ArrayList<String> getFunctions(String category){
        String jsonFunctions = JsonManager.getJsonOBJByKey(this.json, "functions");//json->function
        String jsonCategory = JsonManager.getJsonOBJByKey(
                jsonFunctions, category);//function->category
        return JsonManager.getJsonAllkeys(jsonCategory);
    }
    public String getFunctionValue(String category, String function){
        String jsonFunctions = JsonManager.getJsonOBJByKey(this.json, "functions");//json->function
        String jsonCategory = JsonManager.getJsonOBJByKey(
                jsonFunctions, category);//function->category
        String functionValue = JsonManager.getJsonStrByKey(jsonCategory, function);//category->function value
        //monitor the function value
        try {
            return functionValue;
        }catch (Exception e){
            return "";
        }
    }
}