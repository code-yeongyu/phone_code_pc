package exlock.phonecode_pc.EditFeatures.Block;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exlock.phonecode_pc.EditFeatures.ItemTouchHelperAdapter;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;

enum MenuList {
    REMOVE, EDIT, ADD_BELOW
}

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private ManageCode mc;
    public BlockAdapter(ManageCode mc){
        this.mc = mc;
    }
    public List<BlockLists> blocks = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView func1;
        private final TextView func2;
        private final EditText arg;
        private final LinearLayout itemBlock;
        private final TextView lineNumber;

        ViewHolder(final View v, final ManageCode mc){
            super(v);
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();

                    BlockLists bl = mc.getBlockAdapter().blocks.get(position);


                    mc.setLine(position, bl.func1+s.toString()+bl.func2);
                    mc.getCallback().init(position);
                }
            };
            this.arg = v.findViewById(R.id.argEditText);
            this.arg.addTextChangedListener(textWatcher);
            this.lineNumber = v.findViewById(R.id.line_number);
            this.func1 = v.findViewById(R.id.func1);
            this.func2 = v.findViewById(R.id.func2);
            this.itemBlock = v.findViewById(R.id.itemBlock);
        }
        TextView getFunc1() {
            return this.func1;
        }
        TextView getFunc2() {
            return this.func2;
        }
        EditText getArg() {
            return this.arg;
        }
        TextView getLineNumber(){
            return this.lineNumber;
        }
        LinearLayout getItemBlock() {
            return this.itemBlock;
        }

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(blocks, i, i + 1);
            }//if move the item to down
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(blocks, i, i - 1);
            }//if move the item to up
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        
        //todo: write here code indenting or add a tab
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_block, viewGroup, false);
        return new ViewHolder(v, this.mc);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        position = holder.getAdapterPosition();
        final int pos = position;
        String funcString1 = this.blocks.get(position).func1;
        String arg = this.blocks.get(position).arg;
        String funcString2 = this.blocks.get(position).func2;

        holder.getItemBlock().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialog(holder, pos);
                return true;
            }
        });

        boolean isFunc1Empty = funcString1.equals("");
        holder.getItemBlock().setVisibility(isFunc1Empty ? View.VISIBLE : View.VISIBLE);
        if(isFunc1Empty){
            return;
        }
        holder.getArg().setText(arg);
        holder.getFunc2().setText(funcString2);
        holder.getLineNumber().setText(" "+(position+1)+" ");
        holder.getLineNumber().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN){

                }
                return false;
            }
        });
        boolean isFunc2Empty = funcString2.equals("");
        holder.getArg().setVisibility(isFunc2Empty ? View.GONE : View.VISIBLE);
        holder.getFunc2().setVisibility(isFunc2Empty ? View.GONE : View.VISIBLE);
        holder.getFunc1().setText(funcString1);
        if(isFunc2Empty){
            return;
        }
    }
    private void createDialog(@NotNull ViewHolder holder, final int position){
        final String[] items = {"Remove Block", "Edit Block", "Add Block Below"};
        final Context context = holder.getItemBlock().getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int which) {
                        MenuList[] values = MenuList.values();
                        switch (values[which]){
                            case REMOVE:
                                mc.removeLine(position);
                                mc.getBlockAdapter().notifyItemRemoved(position);
                                mc.updateUI();
                                break;
                            case EDIT:
                                Toast.makeText(context, "You clicked Edit!", Toast.LENGTH_SHORT).show();
                                break;
                            case ADD_BELOW:
                                Toast.makeText(context, "You clicked Add below!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        builder.show();
    }
    @Override
    public int getItemCount() {
        return blocks.size();
    }
}