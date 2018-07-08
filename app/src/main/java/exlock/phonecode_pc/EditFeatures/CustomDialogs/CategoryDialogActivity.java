package exlock.phonecode_pc.EditFeatures;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;

public class CategoryDialogActivity extends Dialog {

    public CategoryDialogActivity(Context context) { super(context); }
    CategoryFunctionAdapter mAdapter;
    RecyclerView mRecyclerView;
    String profileJson;
    LanguageProfile lp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_function_dialog);
        Intent i = getOwnerActivity().getIntent();
        this.profileJson = i.getStringExtra("profileJson");
        this.lp = new LanguageProfile(this.profileJson);
        this.mAdapter = new CategoryFunctionAdapter();
        this.mRecyclerView = findViewById(R.id.CategoryFunctionView);
        this.mRecyclerView.setAdapter(mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();
    }
    public void updateUI(){
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