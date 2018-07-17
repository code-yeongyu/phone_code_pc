package exlock.phonecode_pc.EditFeatures;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import exlock.phonecode_pc.Tools.ManageCode;
import exlock.phonecode_pc.Tools.StringTools;


public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final ManageCode mc;

    public SimpleItemTouchHelperCallback(ManageCode mc) {
        this.mc = mc;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        String target = this.mc.getLine(position);
        if(direction==ItemTouchHelper.RIGHT){
            this.mc.setLine(position, "\t"+target);
            this.mc.updateBlock(position);
            this.mc.getBlockAdapter().notifyItemChanged(position);
        }else if(direction==ItemTouchHelper.LEFT && target.charAt(0)=='\t'){
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
        super.onChildDraw(c, recyclerView, viewHolder, dX / 8, dY, actionState, isCurrentlyActive);
    }
}