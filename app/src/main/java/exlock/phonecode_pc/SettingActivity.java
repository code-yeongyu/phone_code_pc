package exlock.phonecode_pc;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import exlock.phonecode_pc.Tools.JsonManager;
import exlock.phonecode_pc.Tools.LanguageProfile;
import exlock.phonecode_pc.Tools.LanguageProfileJsonReader;
import exlock.phonecode_pc.Tools.LanguageProfileMember;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final TextView indentSetting = findViewById(R.id.settingIndentTextView);
        TextView profileSetting = findViewById(R.id.settingLanguageProfileTextView);
        TextView addProfile = findViewById(R.id.settingAddLanguageProfileTextView);

        final String jsonString = getSharedPreferences("json", MODE_PRIVATE)
                        .getString("profileJson", "");

        indentSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indentSetDialog(jsonString).show();
            }
        });
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_OPEN_DOCUMENT);
                i.setType("application/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(Intent.createChooser(i,"Select the language profiies"), 43);
            }
        });
        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_OPEN_DOCUMENT);
                i.setType("application/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(i,"Select the language profiies"), 44);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (resultCode == Activity.RESULT_OK) {//if set the language profiles
            if(requestCode == 43) {
                Uri uri = null;
                if (resultData != null) {
                    uri = resultData.getData();
                    String selectedFile = uri.toString();
                    String jsonExtension = selectedFile.substring(selectedFile.length() - 4, selectedFile.length());
                    if (!jsonExtension.equals("json")) {
                        Toast.makeText(SettingActivity.this, getString(R.string.toast_not_json), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] path = uri.getPath().split(":");
                    String absolutePath = Environment.getExternalStorageDirectory() + "/" + path[1];
                    String json = JsonManager.getJsonFromPath(absolutePath);

                    if (LanguageProfileJsonReader.getProfileMembers(json) == null) {
                        Toast.makeText(SettingActivity.this, getString(R.string.toast_wrong_json), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences profileJson = getSharedPreferences("json", MODE_PRIVATE);
                    SharedPreferences.Editor editor = profileJson.edit();
                    editor.putString("profileJson", json);
                    editor.apply();

                    addLanguageProfileDirectory(absolutePath, true);
                }

            }else if(requestCode == 44){
                Uri uri = null;
                if (resultData != null) {
                    uri = resultData.getData();
                    String selectedFile = uri.toString();
                    String jsonExtension = selectedFile.substring(selectedFile.length() - 4, selectedFile.length());

                    if (!jsonExtension.equals("json")) {
                        Toast.makeText(SettingActivity.this, getString(R.string.toast_not_json), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] path = uri.getPath().split(":");
                    String absolutePath = Environment.getExternalStorageDirectory() + "/" + path[1];
                    String newJson = JsonManager.getJsonFromPath(absolutePath);

                    if (LanguageProfileJsonReader.getProfileMembers(newJson) == null) {
                        Toast.makeText(SettingActivity.this, getString(R.string.toast_wrong_json), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences jsonSP = getSharedPreferences("json", MODE_PRIVATE);

                    LanguageProfileMember oldLPM = LanguageProfileJsonReader.getProfileMembers(
                            jsonSP.getString("profileJson", "")
                    );
                    LanguageProfileMember currLPM = LanguageProfileJsonReader.getProfileMembers(newJson);

                    ArrayList<String> currCategories;

                    if(oldLPM!=null&&currLPM!=null){
                        currCategories = currLPM.categories;

                        LanguageProfile oldLangProfile = new Gson().fromJson(
                                jsonSP.getString("profileJson", ""),
                                LanguageProfile.class
                        );

                        LanguageProfileJsonReader currLPJR = new LanguageProfileJsonReader(currLPM);

                        ArrayList<String> temp = new ArrayList<>();

                        if(currCategories.size() > 0) {
                            temp.add(JsonManager.addJsonKeyToArray(
                                    oldLangProfile.getFunctions().toString(),
                                    currCategories.get(0),
                                    currLPJR.getFunctions(currCategories.get(0))).toString());
                            for (int i = 1; i < currCategories.size(); i++) {
                                String targetString = currCategories.get(i);

                                temp.add(JsonManager.addJsonKeyToArray(
                                        temp.get(i - 1),
                                        targetString,
                                        currLPJR.getFunctions(targetString)
                                ).toString());
                            }
                            SharedPreferences.Editor editor = jsonSP.edit();
                            editor.putString("profileJson", temp.get(temp.size()-1));
                            editor.apply();
                            this.addLanguageProfileDirectory(absolutePath, false);
                        }
                    }else{
                        //error
                    }
                }
            }
        }
    }
    private Dialog indentSetDialog(final String jsonString) {
        ArrayList<String> temp = new ArrayList<>();
        final String twoSpace = getString(R.string.setting_indent_two_space);
        final String fourSpace = getString(R.string.setting_indent_four_space);
        final String eightSpace = getString(R.string.setting_indent_eight_space);
        final String oneTab = getString(R.string.setting_indent_one_tab);
        final String twoTab = getString(R.string.setting_indent_two_tab);
        temp.add(twoSpace);
        temp.add(fourSpace);
        temp.add(eightSpace);
        temp.add(oneTab);
        temp.add(twoTab);

        final List<String> list = temp;
        final CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this)
                .setTitle("Add a reserved keyword")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = list.get(which);
                        String result = "";
                        if (selected.equals(twoSpace)) {
                            result = "  ";
                        } else if (selected.equals(fourSpace)) {
                            result = "    ";
                        } else if (selected.equals(eightSpace)) {
                            result = "        ";
                        } else if (selected.equals(oneTab)) {
                            result = "\t";
                        } else if (selected.equals(twoTab)) {
                            result = "\t\t";
                        }
                        String modifiedJson = JsonManager.modifyJsonByKey(jsonString, "lang_informs", "indent", result);
                        SharedPreferences sp = getSharedPreferences("json", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("profileJson", modifiedJson);
                        editor.apply();
                    }
                })
                .create();
        return dialog;
    }
    private void setLanguageProfileDiretory(String absolutePath){
        ArrayList<String> pathsArray = new ArrayList<>();
        pathsArray.add(absolutePath);
        Set<String> set = new LinkedHashSet<>(pathsArray);
        pathsArray.clear();
        pathsArray.addAll(set);

        SharedPreferences absolutePaths = getSharedPreferences("language_profile_paths", MODE_PRIVATE);
        SharedPreferences.Editor editor = absolutePaths.edit();
        editor.putString("lpp", new Gson().toJson(pathsArray));
        editor.apply();
    }
        ArrayList<String> pathsArray = new ArrayList<>();
    private void addLanguageProfileDirectory(String absolutePath, Boolean isClear){
        SharedPreferences absolutePaths = getSharedPreferences("json", MODE_PRIVATE);

        SharedPreferences.Editor editor = absolutePaths.edit();

        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        String prevJson = absolutePaths.getString("language_profile_paths", "");
        if(!isClear) {
            ArrayList<String> paths = new Gson().fromJson(prevJson, LanguageProfilesPath.class).getPaths();
            for(int i = 0;i<paths.size();i++)
                jArray.put(paths.get(i));
        }
        jArray.put(absolutePath);
        try {
            jObject.put("paths", jArray);
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }
        editor.putString("language_profile_paths", jObject.toString());
        editor.apply();
    }
}
