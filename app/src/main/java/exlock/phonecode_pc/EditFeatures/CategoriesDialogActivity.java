package exlock.phonecode_pc.EditFeatures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;

public class CategoriesDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_dialog);
        final LinearLayout functionsView= findViewById(R.id.functionsView);
        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        final ArrayList categories = lp.getCategories();
        Log.d("dd", lp.getCategories()+"");
        for (int i = 0; i < categories.size(); i++) {
            final String strCategory = categories.get(i).toString();//A string variable that contains a category's name
            Log.d("dd", strCategory+"hi");
            TextView tv = new TextView(getApplication());
            tv.setText(strCategory);
            tv.setTextSize(24);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lp.getFunctions(strCategory);
                    Intent i1 = new Intent(getApplication(), FunctionsDialogActivity.class);
                    i1.putExtra("strCategory", strCategory);
                    startActivity(i1);
                }
            });
            View v = new View(getApplication());
            v.setMinimumHeight(1);
            v.setBackgroundColor(0xBBBBBB);
            functionsView.addView(tv);
        }
    }
}