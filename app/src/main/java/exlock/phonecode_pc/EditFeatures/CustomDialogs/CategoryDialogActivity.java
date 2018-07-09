package exlock.phonecode_pc.EditFeatures.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.ArrayList;

import exlock.phonecode_pc.Tools.ManageUIBlocks;
import exlock.phonecode_pc.R;

public class CategoryDialogActivity extends Dialog {
    private CategoryAdapter mAdapter;
    private ManageUIBlocks mub;

    public CategoryDialogActivity(Context context) { super(context); }
    public void init(ManageUIBlocks mub){
        this.mAdapter = new CategoryAdapter();
        this.mAdapter.init(mub, this);
        this.mub = mub;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_function_dialog);

        RecyclerView mRecyclerView = findViewById(R.id.CategoryFunctionView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        updateUI();
    }
    private void updateUI(){
        this.mAdapter.lists.clear();
        ArrayList<String> categories = this.mub.getLanguageProfile().getCategories();
        for(int i = 0;i<categories.size();i++){
            this.mAdapter.lists.add(this.mAdapter.getItemCount(),
                    new CategoryFunctionLists().newInstance(
                            categories.get(i)
                    )
            );
        }
        this.mAdapter.notifyDataSetChanged();
    }
}