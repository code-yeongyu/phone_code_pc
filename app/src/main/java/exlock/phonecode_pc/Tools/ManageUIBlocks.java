package exlock.phonecode_pc.Tools;

import java.util.ArrayList;

import exlock.phonecode_pc.EditFeatures.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.BlockLists;
import exlock.phonecode_pc.LanguageProfile;

import static exlock.phonecode_pc.Tools.StringTools.findStringPositions;

public class ManageUIBlocks {
    private BlockAdapter mAdapter;
    private ManageCode mc;
    private LanguageProfile lp;
    public ManageUIBlocks(BlockAdapter mAdapter, ManageCode mc, LanguageProfile lp){
        this.mAdapter = mAdapter;
        this.mc = mc;
        this.lp = lp;
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
    public void addBlock(String code){//todo: select line and add function there
        String content = this.mc.getContent();
        this.mc.setContent(content+"\n"+code);
        addBlock(code, StringTools.findStringPositions(this.mc.getContent(), "\n").size());
        this.mAdapter.notifyDataSetChanged();
    }
    public LanguageProfile getLanguageProfile(){
        return this.lp;
    }
    public void addBlock(String category, String funcName){//todo: select line and add function there
        String content = this.mc.getContent();
        String functionValue = this.lp.getFunctionValue(category, funcName);
        this.mc.setContent(content+"\n"+functionValue);
        addBlock(functionValue, StringTools.findStringPositions(this.mc.getContent(), "\n").size());
        mAdapter.notifyDataSetChanged();
    }
    public void updateUI(){
        mAdapter.blocks.clear();
        String[] lines = mc.getContent().split("\n");
        for(int i = 0;i<lines.length;i++){
            this.addBlock(lines[i], i);
        }
        this.mAdapter.notifyDataSetChanged();
    }
}