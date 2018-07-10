package exlock.phonecode_pc.EditFeatures.CustomDialogs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {

    List<CategoryFunctionLists> lists = new ArrayList<>();
    private ManageCode mc;
    private String category;
    private FunctionDialogActivity fda;

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
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String functionName = lists.get(position).name;
        final TextView nameTextView = holder.getName();
        nameTextView.setText(functionName);
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mc.getContent();
                String functionValue = mc.getLanguageProfile().getFunctionValue(category, functionName);
                mc.setContent(content+"\n"+functionValue);
                mc.addUIBlock(functionValue);
                mc.notifyUpdatesInUI();
                fda.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }
}