package exlock.phonecode_pc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exlock.phonecode_pc.EditFeatures.Block.BlockLists;
import exlock.phonecode_pc.EditFeatures.CustomDialog.CategoryDialogActivity;
import exlock.phonecode_pc.EditFeatures.SimpleItemTouchHelperCallback;
import exlock.phonecode_pc.Tools.LanguageProfile;
import exlock.phonecode_pc.Tools.ManageCode;

public class EditActivity extends AppCompatActivity {

    final String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";//Todo: remove this if file manager is developed
    ManageCode mc;
    EditText codeEditor;
    RecyclerView mRecyclerView;
    Boolean isBlockmode = true;
    //Todo: horizontal scroll or new line for long codes in a line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", "")
        );
        this.codeEditor = findViewById(R.id.textEditor);

        this.mc = new ManageCode(this.testPath, lp);//only for testing. Directory will be able to change in the future

        this.mRecyclerView = findViewById(R.id.blocksView);
        this.mRecyclerView.setNestedScrollingEnabled(false);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        this.mRecyclerView.setAdapter(this.mc.getBlockAdapter());

        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);
        AddFloatingActionButton addReservedKeywordButton = findViewById(R.id.addReserved);

        this.mc.addBracket("(", ")");

        this.mc.updateUI();

        this.mc.getTouchHelper().attachToRecyclerView(mRecyclerView);


        addBlockButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  CategoryDialogActivity cda = new CategoryDialogActivity(EditActivity.this);
                                                  cda.init(mc);
                                                  cda.show();
                                              }
                                          }
        );
        addCustomBlockButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        customBlockDialog().show();
                                                    }
                                                }
        );
        addReservedKeywordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservedKeywordDialog().show();
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
                //todo: search feature with regex
                return true;
            case R.id.action_changemode:
                if(this.isBlockmode){
                    this.codeEditor.setText(this.mc.getContent());
                    this.mRecyclerView.setVisibility(View.GONE);
                    this.codeEditor.setVisibility(View.VISIBLE);
                    this.isBlockmode = false;
                    item.setTitle(getString(R.string.action_blockmode));
                }else{
                    this.codeEditor.setVisibility(View.GONE);
                    this.mRecyclerView.setVisibility(View.VISIBLE);
                    this.mc.setContent(this.codeEditor.getText().toString());
                    this.mc.updateUI();
                    this.isBlockmode = true;
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
}