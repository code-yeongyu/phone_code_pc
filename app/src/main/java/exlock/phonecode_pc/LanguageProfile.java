package exlock.phonecode_pc;

import java.util.ArrayList;

import exlock.phonecode_pc.Tools.JsonManager;

/**
 * Created by experse on 18. 5. 27.
 */

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
        }catch (Exception e){//when got json wrong json file
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
    ArrayList<String> getCategories(){
        return this.categories;
    }//A function that returns all the categories
    public ArrayList<String> getFunctions(String category){
        //입력받은 카테고리를 찾아서 그 부분만 뚝 띄어서 리턴시켜야함
        String jsonFunctions = JsonManager.getJsonOBJByKey(this.json, "functions");
        String jsonCategory = JsonManager.getJsonOBJByKey(
                jsonFunctions, category);
        return JsonManager.getJsonAllkeys(jsonCategory);
    }
}