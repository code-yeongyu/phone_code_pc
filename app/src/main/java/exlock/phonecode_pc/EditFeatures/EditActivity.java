package exlock.phonecode_pc.EditFeatures;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.Bundle;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

public class EditActivity extends AppCompatActivity {

    int recentlyClickedButton, itemsInDisplayCodeLayout = 0, numOfEditTexts = 0;
    Boolean isMenuOpenedOnce = false;
    String content;
    ManageCode mc;
    final int editTextInBlockID = 100;
    final int categoryButtonID = 200;

    /*todo: overall
    * change order for all blocks(change from linearlayout to recyclerview)
    * material floating button
    * -> add functions -> onClicked -> categories menu with searching -> functions menu with searching
    * select continuous lines from line x to line y and edit them together
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
    }//todo: update for recyclerview
    public void refreshDisplayCodeLayout(LinearLayout displayCodeLayout){
        itemsInDisplayCodeLayout = 0;
        addItemsInDisplayCodeLayout(displayCodeLayout);
    }//todo: update for recyclerview
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
    public LinearLayout makeBlock(String function, ArrayList<Integer> brackets){
        int aValue = brackets.get(0) + 1;
        int bValue = brackets.get(1);

        LinearLayout temp = new LinearLayout(getApplication());
        temp.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv1 = new TextView(getApplication()), tv2 = new TextView(getApplication());
        EditText et = new EditText(getApplication());


        tv1.setTextSize(20);
        tv1.setText(function.substring(0, aValue));
        tv2.setTextSize(20);
        tv2.setText(function.substring(bValue, function.length()));
        et.setLines(1);
        et.setSingleLine();
        et.setId(editTextInBlockID+numOfEditTexts);
        numOfEditTexts+=1;
        et.setText(function.substring(aValue, bValue));
        temp.addView(tv1);
        temp.addView(et);
        temp.addView(tv2);
        return temp;
    }//todo: update for recyclerview
    public LinearLayout makeBlock(String function){
        LinearLayout temp = new LinearLayout(getApplication());
        temp.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv1 = new TextView(getApplication());
        tv1.setTextSize(20);
        tv1.setText(function);
        temp.addView(tv1);
        return temp;
    }//todo: update for recyclerview
    private LinearLayout block(String function){
        //todo: ables user to select what symbols will be replaced with EditTexts
        ArrayList<Integer> leftCurlyBracketsPositions = findStringPositions(function, "(");
        ArrayList<Integer> rightCurlyBracketsPositions = findStringPositions(function, ")");
        ArrayList<Integer> curlyBrackets = getBracketPairs(rightCurlyBracketsPositions, leftCurlyBracketsPositions);

        ArrayList<Integer> leftSquareBracketsPositions = findStringPositions(function, "[");
        ArrayList<Integer> rightSquareBracketsPositions = findStringPositions(function, "]");
        ArrayList<Integer> sqaureBrackets = getBracketPairs(rightSquareBracketsPositions, leftSquareBracketsPositions);


        if(curlyBrackets!=null) {
            if(!curlyBrackets.isEmpty()) {
                Collections.reverse(curlyBrackets);
                return makeBlock(function, curlyBrackets);
            }else if(sqaureBrackets!=null&&!sqaureBrackets.isEmpty()) {
                Collections.reverse(sqaureBrackets);
                return makeBlock(function, sqaureBrackets);
            }
        }
        return makeBlock(function);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final LinearLayout displayCodeLayout = findViewById(R.id.displayCodeLayout);
        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);

        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";
        mc = new ManageCode(testPath);//only for testing. Directory will be able to change in the future
        this.content = mc.getContent();

        refreshDisplayCodeLayout(displayCodeLayout);

        //A layout that contains functions button
        final ArrayList categories = lp.getCategories();


        addBlockButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  //todo: custom dialog activity
                                              }
                                          }
            );
        addCustomBlockButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  final EditText et = new EditText(EditActivity.this);
                                                  et.setLines(1);
                                                  et.setSingleLine();
                                                  AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                                                          .setTitle("Add a custom Block")
                                                          .setMessage("What do you want to add?")
                                                          .setView(et)
                                                          .setPositiveButton("add", new DialogInterface.OnClickListener() {
                                                              @Override
                                                              public void onClick(DialogInterface dialogInterface, int i) {
                                                                  InputMethodManager mInputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                  if(mInputMethodManager != null)
                                                                      mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);

                                                                  content = content + "\n" + et.getText();
                                                                  mc.setContent(content);
                                                                  addItemsInDisplayCodeLayout(displayCodeLayout);
                                                              }
                                                          })
                                                          .setNegativeButton("cancel", null)
                                                          .create();
                                                  dialog.show();
                                              }
                                          }
        );
        /*todo: refactor for dialog activity
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
                                //todo: if cursor is inside of EditText, add value there
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
                this.save();
                return true;
            case R.id.action_search:
                //todo: search feature with regex
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
}

class Descending implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o2.compareTo(o1);
    }
}