package exlock.phonecode_pc.EditFeatures.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;

import java.lang.reflect.Array;
import java.util.ArrayList;

import exlock.phonecode_pc.Tools.JsonManager;
import exlock.phonecode_pc.Tools.LanguageProfile;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.LanguageProfileMember;
import exlock.phonecode_pc.Tools.ManageCode;

public class FunctionDialogActivity extends Dialog {
    private FunctionAdapter mAdapter;
    private String categoryName;
    private LanguageProfile lp;
    private ManageCode mc;
    private int line = -1;

    FunctionDialogActivity(Context context) { super(context); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_function_dialog);

        RecyclerView mRecyclerView = findViewById(R.id.CategoryFunctionView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        updateUI();
    }
    void init(String profileJson, String categoryName, ManageCode mc){
        LanguageProfileMember lpm = LanguageProfile.getProfileMembers(profileJson);
        if(lpm!=null)
            this.lp = new LanguageProfile(lpm);
        this.categoryName = categoryName;
        this.mAdapter = new FunctionAdapter();
        this.mc = mc;
        this.mAdapter.init(mc, this);
    }
    void init(String profileJson, String categoryName, ManageCode mc, int line){
        LanguageProfileMember lpm = LanguageProfile.getProfileMembers(profileJson);
        if(lpm!=null)
            this.lp = new LanguageProfile(lpm);
        this.categoryName = categoryName;
        this.mAdapter = new FunctionAdapter();
        this.line = line;
        this.mc = mc;
        this.mAdapter.init(mc, this, this.line);
    }
    private void updateUI(){
        this.mAdapter.lists.clear();
        if(this.categoryName.equals("used functions")){
            ArrayList<String> funcName = this.mc.getFunctionsInCode();
            for(int i = 0;i<funcName.size();i++){
                this.mAdapter.lists.add(this.mAdapter.getItemCount(),
                        new CategoryFunctionLists().newInstance(
                                funcName.get(i)
                        )
                );
            }
            return;
        }
        ArrayList<String> functions = lp.getFunctions(this.categoryName);
        for(int i = 0;i<functions.size();i++){
            this.mAdapter.lists.add(this.mAdapter.getItemCount(),
                    new CategoryFunctionLists().newInstance(
                            functions.get(i)
                    )
            );
        }
        this.mAdapter.notifyDataSetChanged();
    }
}