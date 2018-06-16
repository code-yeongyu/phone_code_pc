package exlock.phonecode_pc;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

import exlock.phonecode_pc.Tools.ManageCode;

public class EditActivity extends AppCompatActivity {

    Boolean isOpenedOnce = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";
        final ManageCode mc = new ManageCode(testPath);//only for testing. Directory will be able to change in the future
        Log.d("Test", "Before: "+mc.getContent());
        mc.setContent("print(\"Hello\")");
        mc.saveContent();
        Log.d("Test", "After: "+mc.getContent());
        /*UI stuff*/
        final LinearLayout displayCodeLayout = findViewById(R.id.displayCodeLayout);
        /*Todo:
        * display code blocks per a line in displayCodeLayout
        */

        final LinearLayout categoriesLayout = findViewById(R.id.categoriesLayout);//A layout that contains categories
        final LinearLayout functionLayout = findViewById(R.id.functionsLayout);//A layout that contains functions
        final Button closeButton = findViewById(R.id.closeButton);//A button for closing functions menu

        LinearLayout tempCategoriesLayout = new LinearLayout(getApplicationContext());
        //A layout that contains categories button
        final GridLayout functionsListLayout = new GridLayout(getApplicationContext());
        //A layout that contains functions button
        final ArrayList categories = lp.getCategories();

        closeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                controlMenu(closeButton, categoriesLayout,false);
            }
        });

        /*functionsListLayout UI set*/
        functionsListLayout.setColumnCount(4);
        functionsListLayout.setBackgroundColor(Color.parseColor("#c4ffed"));
        /*functionsListLayout ui set*/
        /*UI stuff*/
        
        for(int i = 0;i<categories.size();i++){
            final String strCategory = categories.get(i).toString();//A string variable that contains a category's name
            View.OnClickListener categoryOnClickListner = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controlMenu(closeButton, categoriesLayout,true);
                    ArrayList functions = lp.getFunctions(strCategory);//An ArrayList that contains the list of functions
                    for(int j = 0;j<functions.size();j++) {
                        final String strFunctions = functions.get(j).toString();//An variable that contains a function
                        Button function = new Button(getApplicationContext());
                        function.setText(strFunctions);
                        function.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*Todo:
                                 * add block(add Content)
                                 * update displayCodeLayout
                                */

                                Toast.makeText(getApplicationContext(), strFunctions, Toast.LENGTH_SHORT).show();
                            }
                        });
                        functionsListLayout.addView(function);
                    }
                    if(!isOpenedOnce) {
                        functionLayout.addView(functionsListLayout);
                        isOpenedOnce = true;
                    }
                }
            };

            Button category = new Button(getApplicationContext());
            category.setText(strCategory);
            category.setOnClickListener(categoryOnClickListner);//on Button Clicked, load functions
            tempCategoriesLayout.addView(category);
        }
        categoriesLayout.addView(tempCategoriesLayout);
    }
    public void controlMenu(Button closeButton, LinearLayout categoriesLayout, Boolean isRequestClose){
        ScrollView scrollCategoriesLayout = findViewById(R.id.scrollFunctionsLayout);
        if(isRequestClose) {
            categoriesLayout.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
            scrollCategoriesLayout.setVisibility(View.VISIBLE);
        }else{
            categoriesLayout.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
            scrollCategoriesLayout.setVisibility(View.GONE);
        }
    }
}
