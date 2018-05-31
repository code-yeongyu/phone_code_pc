package exlock.phonecode_pc.Blocks;

import java.util.ArrayList;
import java.util.Comparator;

import exlock.phonecode_pc.Tools.JsonManager;

/**
 * Created by experse on 18. 5. 29.
 */

public class AlignBlocks {//카테고리 별로 블록을 분류
    private ArrayList<Block> b;
    public void addBlock(Block input){
        b.add(input);
    }
    public ArrayList<Block> getBlocks(){
        return b;
    }
}
