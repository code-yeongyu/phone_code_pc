package exlock.phonecode_pc.EditFeatures;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.ArrayList;

import exlock.phonecode_pc.EditFeatures.Block.BlockLists;
import exlock.phonecode_pc.Tools.ManageCode;


public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ManageCode mc;
    private int position;

    public SimpleItemTouchHelperCallback(ManageCode mc) {
        this.mc = mc;
    }

    public void setManageCode(ManageCode mc){
        this.mc = mc;
    }

    public void init(int position){
        this.position = position;
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
        BlockLists bl = this.mc.getBlockAdapter().blocks.get(position);
        String target;

        target = this.mc.getLine(position);

        if(direction==ItemTouchHelper.RIGHT){
            this.mc.setLine(position, "\t"+target);
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
        super.onChildDraw(c, recyclerView, viewHolder, dX / 16, dY, actionState, isCurrentlyActive);
    }
}