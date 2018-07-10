package exlock.phonecode_pc.EditFeatures.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.ArrayList;

import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

public class FunctionDialogActivity extends Dialog {
    private FunctionAdapter mAdapter;
    private String categoryName;
    private LanguageProfile lp;

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
        this.lp = new LanguageProfile(
                profileJson
        );
        this.categoryName = categoryName;
        this.mAdapter = new FunctionAdapter();
        this.mAdapter.init(mc, categoryName, this);
    }
    private void updateUI(){
        this.mAdapter.lists.clear();
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