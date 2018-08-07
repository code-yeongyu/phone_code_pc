package exlock.phonecode_pc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;

import exlock.phonecode_pc.Tools.JsonManager;
import exlock.phonecode_pc.Tools.LanguageProfile;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        TextView indentSetting = findViewById(R.id.settingIndentTextView);
        TextView profileSetting = findViewById(R.id.settingLanguageProfileTextView);
        indentSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "On development", Toast.LENGTH_SHORT).show();
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
}
