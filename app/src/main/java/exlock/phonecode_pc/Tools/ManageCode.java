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
import java.util.Collections;
import java.util.List;

import exlock.phonecode_pc.EditFeatures.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.BlockLists;
import exlock.phonecode_pc.LanguageProfile;

import static exlock.phonecode_pc.Tools.StringTools.findStringPositions;

public class ManageCode {
    private String path;
    private LanguageProfile lp;
    private String content = "";
    private File file;
    private ArrayList<String> bracketLists = new ArrayList<>();
    private BlockAdapter mAdapter;

    private void setContentFromFile() {
        try {
            String data;
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(this.path));//get profile json from set path
            data = reader.readLine();
            while (data != null) {
                buffer.append(data);
                data = reader.readLine();
                if(data!=null){
                    buffer.append("\n");
                }
            }
            reader.close();
            this.content = buffer.toString();
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
            file = new File(this.path);
            if (file.exists()) {
                setContentFromFile();//if file already exists, get content
            } else {//if file doesn't exist, create a new file
                saveContent();
            }
        }
    }

    public ManageCode(String path, LanguageProfile lp, BlockAdapter mAdapter) {
        this.setPath(path);
        this.setLanguageProfile(lp);
        this.loadContent();
    }

    public void save() {
        List<BlockLists> blocks = this.mAdapter.blocks;
        String[] lines = this.getContent().split("\n");
        StringBuilder temp = new StringBuilder();
        for(int i = 0;i<lines.length;i++){
            ArrayList<Integer> a = this.getPairsLine(i);
            if(!a.isEmpty()) {
                Collections.reverse(a);
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

    public void addBracket(String left, String right) {
        this.bracketLists.add(left);
        this.bracketLists.add(right);
    }
    public ArrayList<String> getBrackets(){
        return this.bracketLists;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return this.path;
    }

    public void setLanguageProfile(LanguageProfile lp){
        this.lp = lp;
    }
    public LanguageProfile getLanguageProfile(){
        return this.lp;
    }

    public void setBlockAdapter(BlockAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }
    public BlockAdapter getBlockAdapter(BlockAdapter mAdapter) {
        return this.mAdapter;
    }

    public void setContent(String content){
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }

    public ArrayList<Integer> getPairsLine(int line) {
        String[] function = this.content.split("\n");
        ArrayList<ArrayList<Integer>> bracketPositions = new ArrayList<>();
        ArrayList<Integer> pairs = new ArrayList<>();

        for(int i = 0;i<bracketLists.size();i++){
            bracketPositions.add(StringTools.findStringPositions(function[line], bracketLists.get(i)));
        }//create positions per every brackets
        for(int i = 0;i<bracketLists.size();i+=2){
            ArrayList<Integer> left = bracketPositions.get(i);
            left.addAll(bracketPositions.get(i+1));//merge left and right
            if(left.size()%2==0) {//if it's able to get pairs
                Collections.sort(left, new Descending());
                int leftSize = left.size();
                while(leftSize!=0) {
                    int temp = (leftSize / 2) - 1;
                    pairs.add(left.get(temp));
                    pairs.add(left.get(temp + 1));//make pairs start from center
                    left.remove(temp);
                    left.remove(temp);
                    //remove the center values
                    leftSize -= 2;
                }
            }
        }
        Collections.reverse(pairs);
        return pairs;
    }

    public boolean removeFile() {
        if(file!=null&&file.exists()){
            file.delete();
            return true;
        }
        return false;
    }

    private void makeUIBlock(@NonNull @NotNull String func, @NonNull @NotNull ArrayList<Integer> brackets) {
        int aValue = brackets.get(0) + 1;
        int bValue = brackets.get(1);
        this.makeUIBlock(func.substring(0, aValue),func.substring(aValue, bValue),func.substring(bValue, func.length()));
    }
    private void makeUIBlock(String func1, String arg, String func2) {
        this.mAdapter.blocks.add(this.mAdapter.getItemCount(),
                new BlockLists().newInstance(
                        func1,arg,func2
                )
        );
    }
    private void addBlock(String function, int line) {
        //todo: ables user to select what symbols will be replaced with EditTexts

        ArrayList<Integer> dam = findStringPositions(function, ").");
        ArrayList<Integer> brackets = this.getPairsLine(line);
        if(dam==null||dam.isEmpty()) {
            if (!brackets.isEmpty()) {
                this.makeUIBlock(function, brackets);
                return;
            }
        }
        this.makeUIBlock(function, "", "");
    }

    public void addUIBlock(String code) {//todo: select line and add function there
        this.addBlock(
                code,
                StringTools
                    .findStringPositions(this.getContent(), "\n")
                    .size()
        );
        this.mAdapter.notifyDataSetChanged();
    }
    public void updateUI() {
        mAdapter.blocks.clear();
        String[] lines = this.getContent().split("\n");
        for(int i = 0;i<lines.length;i++){
            this.addBlock(lines[i], i);
        }
        this.mAdapter.notifyDataSetChanged();
    }
}
