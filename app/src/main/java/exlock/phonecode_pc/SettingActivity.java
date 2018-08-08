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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import exlock.phonecode_pc.Tools.JsonManager;
import exlock.phonecode_pc.Tools.LanguageProfileJsonReader;
import exlock.phonecode_pc.Tools.LanguageProfileMember;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final TextView indentSetting = findViewById(R.id.settingIndentTextView);
        TextView profileSetting = findViewById(R.id.settingLanguageProfileTextView);

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
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == 43 && resultCode == Activity.RESULT_OK) {
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
                String json = JsonManager.getJsonFromPath(Environment.getExternalStorageDirectory() + path[1]);
                if (LanguageProfile.getProfileMembers(json) == null) {
                    Toast.makeText(SettingActivity.this, getString(R.string.toast_wrong_json), Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sp = getSharedPreferences("json", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("profileJson", json);
                editor.apply();
            }
        }
    }
    private Dialog indentSetDialog(final String jsonString){
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
                        if(selected.equals(twoSpace)){
                            result = "  ";
                        }else if(selected.equals(fourSpace)){
                            result = "    ";
                        }else if(selected.equals(eightSpace)){
                            result = "        ";
                        }else if(selected.equals(oneTab)){
                            result = "\t";
                        }else if(selected.equals(twoTab)){
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
}
