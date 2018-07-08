package exlock.phonecode_pc.EditFeatures;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.ArrayList;

import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;

public class FunctionDialogActivity extends Dialog {

    public FunctionDialogActivity(Context context) { super(context); }
    CategoryFunctionAdapter mAdapter;
    RecyclerView mRecyclerView;
    String profileJson, categoryName;
    LanguageProfile lp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_function_dialog);
        Intent i = getOwnerActivity().getIntent();
        this.profileJson = i.getStringExtra("profileJson");
        this.categoryName = i.getStringExtra("categoryName");
        this.lp = new LanguageProfile(this.profileJson);
        this.mAdapter = new CategoryFunctionAdapter();
        this.mRecyclerView = findViewById(R.id.CategoryFunctionView);
        this.mRecyclerView.setAdapter(mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();
    }
    public void updateUI(){
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