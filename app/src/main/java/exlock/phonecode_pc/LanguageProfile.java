package exlock.phonecode_pc;

import java.util.ArrayList;

import exlock.phonecode_pc.Tools.JsonManager;

/**
 * Created by experse on 18. 5. 27.
 */

public class LanguageProfile {
    private String json, name, version;
    private ArrayList<String> categories, symbols, phrases;

    public LanguageProfile(String json){
        String informsJson = JsonManager.getJsonOBJByKey(json, "lang_informs");
        this.json = json;
        this.name = JsonManager.getJsonStrByKey(informsJson, "name");
        this.version = JsonManager.getJsonStrByKey(informsJson, "version");
        this.categories = JsonManager.getJsonAllkeys
                (JsonManager.getJsonOBJByKey(json, "functions"));
        this.symbols = JsonManager.getJsonAllkeys
                (JsonManager.getJsonOBJByKey(json, "symbols"));
        this.phrases = JsonManager.getJsonAllkeys
                (JsonManager.getJsonOBJByKey(json, "phrase"));
    }
    public String getLanguageName(){
        return this.name;
    }
    public String getLanguageVersion(){
        return this.version;
    }
    public ArrayList<String> getPhrases() {
        return this.phrases;
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