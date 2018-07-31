package exlock.phonecode_pc.EditFeatures.CustomDialog;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

import static android.content.Context.MODE_PRIVATE;

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    List<CategoryFunctionLists> lists = new ArrayList<>();
    private CategoryDialogActivity cda;
    private ManageCode mc;
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
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String categoryName = lists.get(position).name;
        final TextView nameTextView = holder.getName();
        nameTextView.setText(categoryName);
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionDialogActivity fda = new FunctionDialogActivity(holder.getView().getContext());
                if(line == -1) {
                    fda.init(holder.getView().getContext().getSharedPreferences("json", MODE_PRIVATE)
                                    .getString("profileJson", ""),
                            categoryName,
                            mc);
                }else{
                    fda.init(holder.getView().getContext().getSharedPreferences("json", MODE_PRIVATE)
                                    .getString("profileJson", ""),
                            categoryName,
                            mc,
                            line);
                }
                fda.show();
                cda.dismiss();
            }
        });
    }
    void init(ManageCode mc, CategoryDialogActivity cda){
        this.mc = mc;
        this.cda = cda;
    }
    void init(ManageCode mc, CategoryDialogActivity cda, int line){
        this.mc = mc;
        this.cda = cda;
        this.line = line;
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }
}