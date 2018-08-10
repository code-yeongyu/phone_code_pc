package exlock.phonecode_pc.EditFeatures.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import exlock.phonecode_pc.EditActivity;
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
        Button searchButton = findViewById(R.id.searchButton);
        ArrayList<String> categories = this.mc.getLanguageProfile().getCategories();

        ArrayList<Integer> positionsOfCategories = new ArrayList<>();
        ArrayList<String> funcsInCode = mc.getFunctionsInCode();
        searchButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                EditText searchEditText = findViewById(R.id.searchEditText);
                                                String query = searchEditText.getText().toString();
                                                if(query.equals("")){
                                                    mAdapter.lists.clear();
                                                    if(!funcsInCode.isEmpty()){
                                                        mAdapter.lists.add(new CategoryFunctionLists().newInstance("used functions"));
                                                    }
                                                    for(int i = 0;i<categories.size();i++){
                                                        mAdapter.lists.add(
                                                                new CategoryFunctionLists().newInstance(
                                                                        categories.get(i)
                                                                )
                                                        );
                                                    }
                                                    mAdapter.notifyDataSetChanged();//update ui
                                                    return;
                                                }
                                                for(int i = 0;i<categories.size();i++){
                                                    if(categories.get(i).contains(query)){
                                                        positionsOfCategories.add(i);
                                                    }
                                                }
                                                for(int i = 0;i<positionsOfCategories.size();i++){
                                                    mAdapter.lists.clear();
                                                    mAdapter.lists.add(
                                                            new CategoryFunctionLists().newInstance(
                                                                    categories.get(positionsOfCategories.get(i))
                                                            )
                                                    );
                                                }
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
        );


        this.mAdapter.lists.clear();

        if(!funcsInCode.isEmpty()){
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