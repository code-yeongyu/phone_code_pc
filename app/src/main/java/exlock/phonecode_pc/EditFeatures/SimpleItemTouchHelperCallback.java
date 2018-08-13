package exlock.phonecode_pc.EditFeatures;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import exlock.phonecode_pc.EditFeatures.Block.BlockLists;
import exlock.phonecode_pc.R;
import exlock.phonecode_pc.Tools.ManageCode;


public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ManageCode mc;
    public SimpleItemTouchHelperCallback(ManageCode mc) {
        this.mc = mc;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mc.getBlockAdapter().onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        String target;

        View v = viewHolder.itemView;
        TextView f1 = v.findViewById(R.id.func1);
        TextView arg = v.findViewById(R.id.argEditText);
        TextView f2 = v.findViewById(R.id.func2);

        this.mc.updateLine();

        target = f1.getText().toString()+arg.getText()+f2.getText();
        String indent = mc.getLanguageProfile().getIndent();
        if(direction==ItemTouchHelper.RIGHT){
            this.mc.setLine(position, mc.getLanguageProfile().getIndent()+target);
        }else if(direction==ItemTouchHelper.LEFT &&
                target.substring(0, indent.length()).equals(mc.getLanguageProfile().getIndent())){
            this.mc.setLine(position, target.substring(1, target.length()));
        }
        this.mc.updateBlock(position);
        this.mc.getBlockAdapter().notifyItemChanged(position);
    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(!isCurrentlyActive){
            super.onChildDraw(c, recyclerView, viewHolder, 0, dY, actionState, false);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX / 16, dY, actionState, isCurrentlyActive);
    }
}