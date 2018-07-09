package exlock.phonecode_pc.EditFeatures.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import exlock.phonecode_pc.EditFeatures.BlockAdapter;
import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

public class CategoryDialogActivity extends Dialog {
    private CategoryAdapter mAdapter;
    private LanguageProfile lp;

    public CategoryDialogActivity(Context context) { super(context); }
    public void init(String profileJson, ManageCode mc, BlockAdapter ba){
        this.mAdapter = new CategoryAdapter();
        this.lp = new LanguageProfile(
                profileJson
        );
        this.mAdapter.init(mc, ba, this);
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

        for(int i = 0;i<lp.getCategories().size();i++){
            this.mAdapter.lists.add(this.mAdapter.getItemCount(),
                    new CategoryFunctionLists().newInstance(
                            this.lp.getCategories().get(i)
                    )
            );
        }
        this.mAdapter.notifyDataSetChanged();
    }
}