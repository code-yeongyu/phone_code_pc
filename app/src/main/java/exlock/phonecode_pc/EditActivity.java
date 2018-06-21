package exlock.phonecode_pc;

import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import exlock.phonecode_pc.Tools.ManageCode;


/*Todo:
* change order for all blocks
* change block
* save button
* block finding feature
* category finding feature
* function finding feature
* ui improves for category and menus
*
* shows menu if tempBlockLayout was clicked for a long time :
* change block
* remove block
* add string
* add block below/above
*
* EditText should be located between two related brackets,
* so split between "(" and closest ")" and locate EditText which has the content inside of them there.
*
* if cursor is inside of EditText, add function's value there
*
* change scrollBlocksView's size to 50 if isRequestClose, or 200
*/

public class EditActivity extends AppCompatActivity {
    LinearLayout displayCodeLayout;
    ScrollView scrollBlocksView, scrollCategoriesView;

    int recentlyClickedButton, itemsInDisplayCodeLayout = 0;
    Boolean isMenuOpenedOnce = false;
    String content;

    /*Todo:
    * change order for all blocks
    * save button
    * block finding feature
    * function finding feature
    * ui improves for category and menus
    */

    public void refreshDisplayCodeLayout(){
        itemsInDisplayCodeLayout = 0;
        addItemsInDisplayCodeLayout();
    }
    public void addItemsInDisplayCodeLayout(){
        String[] lines = content.split("\n");

        for(;itemsInDisplayCodeLayout<lines.length;itemsInDisplayCodeLayout++){
            final String functionString = lines[itemsInDisplayCodeLayout];
            LinearLayout tempBlockLayout = new LinearLayout(getApplication());
            /*Todo:
            * shows menu if tempBlockLayout was clicked for a long time :
            * change block
            * remove block
            * add string
            */
            tempBlockLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(getApplication());

            tv.setText(functionString);
            EditText et = new EditText(getApplication());
            /*Todo:
            * add EditText's value next to function inside bracket
            */
            tempBlockLayout.addView(tv);
            tempBlockLayout.addView(et);
            displayCodeLayout.addView(tempBlockLayout);
        }
    }
    private ArrayList<Integer> findStringPositions(String source, String target){
        int position = -300;
        int length = source.length();
        final ArrayList<Integer> positions = new ArrayList<>();

        for(;;){
            position = source.indexOf(target);
            if(position==-1){
                break;
            }
            positions.add(length-source.length()+position);
            source = source.substring(position+1, source.length());
        }
        return positions;
    }
    private ArrayList<Integer> getBracketPairs(ArrayList<Integer> left, ArrayList<Integer> right){
        ArrayList<Integer> pairs = new ArrayList<>();
        left.addAll(right);

        if(left.size()%2==0) {//if it's able to get pairs
            Log.d("getPairs", "passed if");
            Collections.sort(left, new Ascending());
            Log.d("getPairs", "worked fine, results: " + left);
            int leftSize = left.size();
            while(leftSize!=0){
                int temp = (leftSize / 2)-1;
                pairs.add(left.get(temp));
                pairs.add(left.get(temp+1));
                left.remove(temp);
                left.remove(temp);
                leftSize-=2;
            }
            Log.d("getPairs", "worked fine, results: " + pairs);
            return pairs;
        }
        return null;
    }
    private void block(String function){//return type is going to change
        ArrayList<Integer> leftBracketsPositions = findStringPositions(function, "(");
        ArrayList<Integer> rightBracketsPositions = findStringPositions(function, ")");
        ArrayList<Integer> a = getBracketPairs(rightBracketsPositions, leftBracketsPositions);

        TextView functionName = new TextView(getApplication());
        EditText bracket = new EditText(getApplication());
        //split string 'function' with a's values.
        //and display a textView with it.
        //and between a's odd and even, locate 'bracket'
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";
        this.displayCodeLayout = findViewById(R.id.displayCodeLayout);
        final ManageCode mc = new ManageCode(testPath);//only for testing. Directory will be able to change in the future

        this.content = mc.getContent();

        refreshDisplayCodeLayout();

        /*UI stuff*/

        final LinearLayout categoriesLayout = findViewById(R.id.categoriesLayout);//A layout that contains categories
        final LinearLayout functionLayout = findViewById(R.id.functionsLayout);//A layout that contains functions
        final Button closeButton = findViewById(R.id.closeButton);//A button for closing functions menu

        LinearLayout tempCategoriesLayout = new LinearLayout(getApplication());
        //A layout that contains categories button
        final GridLayout functionsListLayout = new GridLayout(getApplication());
        //A layout that contains functions button
        final ArrayList categories = lp.getCategories();
        scrollBlocksView = findViewById(R.id.scrollBlocksView);
        scrollCategoriesView = findViewById(R.id.scrollFunctionsView);
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
            final Button category = new Button(getApplication());
            final int forAssign = i+5000;
            View.OnClickListener categoryOnClickListner = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controlMenu(closeButton, categoriesLayout,true);
                    if(recentlyClickedButton != forAssign) {
                        if(isMenuOpenedOnce){
                            functionsListLayout.removeAllViews();
                            functionLayout.removeAllViews();
                        }
                        recentlyClickedButton=forAssign;
                        isMenuOpenedOnce = true;

                        ArrayList functions = lp.getFunctions(strCategory);//An ArrayList that contains the list of functions
                        for (int j = 0; j < functions.size(); j++) {
                            final String strFunctions = functions.get(j).toString();//An variable that contains a function
                            Button function = new Button(getApplication());
                            function.setText(strFunctions);
                            function.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {//when function clicked
                                /*Todo:
                                * if cursor is inside of EditText, add value there
                                */
                                    content = content + "\n" + lp.getFunctionValue(strCategory, strFunctions);
                                    mc.setContent(content);
                                    addItemsInDisplayCodeLayout();
                                }
                            });
                            functionsListLayout.addView(function);
                        }
                        functionLayout.addView(functionsListLayout);
                    }
                }
            };
            category.setId(forAssign);
            category.setText(strCategory);
            category.setOnClickListener(categoryOnClickListner);//on Button Clicked, load functions
            tempCategoriesLayout.addView(category);
        }
        categoriesLayout.addView(tempCategoriesLayout);
    }
    public void controlMenu(Button closeButton, LinearLayout categoriesLayout, Boolean isRequestClose){
        if(isRequestClose) {
            categoriesLayout.setVisibility(View.GONE);
            closeButton.setVisibility(View.VISIBLE);
            scrollCategoriesView.setVisibility(View.VISIBLE);
        }else{
            categoriesLayout.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
            scrollCategoriesView.setVisibility(View.GONE);
        }
        /*Todo:
        * change scrollBlocksView's size to 50 if isRequestClose, or 200
        */
    }
}

class Ascending implements Comparator<Integer> {

    @Override
    public int compare(Integer o1, Integer o2) {
        return o1.compareTo(o2);
    }

}