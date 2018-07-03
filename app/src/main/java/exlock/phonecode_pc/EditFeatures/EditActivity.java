package exlock.phonecode_pc.EditFeatures;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;
import exlock.phonecode_pc.Tools.StringTools;

import static exlock.phonecode_pc.Tools.StringTools.findStringPositions;

public class EditActivity extends AppCompatActivity {

    ManageCode mc;
    BlockAdapter mAdapter;
    RecyclerView mRecyclerView;
    public void makeBlock(String func, ArrayList<Integer> brackets){
        int aValue = brackets.get(0) + 1;
        int bValue = brackets.get(1);
        makeBlock(func.substring(0, aValue),func.substring(aValue, bValue),func.substring(bValue, func.length()));
    }
    public void makeBlock(String func1, String arg, String func2){
        this.mAdapter.blocks.add(this.mAdapter.getItemCount(),
                new BlockLists().newInstance(
                        func1,arg,func2
                )
        );
        this.mAdapter.notifyDataSetChanged();
    }
    private void addBlock(String function, int line)  {
        //todo: ables user to select what symbols will be replaced with EditTexts

        ArrayList<Integer> dam = findStringPositions(function, ").");
        ArrayList<Integer> brackets = mc.getPairsLine(line);
        if(dam==null||dam.isEmpty()) {
            if (!brackets.isEmpty()) {
                makeBlock(function, brackets);
                return;
            }
        }
        makeBlock(function, "", "");
    }
    //Todo: enable save, improve performances for recycler view

    public void updateUI(){
        mAdapter.blocks.clear();
        String[] lines = mc.getContent().split("\n");
        for(int i = 0;i<lines.length;i++){
            this.addBlock(lines[i], i);
        }
    }
    public void save(){
        String[] lines = mc.getContent().split("\n");
        StringBuilder temp = new StringBuilder();
        for(int i = 0;i<lines.length;i++){
            ArrayList<Integer> a = mc.getPairsLine(i);
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
        mc.setContent(temp.toString());
        mc.saveContent();
    }

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
        mc.addBracket("(", ")");
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
                                                                        String etText = et.getText().toString();
                                                                        String content = mc.getContent();
                                                                        mc.setContent(content+"\n"+etText);
                                                                        addBlock(etText, StringTools.findStringPositions(content, "\n").size()+1);
                                                                        updateUI();
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