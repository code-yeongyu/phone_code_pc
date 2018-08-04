package exlock.phonecode_pc.EditFeatures.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;

import java.util.ArrayList;

import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

public class CategoryDialogActivity extends Dialog {
    private CategoryAdapter mAdapter;
    private ManageCode mc;

    public CategoryDialogActivity(Context context) { super(context); }
    public void init(ManageCode mc){
        this.mAdapter = new CategoryAdapter();
        this.mc = mc;
        this.mAdapter.init(mc, this);
    }
    public void init(ManageCode mc, int line){
        this.mAdapter = new CategoryAdapter();
        this.mc = mc;
        this.mAdapter.init(mc, this, line);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_function_dialog);

        RecyclerView mRecyclerView = findViewById(R.id.CategoryFunctionView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(this.mAdapter);

        this.mAdapter.lists.clear();
        ArrayList<String> categories = this.mc.getLanguageProfile().getCategories();
        ArrayList<String> funcs = mc.getFunctionsInCode();

        if(!funcs.isEmpty()){
            this.mAdapter.lists.add(new CategoryFunctionLists().newInstance("used functions"));
        }
        for(int i = 0;i<categories.size();i++){
            this.mAdapter.lists.add(this.mAdapter.getItemCount(),
                    new CategoryFunctionLists().newInstance(
                            categories.get(i)
                    )
            );
        }

        this.mAdapter.notifyDataSetChanged();//update ui
    }
}