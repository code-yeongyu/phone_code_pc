package exlock.phonecode_pc.EditFeatures.CustomDialog;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import exlock.phonecode_pc.EditFeatures.Block.BlockAdapter;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {

    List<CategoryFunctionLists> lists = new ArrayList<>();
    private ManageCode mc;
    private String category;
    private FunctionDialogActivity fda;
    private int line = -1;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final View v;

        ViewHolder(final View v){
            super(v);
            this.name = v.findViewById(R.id.func1);
            this.v = v;
        }
        TextView getName() {
            return name;
        }
        View getView() {return v;}
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_category_function, viewGroup, false);
        return new ViewHolder(v);
    }

    void init(ManageCode mc, String category, FunctionDialogActivity fda){
        this.mc = mc;
        this.category = category;
        this.fda = fda;
    }

    void init(ManageCode mc, String category, FunctionDialogActivity fda, int line){
        this.mc = mc;
        this.category = category;
        this.fda = fda;
        this.line = line;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String functionName = lists.get(position).name;
        final TextView nameTextView = holder.getName();
        nameTextView.setText(functionName);
        if(this.line == -1) {
            nameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = mc.getContent();
                    String functionValue = mc.getLanguageProfile().getFunctionValue(category, functionName);
                    mc.setContent(content + "\n" + functionValue);
                    mc.addUIBlock(functionValue);
                    mc.notifyUpdatesInUI();
                    fda.dismiss();
                }
            });
        }else{
            nameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = mc.getContent();
                    String functionValue = mc.getLanguageProfile().getFunctionValue(category, functionName);
                    mc.setContent(content + "\n" + functionValue);
                    BlockAdapter ba = mc.getBlockAdapter();
                    ba.blocks.add(line+1,
                            mc.makeUIBlock(functionValue, "", "")
                    );
                    mc.notifyUpdatesInUI();
                    fda.dismiss();
                    //todo: finish add below feature
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
}