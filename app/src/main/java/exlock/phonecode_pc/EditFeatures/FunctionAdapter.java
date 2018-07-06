package exlock.phonecode_pc.EditFeatures;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import exlock.phonecode_pc.R;

class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {

    List<FunctionLists> functions = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView function;

        ViewHolder(final View v){
            super(v);
            function = v.findViewById(R.id.function);
        }
        TextView getFunc1() {
            return function;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_function, viewGroup, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String functionString = functions.get(position).function;
        holder.getFunc1().setText(functionString);
    }
    @Override
    public int getItemCount() {
        return functions.size();
    }
}