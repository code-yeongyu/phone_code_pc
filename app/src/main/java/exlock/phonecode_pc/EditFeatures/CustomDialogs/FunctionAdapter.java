package exlock.phonecode_pc.EditFeatures.CustomDialogs;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import exlock.phonecode_pc.EditFeatures.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.BlockLists;
import exlock.phonecode_pc.LanguageProfile;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;
import exlock.phonecode_pc.Tools.StringTools;

import static android.content.Context.MODE_PRIVATE;
import static exlock.phonecode_pc.Tools.StringTools.findStringPositions;

class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {

    List<CategoryFunctionLists> lists = new ArrayList<>();
    private ManageCode mc;
    private String category;
    private BlockAdapter mAdapter;
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

    void init(ManageCode mc, String category, BlockAdapter ba, FunctionDialogActivity fda){
        this.mc = mc;
        this.category = category;
        this.mAdapter = ba;
        this.fda = fda;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String functionName = lists.get(position).name;
        final TextView nameTextView = holder.getName();
        final LanguageProfile lp =
                new LanguageProfile(holder.getView().getContext().getSharedPreferences("json", MODE_PRIVATE).getString("profileJson", ""));
        nameTextView.setText(functionName);
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String funcValue = lp.getFunctionValue(category, functionName);
                mc.setContent(mc.getContent()+"\n"+funcValue);
                addBlock(funcValue, StringTools.findStringPositions(mc.getContent(), "\n").size());
                fda.dismiss();
            }
        });
    }
    private void addBlock(String function, int line)  {
        //todo: ables user to select what symbols will be replaced with EditTexts

        ArrayList<Integer> dam = findStringPositions(function, ").");
        ArrayList<Integer> brackets = mc.getPairsLine(line);
        if(dam==null||dam.isEmpty()) {
            if (!brackets.isEmpty()) {
                makeBlock(function, brackets);
                return;
            }
        }
        makeBlock(function, "", "");
    }
    private void makeBlock(String func, ArrayList<Integer> brackets){
        int aValue = brackets.get(0) + 1;
        int bValue = brackets.get(1);
        makeBlock(func.substring(0, aValue),func.substring(aValue, bValue),func.substring(bValue, func.length()));
    }
    private void makeBlock(String func1, String arg, String func2){
        this.mAdapter.blocks.add(this.mAdapter.getItemCount(),
                new BlockLists().newInstance(
                        func1,arg,func2
                )
        );
    }
    @Override
    public int getItemCount() {
        return lists.size();
    }
}