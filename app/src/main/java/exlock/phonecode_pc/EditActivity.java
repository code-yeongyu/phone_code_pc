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
    private String workingFilePath = ""; //현재 작업중인 파일 path

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        //ui 코드

        this.codeEditor = findViewById(R.id.textEditor);

        final Intent i = getIntent();

        final SharedPreferences jsonSP = getSharedPreferences("json", MODE_PRIVATE);
        final String profileJson = jsonSP.getString("profileJson", "");
        //language profile 로드
        if(profileJson.equals("")){
            Toast.makeText(this, getString(R.string.no_language_profile), Toast.LENGTH_SHORT).show();
            final Intent SettingActivity = new Intent(this, SettingActivity.class);
            startActivity(SettingActivity);
            finish();
            //language profile 이 로드되지 않았음을 알리고, 액티비티 종료
        }

        this.workingFilePath = i.getStringExtra("path");

        final String[] fileName = this.workingFilePath.split("/");
        toolbar.setTitle(fileName[fileName.length-1]);
        //상단의 toolbar 이름을 작업중인 파일명으로 설정
        setSupportActionBar(toolbar);
        //액션바로 toolbar을 사용하도록 설정

        final AddFloatingActionButton addBlockButton = findViewById(R.id.addBlockButton);
        final AddFloatingActionButton addCustomBlockButton = findViewById(R.id.addCustomBlockButton);
        final AddFloatingActionButton addReservedKeywordButton = findViewById(R.id.addReserved);
        final AddFloatingActionButton addObjectButton = findViewById(R.id.addObject);
        //플로팅 버튼들

        LanguageProfileMember lpm = LanguageProfileJsonReader.getProfileMembers(
                jsonSP.getString("profileJson", "")
        );
        //language profile 안의 parameter 들을 객체로 바꿈

        LanguageProfileJsonReader lp;
        if(lpm!=null){
            lp = new LanguageProfileJsonReader(lpm);
            this.mc = new ManageCode(this.workingFilePath, lp);
        }else{
            Toast.makeText(this, getString(R.string.no_language_profile), Toast.LENGTH_SHORT).show();
            final Intent SettingActivity = new Intent(this, SettingActivity.class);
            startActivity(SettingActivity);
            finish();
            //language profile 이 로드되지 않았음을 알리고, 액티비티 종료
        }

        final ItemTouchHelper.Callback c = this.mc.getCallback(); // recyclerview의 터치 관련 작업을 위한 콜백
        this.mc.setTouchHelper(new ItemTouchHelper(c));
        final ItemTouchHelper mItemTouchHelper = this.mc.getTouchHelper();

        final OnStartDragListener dragListener = new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder vh) {
                mItemTouchHelper.startDrag(vh);
            }
        }; // recyclerview에 드래그가 시작될때 작업 설정

        this.mc.addBracket("(", ")"); // edittext를 사이에 넣도록 설정 할 문자 두가지 설정
        this.mRecyclerView = findViewById(R.id.blocksView);
        this.mRecyclerView.setNestedScrollingEnabled(false); //horizontal scroll 금지
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication())); // 리니어 레이아웃으로 recyclerview 설정
        final BlockAdapter ba = new BlockAdapter(this.mc, dragListener);
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
        //버튼 작업시 리스너

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
        //햄버거 버튼 클릭시 나오는 좌측 메뉴 설정

    }
    private void updateDrawer(){
        final NavigationView navigationView = findViewById(R.id.tabs_view);
        final ArrayList<String> paths = this.getFilePaths();
        final Menu m = navigationView.getMenu();
        for(int i = m.size()-1;i<paths.size();i++) {
            final int num = m.size();
            String[] fileName = paths.get(i).split("/");
            m.add(0, num, num, fileName[fileName.length-1]);
        }
    }
    // drawer 에 최근에 작업했던 파일 목록을 업데이트 하는 함수
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        final MenuInflater inflater = getMenuInflater();
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
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        if (id == 0) { // 0번째 항목은 새 파일을 여는 항목
            final Intent i = new Intent();
            i.setAction(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("application/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(i,"Select the file that you want to edit"), 31);
        }else{ // 기존에 열었었던 파일을 열려고 시도하는 경우
            String selectedFile = this.getFilePaths().get(id-1);
            if(!workingFilePath.equals(selectedFile)){
                final Intent i = getIntent();
                i.putExtra("path", this.getFilePaths().get(id-1)); // n번째 파일을 path에 넣어서
                startActivity(i); // 새 activity 를 시작하고
                finish(); // 끝낸다.
            }
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                    final String[] path = uri.getPath().split(":");
                    final String absolutePath = Environment.getExternalStorageDirectory() + "/" + path[1];

                    this.addFile(absolutePath);
                    final Intent i = getIntent();
                    i.putExtra("path", absolutePath);
                    startActivity(i);
                    finish();
                }

            }
        }
    }
    private void addFile(String absolutePath){
        SharedPreferences fileSP = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = fileSP.edit();
        JSONObject pathJObject = new JSONObject();
        String prevPath = fileSP.getString("paths", "");
        ArrayList<String> paths =
                new Gson().fromJson(prevPath, FilePath.class).getPaths();
        JSONArray pathJarray = new JSONArray(paths);

        pathJarray.put(absolutePath); //jsonArray에 새 파일의 path 추가

        try {
            pathJObject.put("paths", pathJarray); //pathJObject에 array pathJarray 추가
        }catch(JSONException e){
            e.printStackTrace();
            return;
        }
        editor.putString("paths", pathJObject.toString());
        editor.apply();//paths라는 이름으로 sharedpreference에 추가
    }
    private ArrayList<String> getFilePaths(){
        SharedPreferences absolutePaths = getSharedPreferences("file", MODE_PRIVATE);
        String pathsJson = absolutePaths.getString("paths", "");
        FilePath lpp = new Gson().fromJson(pathsJson, FilePath.class);
        return lpp.getPaths();
    }
    // 열려있는 파일들의 arraylist를 반환하는 함

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
        final ArrayList<String> list = mc.getLanguageProfile().getReserved();
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
        }
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
    // 버튼 누를시 나오는 dialog를 return 하는 함수
}
