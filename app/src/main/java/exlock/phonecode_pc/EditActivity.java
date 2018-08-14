package exlock.phonecode_pc;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import exlock.phonecode_pc.EditFeatures.Block.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.Block.OnStartDragListener;
import exlock.phonecode_pc.EditFeatures.CustomDialog.CategoryDialogActivity;
import exlock.phonecode_pc.Tools.FilePath;
import exlock.phonecode_pc.Tools.LanguageProfileJsonReader;
import exlock.phonecode_pc.Tools.LanguageProfileMember;
import exlock.phonecode_pc.Tools.ManageCode;

public class EditActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ManageCode mc;
    private EditText codeEditor;
    private RecyclerView mRecyclerView;
    private Boolean isBlockMode = true;
    private boolean isPUT_VALUEAdded = false;
    private String workingFilePath = "";

    //Todo: horizontal scroll or new line for long codes in a line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Intent i = getIntent();
        this.workingFilePath = i.getStringExtra("path");
        String[] fileName = this.workingFilePath.split("/");
        toolbar.setTitle(fileName[fileName.length-1]);
        setSupportActionBar(toolbar);

        SharedPreferences jsonSP = getSharedPreferences("json", MODE_PRIVATE);
        String profileJson = jsonSP.getString("profileJson", "");
        if(profileJson.equals("")){
            Toast.makeText(this, getString(R.string.no_language_profile), Toast.LENGTH_SHORT).show();
            i = new Intent(this, SettingActivity.class);
            startActivity(i);
            finish();
        }

        AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);
        AddFloatingActionButton addReservedKeywordButton = findViewById(R.id.addReserved);
        AddFloatingActionButton addObjectButton = findViewById(R.id.addObject);
        LanguageProfileMember lpm = LanguageProfileJsonReader.getProfileMembers(
                jsonSP.getString("profileJson", "")
        );



        final LanguageProfileJsonReader lp;
        if(lpm!=null){
            lp = new LanguageProfileJsonReader(lpm);
            this.mc = new ManageCode(this.workingFilePath, lp);
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

        this.mc.addBracket("(", ")");
        this.mRecyclerView = findViewById(R.id.blocksView);
        this.mRecyclerView.setNestedScrollingEnabled(false);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        BlockAdapter ba = new BlockAdapter(this.mc, dragListener);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.tabs_view);
        navigationView.setNavigationItemSelectedListener(EditActivity.this);
        Menu m = navigationView.getMenu();
        m.add(0, 0, 0, getString(R.string.drawer_add_new_tab));
        this.updateDrawer();
    }
    private void updateDrawer(){
        NavigationView navigationView = findViewById(R.id.tabs_view);
        ArrayList<String> paths = this.getFilePaths();
        Menu m = navigationView.getMenu();
        for(int i = m.size()-1;i<paths.size();i++) {
            int num = m.size();
            String[] fileName = paths.get(i).split("/");
            m.add(0, num, num, fileName[fileName.length-1]);
        }
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.d("id", id+"");

        if (id == 0) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("application/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(i,"Select the file that you want to edit"), 31);
        }else{
            String selectedFile = this.getFilePaths().get(id-1);
            if(!workingFilePath.equals(selectedFile)){
                Intent i = getIntent();
                i.putExtra("path", this.getFilePaths().get(id-1));
                startActivity(i);
                finish();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 31) {
                Uri uri;
                if (resultData != null) {
                    uri = resultData.getData();
                    String[] path = uri.getPath().split(":");
                    String absolutePath = Environment.getExternalStorageDirectory() + "/" + path[1];

                    this.addFile(absolutePath, false);
                    Intent i = getIntent();
                    i.putExtra("path", absolutePath);
                    startActivity(i);
                    finish();
                }

            }
        }
    }
    private void addFile(String absolutePath, Boolean isClear){
        SharedPreferences fileSP = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = fileSP.edit();

        JSONObject pathJObject = new JSONObject();
        JSONArray pathJarray = new JSONArray();

        String prevPath = fileSP.getString("paths", "");

        if(!isClear) {
            ArrayList<String> paths =
                    new Gson().fromJson(prevPath, FilePath.class).getPaths();
            for(int i = 0;i<paths.size();i++) {
                pathJarray.put(paths.get(i));
            }
        }

        pathJarray.put(absolutePath);

        try {
            pathJObject.put("paths", pathJarray);
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }
        editor.putString("paths", pathJObject.toString());
        editor.apply();
    }
    private ArrayList<String> getFilePaths(){
        SharedPreferences absolutePaths = getSharedPreferences("file", MODE_PRIVATE);
        String pathsJson = absolutePaths.getString("paths", "");
        FilePath lpp = new Gson().fromJson(pathsJson, FilePath.class);
        return lpp.getPaths();
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
                    .setTitle("Type the name of the variable")
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
                .setTitle("Type the name of the object")
                .setView(et)
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(mInputMethodManager != null)
                            mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        String final_value = value+" "+et.getText().toString();
                        objectArgumentSetDialog(final_value).show();
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        return dialog;
    }
    private Dialog objectArgumentSetDialog(final String VALUE){
        final EditText et = new EditText(EditActivity.this);
        et.setLines(1);
        et.setSingleLine();
        AlertDialog dialog = new AlertDialog.Builder(EditActivity.this)
                .setTitle("Type the arguments of the object")
                .setView(et)
                .setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(mInputMethodManager != null)
                            mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        String final_value = VALUE+"("+et.getText().toString()+")";
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