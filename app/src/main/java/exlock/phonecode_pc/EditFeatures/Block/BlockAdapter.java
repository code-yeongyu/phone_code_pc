package exlock.phonecode_pc.EditFeatures.Block;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exlock.phonecode_pc.EditActivity;
import exlock.phonecode_pc.EditFeatures.CustomDialog.CategoryDialogActivity;
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
        private final LinearLayout block;
        private final TextView lineNumber;

        ViewHolder(final View v, final ManageCode mc){
            super(v);
            //todo: keep values inside the edit text when try to add an indent to a block
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //int position = getAdapterPosition();
                    //BlockLists bl = mc.getBlockAdapter().blocks.get(position);
                    //Log.d("cs", bl.func1+s.toString()+bl.func2);
                    //mc.setLine(position, bl.func1+s.toString()+bl.func2);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            this.arg = v.findViewById(R.id.argEditText);
            this.arg.addTextChangedListener(textWatcher);
            this.lineNumber = v.findViewById(R.id.line_number);
            this.func1 = v.findViewById(R.id.func1);
            this.func2 = v.findViewById(R.id.func2);
            this.block = v.findViewById(R.id.block);
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
        LinearLayout getblock() {
            return this.block;
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


        holder.getblock().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createDialog(holder, pos);
                return true;
            }
        });

        boolean isFunc1Empty = funcString1.equals("");
        holder.getblock().setVisibility(isFunc1Empty ? View.VISIBLE : View.VISIBLE);
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
        final Context context = holder.getblock().getContext();
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
                                editBlockDialog(context, position).show();
                                break;
                            case ADD_BELOW:
                                CategoryDialogActivity cda = new CategoryDialogActivity(context);
                                cda.init(mc, position);
                                cda.show();
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
    private Dialog editBlockDialog(final Context context, final int position){
        final EditText et = new EditText(context);
        et.setLines(1);
        et.setSingleLine();
        et.setText(mc.getLine(position));
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Edit line "+(position+1))
                .setView(et)
                .setPositiveButton("change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(mInputMethodManager != null)
                            mInputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        mc.setLine(position, et.getText().toString());
                        mc.getBlockAdapter().notifyItemChanged(position);
                        mc.updateUI();
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        return dialog;
    }
}