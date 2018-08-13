package exlock.phonecode_pc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import exlock.phonecode_pc.EditFeatures.Block.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.Block.OnStartDragListener;
import exlock.phonecode_pc.EditFeatures.CustomDialog.CategoryDialogActivity;
import exlock.phonecode_pc.EditFeatures.SimpleItemTouchHelperCallback;
import exlock.phonecode_pc.Tools.LanguageProfile;
import exlock.phonecode_pc.Tools.LanguageProfileJsonReader;
import exlock.phonecode_pc.Tools.LanguageProfileMember;
import exlock.phonecode_pc.Tools.ManageCode;

public class EditActivity extends AppCompatActivity {

    private ManageCode mc;
    private EditText codeEditor;
    private RecyclerView mRecyclerView;
    private Boolean isBlockMode = true;
    private boolean isPUT_VALUEAdded = false;

    //Todo: horizontal scroll or new line for long codes in a line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences jsonSP = getSharedPreferences("json", MODE_PRIVATE);

        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);
        AddFloatingActionButton addReservedKeywordButton = findViewById(R.id.addReserved);
        AddFloatingActionButton addObjectButton = findViewById(R.id.addObject);
        String profileJson = jsonSP.getString("profileJson", "");
        if(profileJson.equals("")){
            Toast.makeText(this, getString(R.string.no_language_profile), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
            finish();
        }
        LanguageProfileMember lpm = LanguageProfileJsonReader.getProfileMembers(
                jsonSP.getString("profileJson", "")
        );
        Intent i = getIntent();
        String path = i.getStringExtra("path");
        final LanguageProfileJsonReader lp;
        if(lpm!=null){
            lp = new LanguageProfileJsonReader(lpm);
            this.mc = new ManageCode(path, lp);
        }else{
            finish();
        }
        this.codeEditor = findViewById(R.id.textEditor);

        ItemTouchHelper.Callback c = this.mc.getCallback();
        this.mc.setTouchHelper(new ItemTouchHelper(c));
        ItemTouchHelper mItemTouchHelper = this.mc.getTouchHelper();

        OnStartDragListener dragListener = new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder vh) {
                mItemTouchHelper.startDrag(vh);
            }
        };
        BlockAdapter ba = new BlockAdapter(this.mc, dragListener);


        this.mc.addBracket("(", ")");
        this.mRecyclerView = findViewById(R.id.blocksView);
        this.mRecyclerView.setNestedScrollingEnabled(false);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));

        this.mc.setBlockAdapter(ba);
        this.mRecyclerView.setAdapter(this.mc.getBlockAdapter());

        this.mc.getTouchHelper().attachToRecyclerView(this.mRecyclerView);

        this.mc.updateUI();

        addBlockButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  CategoryDialogActivity cda = new CategoryDialogActivity(EditActivity.this);
                                                  cda.init(mc);
                                                  cda.show();
                                              }
                                          });
        addCustomBlockButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        customBlockDialog().show();
                                                    }
                                                });
        addReservedKeywordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservedKeywordDialog().show();
            }
        });
        addObjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objectDialog().show();
            }
        });
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
                this.mc.save();
                return true;
            case R.id.action_search:
                this.searchingDialog().show();
                //todo: search feature with regex
                return true;
            case R.id.action_changemode:
                if(this.isBlockMode){
                    this.codeEditor.setText(this.mc.getContent());
                    this.mRecyclerView.setVisibility(View.GONE);
                    this.codeEditor.setVisibility(View.VISIBLE);
                    this.isBlockMode = false;
                    item.setTitle(getString(R.string.action_blockmode));
                }else{
                    this.codeEditor.setVisibility(View.GONE);
                    this.mRecyclerView.setVisibility(View.VISIBLE);
                    this.mc.setContent(this.codeEditor.getText().toString());
                    this.mc.updateUI();
                    this.isBlockMode = true;
                    item.setTitle(getString(R.string.action_textmode));
                }
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
    private Dialog customBlockDialog(){
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
                        mc.setContent(mc.getContent()+"\n"+et.getText().toString());
                        mc.addUIBlock(et.getText().toString());
                        mc.notifyUpdatesInUI();
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        return dialog;
    }
    private Dialog reservedKeywordDialog(){
        ArrayList<String> list = mc.getLanguageProfile().getReserved();
        final CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("Add a reserved keyword")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mc.setContent(mc.getContent()+"\n"+cs[which]);
                        mc.addUIBlock(cs[which].toString());
                        mc.notifyUpdatesInUI();
                    }
                })
                .create();
        return dialog;
    }
    private Dialog objectDialog(){
        ArrayList<String> list = mc.getLanguageProfile().getReservedObject();
        final String PUT_VALUE = "[PUT VALUE IN A VARIABLE]";
        if(!isPUT_VALUEAdded&&mc.getLanguageProfile().getWayToCreateVar().equals("=")){
            list.add(PUT_VALUE);
            isPUT_VALUEAdded = true;
        }//todo: add more conditional states for other languages(this only works in python)
        final CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("Create an object")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = cs[which].toString();
                        objectCreateDialog(value).show();
                    }
                })
                .create();
        return dialog;
    }
    private Dialog objectCreateDialog(@NotNull final String value){
        final EditText et = new EditText(EditActivity.this);
        et.setLines(1);
        et.setSingleLine();
        if(value.equals("[PUT VALUE IN A VARIABLE]")){
            AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                    .setTitle("Type the name of variable")
                    .setView(et)
                    .setPositiveButton("done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            InputMethodManager mInputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if(mInputMethodManager != null)
                                mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                            String final_value = et.getText().toString()+mc.getLanguageProfile().getWayToCreateVar();

                            varValueCreateDialog(final_value).show();
                        }
                    })
                    .setNegativeButton("cancel", null)
                    .create();
            return dialog;
        }
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("Create an object")
                .setView(et)
                .setPositiveButton("create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(mInputMethodManager != null)
                            mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        String final_value = value+" "+et.getText().toString();
                        mc.setContent(mc.getContent()+"\n"+final_value);
                        mc.addUIBlock(final_value);
                        mc.notifyUpdatesInUI();
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        return dialog;
    }
    private Dialog varValueCreateDialog(final String VALUE){
        final EditText et = new EditText(EditActivity.this);
        et.setLines(1);
        et.setSingleLine();
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("Type the value of variable")
                .setView(et)
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(mInputMethodManager != null)
                            mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                            String final_value = VALUE+et.getText().toString();
                            mc.setContent(mc.getContent()+"\n"+final_value);
                            mc.addUIBlock(final_value);
                            mc.notifyUpdatesInUI();
                        }
                })
                .setNegativeButton("cancel", null)
                .create();
        return dialog;
    }
    private Dialog searchingDialog(){
        final EditText et = new EditText(EditActivity.this);
        et.setLines(1);
        et.setSingleLine();
        ArrayList<Integer> positionsOfBlocks = new ArrayList<>();
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("Search")
                .setView(et)
                .setPositiveButton("search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(mInputMethodManager != null)
                            mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);

                        ArrayList<String> lines = mc.getLines();
                        for(i = 0;i<lines.size();i++){
                            if(lines.get(i).contains(et.getText())){
                                positionsOfBlocks.add(i);
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        if(positionsOfBlocks.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Nothing block with that text.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        stringBuilder.append(positionsOfBlocks.get(0)+1);
                        for(i = 1;i<positionsOfBlocks.size();i++){
                            stringBuilder.append(", ").append(positionsOfBlocks.get(1)+1);
                        }
                        Toast.makeText(getApplicationContext(), "Line at: "+stringBuilder.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        return dialog;
    }
}