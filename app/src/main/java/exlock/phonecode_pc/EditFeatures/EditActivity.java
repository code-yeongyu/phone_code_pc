package exlock.phonecode_pc.EditFeatures;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

    String content;
    ManageCode mc;
    BlockAdapter mAdapter;
    RecyclerView mRecyclerView;
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
    public void makeBlock(String func, ArrayList<Integer> brackets){
        int aValue = brackets.get(0) + 1;
        int bValue = brackets.get(1);
        makeBlock(func.substring(0, aValue),func.substring(aValue, bValue),func.substring(bValue, func.length()));
    }
    public void makeBlock(String func1, String arg, String func2){
        Log.d("fun", func1+arg+func2);
        this.mAdapter.blocks.add(this.mAdapter.getItemCount(),
                new BlockLists().newInstance(
                        func1,arg,func2
                )
        );
        this.mAdapter.notifyDataSetChanged();
    }
    private void addBlock(String function){
        //todo: ables user to select what symbols will be replaced with EditTexts

        ArrayList<Integer> dam = findStringPositions(function, ").");
        ArrayList<Integer> leftCurlyBracketsPositions = findStringPositions(function, "(");
        ArrayList<Integer> rightCurlyBracketsPositions = findStringPositions(function, ")");
        ArrayList<Integer> curlyBrackets = getBracketPairs(rightCurlyBracketsPositions, leftCurlyBracketsPositions);

        //ArrayList<Integer> leftSquareBracketsPositions = findStringPositions(function, "[");
        //ArrayList<Integer> rightSquareBracketsPositions = findStringPositions(function, "]");
        //ArrayList<Integer> sqaureBrackets = getBracketPairs(rightSquareBracketsPositions, leftSquareBracketsPositions);
        if(dam==null||dam.isEmpty()) {
            if (curlyBrackets != null) {
                if (!curlyBrackets.isEmpty()) {
                    Collections.reverse(curlyBrackets);
                    makeBlock(function, curlyBrackets);
                    return;
                    //}else if(sqaureBrackets!=null&&!sqaureBrackets.isEmpty()) {
                    //    Collections.reverse(sqaureBrackets);
                    //    makeBlock(function, curlyBrackets);
                }
            }
        }
        makeBlock(function, "", "");
    }
    //Todo: enable save, improve performances for recycler view

    public void updateUI(){
        mAdapter.blocks.clear();
        String[] lines = content.split("\n");
        for (String line : lines) {
            this.addBlock(line);
        }
    }
    public void save(){
        String[] lines = content.split("\n");
        StringBuilder temp = new StringBuilder();
        for(int i = 0;i<lines.length;i++){
            ArrayList<Integer> leftBracketsPositions = findStringPositions(lines[i], "(");
            ArrayList<Integer> rightBracketsPositions = findStringPositions(lines[i], ")");
            ArrayList<Integer> a = getBracketPairs(rightBracketsPositions, leftBracketsPositions);
            if(a!=null) {
                if(!a.isEmpty()) {
                    Collections.reverse(a);
                    int aValue = a.get(0) + 1;
                    int functionLength = lines[i].length();
                    temp.append(
                            lines[i].substring(0, aValue))
                            .append(mAdapter.blocks.get(i).arg)
                            .append(lines[i].substring(a.get(1), functionLength));
                }else{
                    temp.append(lines[i]);
                }
                temp.append("\n");
            }
        }
        mc.setContent(temp.toString());
        mc.saveContent();
    }//todo: needs update for recyclerview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mAdapter = new BlockAdapter();
        mRecyclerView = findViewById(R.id.blocksView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        mRecyclerView.setAdapter(mAdapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);

        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";
        mc = new ManageCode(testPath);//only for testing. Directory will be able to change in the future
        this.content = mc.getContent();
        updateUI();

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
                                                                        addBlock(et.getText().toString());
                                                                    }
                                                                })
                                                                .setNegativeButton("cancel", null)
                                                                .create();
                                                        dialog.show();
                                                    }
                                                }
        );
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
