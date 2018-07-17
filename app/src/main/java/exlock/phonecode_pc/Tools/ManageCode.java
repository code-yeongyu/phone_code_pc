package exlock.phonecode_pc.Tools;

import android.os.Environment;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import exlock.phonecode_pc.EditFeatures.Block.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.Block.BlockLists;

public class ManageCode {
    private String path;
    private LanguageProfile lp;
    private String content = "";
    private File file;
    private ArrayList<String> bracketLists = new ArrayList<>();
    private BlockAdapter mAdapter;

    public ManageCode(String path, LanguageProfile lp) {
        this.setPath(path);
        this.setLanguageProfile(lp);
        this.loadContent();
        this.setBlockAdapter(new BlockAdapter());
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
    private void loadContent() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(this.getPath());
            if (file.exists()) {
                this.setContentFromFile();//if file already exists, get content
            } else {//if file doesn't exist, create a new file
                this.saveContent();
            }
        }
    }
    public void save() {
        List<BlockLists> blocks = this.getBlockAdapter().blocks;
        String[] lines = this.getContent().split("\n");
        StringBuilder temp = new StringBuilder();
        for(int i = 0;i<lines.length;i++){
            ArrayList<Integer> a = this.getPairs(this.getLine(i));
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
    public void setLanguageProfile(LanguageProfile lp){
        this.lp = lp;
    }
    public LanguageProfile getLanguageProfile(){
        return this.lp;
    }

    //utils
    private ArrayList<Integer> getPairs(String code){
        ArrayList<Integer> pairs = new ArrayList<>();
        ArrayList<Integer> bracketPositions = new ArrayList<>();

        for(int i = 0;i<this.getBrackets().size();i++){
            bracketPositions.addAll(StringTools.findStringPositions(code, this.getBrackets().get(i)));
        }//put all the positions of brackets positions

        if(bracketPositions.size()%2==0) {//if it's able to get pairs
            Collections.sort(bracketPositions, new Descending());//sort bracketPositions from lower value to higher value
            pairs.add(bracketPositions.get(bracketPositions.size() - 1));
            pairs.add(bracketPositions.get(0));
            //get the outermost bracket pairs and add it to pairs
        }
        return pairs;//return the ArrayList which has the positions of outermost bracket pairs
    }
    public void setLine(int line, String to) {
        List<String> lines = Arrays.asList(this.getContent().split("\n"));
        lines.set(line, to);
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<lines.size();i++){
            builder.append(lines.get(i));
            builder.append("\n");
        }
        this.setContent(builder.toString());
    }
    public String getLine(int line) {
        List<String> lines = Arrays.asList(this.content.split("\n"));
        return lines.get(line);
    }
    public void removeLine(int line) {
        List<String> lines = Arrays.asList(this.content.split("\n"));
        lines.remove(line);
    }
    private void addBlock(String function, int line) {
        //todo: ables user to select what symbols will be replaced with EditTexts
        ArrayList<Integer> dam = StringTools.findStringPositions(function, ").");
        ArrayList<Integer> brackets = this.getPairs(this.getLine(line));
        if(dam==null||dam.isEmpty()) {
            if (!brackets.isEmpty()) {
                this.getBlockAdapter().blocks.add(getBlockAdapter().getItemCount(), this.makeUIBlock(function));
                return;
            }
        }
        this.makeUIBlock(function, "", "");
    }

    //UI
    private BlockLists makeUIBlock(String func1, String arg, String func2) {
        BlockLists bl = new BlockLists();
        bl.newInstance(func1, arg, func2);
        return bl;
    }
    private BlockLists makeUIBlock(@NonNull @NotNull String func) {
        ArrayList<Integer> brackets = this.getPairs(func);
        int aValue = brackets.get(0) + 1;
        int bValue = brackets.get(1);
        return this.makeUIBlock(func.substring(0, aValue),func.substring(aValue, bValue),func.substring(bValue, func.length()));
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
            this.addBlock(lines[i], i);
        }
        this.getBlockAdapter().notifyDataSetChanged();
    }
    public void addUIBlock(String code) {//todo: select line and add function there
        this.addBlock(
                code,
                StringTools
                        .findStringPositions(this.getContent(), "\n")
                        .size()
        );
    }
    public void updateBlock(int line){
        List<String> lines = Arrays.asList(this.getContent().split("\n"));
        BlockLists bl = this.makeUIBlock(lines.get(line));
        this.getBlockAdapter().blocks.set(line, bl);
    }
    //Todo: set indent
}