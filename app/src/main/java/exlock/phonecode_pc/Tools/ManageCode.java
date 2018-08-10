package exlock.phonecode_pc.Tools;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import exlock.phonecode_pc.EditFeatures.Block.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.Block.BlockLists;
import exlock.phonecode_pc.EditFeatures.SimpleItemTouchHelperCallback;

public class ManageCode {
    private String path;
    private LanguageProfileJsonReader lp;
    private String content = "";
    private File file;
    private ArrayList<String> bracketLists = new ArrayList<>();
    private BlockAdapter mAdapter;
    private SimpleItemTouchHelperCallback callback;
    private ItemTouchHelper helper;
    private ArrayList<String> lines;

    public ManageCode(String path, LanguageProfileJsonReader lp) {
        this.setPath(path);
        this.setLanguageProfile(lp);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(this.getPath());
            if (file.exists()) {
                this.setContentFromFile();//if file already exists, get content
            } else {//if file doesn't exist, create a new file
                this.saveContent();
            }
        }
        this.setBlockAdapter(new BlockAdapter(this));
        this.callback =
                new SimpleItemTouchHelperCallback(this);
        this.helper = new ItemTouchHelper(this.callback);
    }

    public SimpleItemTouchHelperCallback getCallback() {
        return this.callback;
    }

    public ItemTouchHelper getTouchHelper(){
        return this.helper;
    }

    //files
    private void setContentFromFile() {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(this.path));//get profile json from set path
            String data = reader.readLine();
            while (data != null) {
                builder.append(data);
                data = reader.readLine();
                if(data!=null){
                    builder.append("\n");
                }
            }
            reader.close();
            this.content = builder.toString();
        } catch(FileNotFoundException e){
            //todo: check permission and alert dialog or print no file
        } catch (IOException e) {
            e.printStackTrace();
        }//get file
    }
    private void saveContent() {
        try {
            if(file.canWrite()) {
                FileOutputStream fos = new FileOutputStream(this.path);
                fos.write(this.content.getBytes());//create file
                fos.close();
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void save() {
        List<BlockLists> blocks = this.getBlockAdapter().blocks;
        String[] lines = this.getContent().split("\n");
        StringBuilder temp = new StringBuilder();
        for(int i = 0;i<lines.length;i++){
            ArrayList<Integer> a = this.getOutermostPairs(lines[i]);
            if(!a.isEmpty()) {
                int aValue = a.get(0) + 1;
                int functionLength = lines[i].length();
                temp.append(
                        lines[i].substring(0, aValue))//texts before arguments
                        .append(blocks.get(i).arg)//arguments in edit text
                        .append(lines[i].substring(a.get(1), functionLength));//texts after arguments
            }else{//if brackets not exists
                temp.append(lines[i]);
            }
            temp.append("\n");
        }
        this.setContent(temp.toString());
        this.saveContent();
    }
    public boolean removeFile() {
        if(file!=null&&file.exists()){
            file.delete();
            return true;
        }
        return false;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return this.path;
    }

    //managing the content
    public void setContent(String content){
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }
    public void addBracket(String left, String right) {
        this.bracketLists.add(left);
        this.bracketLists.add(right);
    }//faster index means higher priority
    public ArrayList<String> getBrackets() {
        return this.bracketLists;
    }
    public void setLanguageProfile(LanguageProfileJsonReader lp){
        this.lp = lp;
    }
    public LanguageProfileJsonReader getLanguageProfile(){
        return this.lp;
    }

    //utils
    private ArrayList<Integer> getOutermostPairs(String code){
        ArrayList<Integer> pairs = new ArrayList<>();
        ArrayList<Integer> bracketPositions = new ArrayList<>();

        for(int i = 0;i<this.getBrackets().size();i++){
            bracketPositions.addAll(StringTools.findStringPositions(code, this.getBrackets().get(i)));
        }//put all the positions of brackets positions

        if(!bracketPositions.isEmpty() && bracketPositions.size()%2==0) {//if it's able to get pairs
            Collections.sort(bracketPositions, new Descending());//sort bracketPositions from lower value to higher value
            pairs.add(bracketPositions.get(bracketPositions.size() - 1)+1);
            pairs.add(bracketPositions.get(0));
            //get the outermost bracket pairs and add it to pairs
        }
        return pairs;//return the ArrayList which has the positions of outermost bracket pairs
    }
    private ArrayList<Integer> getOutermostdPairs(String code){
        ArrayList<Integer> pairs = new ArrayList<>();
        ArrayList<ArrayList<Integer>> bracketPositions = new ArrayList<>();

        for(int i = 0;i<this.getBrackets().size()/2;i++){
            ArrayList<Integer> tempPositions = new ArrayList<>();
            for(int j = 0;j<this.getBrackets().size();j++){
                tempPositions.addAll(StringTools.findStringPositions(code, this.getBrackets().get(j)));
            }//add all positions per brackets
            bracketPositions.add(tempPositions);
        }//put all the positions of brackets positions
        //todo: *done

        ArrayList<Integer> temp = new ArrayList<>();
        for(int i = 0;i<this.getBrackets().size()/2;i++){
            if(bracketPositions.get(i).size()%2==0) {//if it's able to get pairs
                Collections.sort(bracketPositions.get(i), new Descending());//sort bracketPositions from lower value to higher value
                temp.add(bracketPositions.get(i).get(bracketPositions.size()));//get the front value of pairs
            }
        }
        int i;
        Collections.sort(temp, new Descending());
        int position = 0;
        for(i = 0;i<temp.size();i++){
            if(bracketPositions.get(i).contains(temp.get(i))) {
                position = bracketPositions.get(i).indexOf(temp.get(i));
                break;
            }
        }


        pairs.add(bracketPositions.get(i).get(0));
        pairs.add(bracketPositions.get(i).get(position));
        return pairs;//return the ArrayList which has the positions of outermost bracket pairs
    }//deprecated
    public void setListAsContent(@NotNull List<String> lines){
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<lines.size();i++){
            builder.append(lines.get(i));
            builder.append("\n");
        }
        this.setContent(builder.toString());
    }

    public void updateLine(){
        this.lines = new ArrayList<>(Arrays.asList(this.content.split("\n")));
    }
    public void setLine(int line, String to) {
        this.lines.set(line, to);
        this.setListAsContent(this.lines);
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public String getLine(int line) {
        return lines.get(line);
    }
    public void removeLine(int line) {
        this.lines.remove(line);
        this.setListAsContent(this.lines);
    }
    public ArrayList<String> getFunctionsInCode(){
        List<String> lines = Arrays.asList(this.content.split("\n"));
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0;i<lines.size();i++){
            String line = lines.get(i);
            ArrayList<Integer> pairs = this.getOutermostPairs(line);
            if(!pairs.isEmpty()){//if targeting line has brackets
                result.add(line.substring(0, pairs.get(0)-1));
            }
        }
        for(int i = 0;i<result.size();i++){
            String target = result.get(i);
            result.set(i,target.replaceAll("\\s+","").replaceAll("\t", ""));//remove blanks
        }
        Set<String> set = new LinkedHashSet<>(result);
        result.clear();
        result.addAll(set);
        return result;
    }

    //UI
    private void addBlock(String function) {
        //todo: ables user to select what symbols will be replaced with EditTexts
        ArrayList<Integer> dam = StringTools.findStringPositions(function, ").");

        ArrayList<Integer> brackets = this.getOutermostPairs(function);

        if(dam==null||dam.isEmpty()) {
            if (!brackets.isEmpty()) {
                this.getBlockAdapter().blocks.add(getBlockAdapter().getItemCount(),
                        this.makeUIBlock(function, brackets));
                return;
            }
        }//if has brackets

        this.getBlockAdapter().blocks.add(getBlockAdapter().getItemCount(),
                this.makeUIBlock(function, "", ""));
    }
        this.getBlockAdapter().blocks.add(getBlockAdapter().getItemCount(),
                this.makeUIBlock(function, "", ""));
    }
    public BlockLists makeUIBlock(String func1, String arg, String func2) {
        BlockLists bl = new BlockLists();
        bl.newInstance(func1, arg, func2);
        return bl;
    }
    @Nullable
    private BlockLists makeUIBlock(@NonNull @NotNull String func, ArrayList<Integer> brackets) {
        if(brackets.isEmpty()){
            return this.makeUIBlock(func, "", "");
        }
        int aValue = brackets.get(0);
        int bValue = brackets.get(1);
        String func1 = func.substring(0, aValue);
        String arg = func.substring(aValue, bValue);
        String func2 = func.substring(bValue, func.length());
        return this.makeUIBlock(func1, arg, func2);
    }
    public void setBlockAdapter(BlockAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }
    public BlockAdapter getBlockAdapter() {
        return this.mAdapter;
    }
    public void notifyUpdatesInUI(){
        this.mAdapter.notifyDataSetChanged();
    }
    public void updateUI() {
        this.getBlockAdapter().blocks.clear();
        String[] lines = this.getContent().split("\n");
        for(int i = 0;i<lines.length;i++){
            this.addBlock(lines[i]);
        }
        this.getBlockAdapter().notifyDataSetChanged();
    }
    public void addUIBlock(String code) {//todo: select line and add function there
        this.addBlock(
                code
        );
    }
    public void updateBlock(int line){
        updateLine();
        String target = this.getLine(line);
        ArrayList<Integer> brackets = this.getOutermostPairs(target);
        BlockLists bl = this.makeUIBlock(target, brackets);
        this.getBlockAdapter().blocks.set(line, bl);
    }
    //Todo: set indent
}