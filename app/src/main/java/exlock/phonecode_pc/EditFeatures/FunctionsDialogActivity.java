package exlock.phonecode_pc.EditFeatures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;

public class FunctionsDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_dialog);
        Intent intent = getIntent();
        final LinearLayout functionsView= findViewById(R.id.functionsView);
        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        final ArrayList categories =
                lp.getFunctions(intent.getStringExtra("strCategory"));

        for (int i = 0; i < categories.size(); i++) {
            final String strCategory = categories.get(i).toString();//A string variable that contains a category's name
            TextView tv = new TextView(getApplication());
            tv.setText(strCategory);
            tv.setTextSize(24);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplication(), strCategory, Toast.LENGTH_SHORT).show();
                }
            });
            View v = new View(getApplication());
            v.setMinimumHeight(1);
            v.setBackgroundColor(0xBBBBBB);
            functionsView.addView(tv);
        }
    }
}
