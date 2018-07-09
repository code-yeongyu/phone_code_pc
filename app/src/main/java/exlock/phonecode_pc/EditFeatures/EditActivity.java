package exlock.phonecode_pc.EditFeatures;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

import exlock.phonecode_pc.EditFeatures.CustomDialogs.CategoryDialogActivity;
import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;
import exlock.phonecode_pc.Tools.StringTools;

import static exlock.phonecode_pc.Tools.StringTools.findStringPositions;

public class EditActivity extends AppCompatActivity {

    final String testPath = Environment.getExternalStorageDirectory() + "/PhoneCode/hello_world.py";//Todo: remove this if file manager is developed
    ManageCode mc;
    BlockAdapter mAdapter;
    RecyclerView mRecyclerView;
    //Todo: horizontal scroll or new line for long codes in a line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAdapter = new BlockAdapter();
        mRecyclerView = findViewById(R.id.blocksView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        mRecyclerView.setAdapter(mAdapter);

        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);

        final LanguageProfile lp = new LanguageProfile(
                getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));

        this.mc = new ManageCode(this.testPath);//only for testing. Directory will be able to change in the future
        this.mc.addBracket("(", ")");

        final ManageUIBlocks mub = new ManageUIBlocks(this.mAdapter, this.mc, lp);

        mub.updateUI();
        addBlockButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  CategoryDialogActivity cda = new CategoryDialogActivity(EditActivity.this);
                                                  cda.init(mub);
                                                  cda.show();
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
                                                                        mub.addBlock(etText);
                                                                    }
                                                                })
                                                                .setNegativeButton("cancel", null)
                                                                .create();
                                                        dialog.show();
                                                    }
                                                }
        );//Todo: seperate dialogs to another class
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
                mc.save(this.mAdapter.blocks);
                return true;
            case R.id.action_search:
                //todo: search feature with regex
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
}