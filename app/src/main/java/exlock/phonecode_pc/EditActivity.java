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

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import exlock.phonecode_pc.EditFeatures.Block.BlockLists;
import exlock.phonecode_pc.EditFeatures.CustomDialog.CategoryDialogActivity;
import exlock.phonecode_pc.EditFeatures.SimpleItemTouchHelperCallback;
import exlock.phonecode_pc.Tools.LanguageProfile;
import exlock.phonecode_pc.Tools.ManageCode;

public class EditActivity extends AppCompatActivity {

    final String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";//Todo: remove this if file manager is developed
    ManageCode mc;
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

        this.mc = new ManageCode(this.testPath, lp);//only for testing. Directory will be able to change in the future

        RecyclerView mRecyclerView = findViewById(R.id.blocksView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        mRecyclerView.setAdapter(this.mc.getBlockAdapter());

        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);

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
                mc.save();
                return true;
            case R.id.action_search:
                //todo: search feature with regex
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
}