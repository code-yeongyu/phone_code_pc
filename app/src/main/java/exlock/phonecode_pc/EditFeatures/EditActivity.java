package exlock.phonecode_pc;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.Bundle;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import exlock.phonecode_pc.Tools.ManageCode;

public class EditActivity extends AppCompatActivity {

    int recentlyClickedButton, itemsInDisplayCodeLayout = 0, numOfEditTexts = 0;
    Boolean isMenuOpenedOnce = false;
    String content;
    ManageCode mc;
    final int editTextInBlockID = 100;
    final int categoryButtonID = 200;
    /*Todo: overall
    * change order for all blocks(change from linearlayout to recyclerview)
    * block finding feature in action bar menu
    * material floating button
    * -> add functions -> onClicked -> categories menu with searching -> functions menu with searching
    */

    public void save(){
        String[] lines = content.split("\n");
        StringBuilder temp = new StringBuilder();
        int addedEditTexts = 0;
        for(int i = 0;i<lines.length;i++){
            ArrayList<Integer> leftBracketsPositions = findStringPositions(lines[i], "(");
            ArrayList<Integer> rightBracketsPositions = findStringPositions(lines[i], ")");
            ArrayList<Integer> a = getBracketPairs(rightBracketsPositions, leftBracketsPositions);
            if(a!=null) {
                if(!a.isEmpty()) {
                    EditText et = findViewById(editTextInBlockID+addedEditTexts);
                    Collections.reverse(a);
                    int aValue = a.get(0) + 1;
                    int functionLength = lines[i].length();
                    temp.append(
                            lines[i].substring(0, aValue))
                            .append(et.getText())
                            .append(lines[i].substring(a.get(1), functionLength));
                    addedEditTexts++;
                }else{
                    temp.append(lines[i]);
                }
                temp.append("\n");
            }
        }
        mc.setContent(temp.toString());
        mc.saveContent();
    }
    public void addItemsInDisplayCodeLayout(LinearLayout displayCodeLayout){
        String[] lines = content.split("\n");

        for(;itemsInDisplayCodeLayout<lines.length;itemsInDisplayCodeLayout++){
            final String functionString = lines[itemsInDisplayCodeLayout];
            displayCodeLayout.addView(this.block(functionString));
        }
    }
    public void refreshDisplayCodeLayout(LinearLayout displayCodeLayout){
        itemsInDisplayCodeLayout = 0;
        addItemsInDisplayCodeLayout(displayCodeLayout);
    }
    private ArrayList<Integer> findStringPositions(String source, String target){
        int position;
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
    @Nullable
    private ArrayList<Integer> getBracketPairs(ArrayList<Integer> left, ArrayList<Integer> right){
        ArrayList<Integer> pairs = new ArrayList<>();
        left.addAll(right);

        if(left.size()%2==0) {//if it's able to get pairs
            Collections.sort(left, new Descending());
            int leftSize = left.size();
            while(leftSize!=0){
                int temp = (leftSize / 2)-1;
                pairs.add(left.get(temp));
                pairs.add(left.get(temp+1));
                left.remove(temp);
                left.remove(temp);
                leftSize-=2;
            }
            return pairs;
        }
        return null;
    }


    private LinearLayout block(String function){
        LinearLayout temp = new LinearLayout(getApplication());
        temp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /*Todo: add code about popup
                *[]---------------[]
                * remove block -> clicked -> a function that removes block
                * add block -> clicked -> category popup menu
                * add string front -> clicked -> an EditText popup
                * add string behind -> clicked -> an EditText popup
                * change block -> clicked -> category popup menu
                *[]---------------[]
                */
                return true;
            }
        });
        temp.setOrientation(LinearLayout.HORIZONTAL);
        ArrayList<Integer> leftBracketsPositions = findStringPositions(function, "(");
        ArrayList<Integer> rightBracketsPositions = findStringPositions(function, ")");
        ArrayList<Integer> a = getBracketPairs(rightBracketsPositions, leftBracketsPositions);

        if(a!=null) {
            TextView tv1 = new TextView(getApplication()), tv2 = new TextView(getApplication());
            tv1.setTextSize(20);
            if(!a.isEmpty()) {
                Collections.reverse(a);
                int aValue = a.get(0) + 1;
                int functionLength = function.length();
                EditText et = new EditText(getApplication());
                et.setId(editTextInBlockID+numOfEditTexts);
                numOfEditTexts+=1;
                tv2.setTextSize(20);
                tv1.setText(function.substring(0, aValue));
                et.setText(function.substring(aValue, functionLength - 1));
                tv2.setText(function.substring(a.get(1), functionLength));
                temp.addView(tv1);
                temp.addView(et);
                temp.addView(tv2);
            }else{
                tv1.setText(function);
                temp.addView(tv1);
            }
        }
        return temp;
    }//not done yet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";
        final LinearLayout displayCodeLayout = findViewById(R.id.displayCodeLayout);
        mc = new ManageCode(testPath);//only for testing. Directory will be able to change in the future
        this.content = mc.getContent();

        refreshDisplayCodeLayout(displayCodeLayout);

        /*UI stuff*/


        LinearLayout tempCategoriesLayout = new LinearLayout(getApplication());
        //A layout that contains categories button
        final GridLayout functionsListLayout = new GridLayout(getApplication());
        //A layout that contains functions button
        final ArrayList categories = lp.getCategories();

        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);

        /*functionsListLayout UI set*/
        functionsListLayout.setColumnCount(4);
        functionsListLayout.setBackgroundColor(Color.parseColor("#c4ffed"));
        /*functionsListLayout ui set*/
        /*UI stuff*/
        addBlockButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  //popup activity
                                              }
                                          }
            );
        addCustomBlockButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  //popup activity
                                              }
                                          }
        );
        /*
        for(int i = 0;i<categories.size();i++){
            final String strCategory = categories.get(i).toString();//A string variable that contains a category's name
            final Button category = new Button(getApplication());
            final int forAssign = i+5000;
            View.OnClickListener categoryOnClickListner = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                //Todo: if cursor is inside of EditText, add value there
                                    content = content + "\n" + lp.getFunctionValue(strCategory, strFunctions);
                                    mc.setContent(content);
                                    addItemsInDisplayCodeLayout(displayCodeLayout);
                                }
                            });
                            functionsListLayout.addView(function);
                        }
                        functionLayout.addView(functionsListLayout);
                    }
                }
            };
            category.setId(categoryButtonID+forAssign);
            category.setText(strCategory);
            category.setOnClickListener(categoryOnClickListner);//on Button Clicked, load functions
            tempCategoriesLayout.addView(category);
        }
        categoriesLayout.addView(tempCategoriesLayout);
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_save:
                //mc.saveContent();
                this.save();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }//not done yet
}

class Descending implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o2.compareTo(o1);
    }
}