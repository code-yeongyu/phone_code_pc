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
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import exlock.phonecode_pc.EditFeatures.Block.BlockAdapter;
import exlock.phonecode_pc.EditFeatures.Block.BlockLists;
import exlock.phonecode_pc.EditFeatures.SimpleItemTouchHelperCallback;

class Descending implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o2.compareTo(o1);
    }
}
class ManageCodeUtils {
    static ArrayList<Integer> getOutermostPairs(String code, ArrayList<String> brackets){
        ArrayList<Integer> pairs = new ArrayList<>();
        ArrayList<Integer> bracketPositions = new ArrayList<>();

        for(int i = 0;i<brackets.size();i++){
            bracketPositions.addAll(ManageCodeUtils.findStringPositions(code, brackets.get(i)));
        } // put all the positions of brackets positions

        if(!bracketPositions.isEmpty() && bracketPositions.size()%2==0) { // if it's able to get pairs
            Collections.sort(bracketPositions, new Descending()); // sort bracketPositions from lower value to higher value
            pairs.add(bracketPositions.get(bracketPositions.size() - 1)+1);
            pairs.add(bracketPositions.get(0));
            // get the outermost bracket pairs and add it to pairs
        }
        return pairs; // return the ArrayList which has the positions of outermost bracket pairs
    }
    static ArrayList<Integer> findStringPositions(String source, String target){
        int position = 0;
        int length = source.length();
        final ArrayList<Integer> positions = new ArrayList<>();

        while(position!=-1){
            position = source.indexOf(target);
            positions.add(length-source.length()+position);
            source = source.substring(position+1, source.length());
        }
        return positions;
    }
}
public class ManageCode {
    class ManageCodeFile {
        private String content;
        private String path;
        private File file;

        public ManageCodeFile(String path) {
            this.setPath(path);
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) { // 파일 접근이 가능한지 확인
                file = new File(this.getPath());
                if (file.exists()) { // 파일이 존재하면
                    this.setContentFromFile();//
                } else {//if file doesn't exist, create a new file
                    this.saveContent();
                }
            }
        }
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
    }
    class ManageCodeContent {
        private String content;
        private ArrayList<String> lines;
        private ManageCodeFile manageCodeFile;
        private ArrayList<String> bracketsList;

        ManageCodeContent(ManageCodeFile manageCodeFile, ArrayList<String> bracketsList){
            this.manageCodeFile = manageCodeFile;
            this.bracketsList = bracketsList;
        }

        public String getRealTimeContent(BlockAdapter mAdapter, ArrayList<String> bracketsList) { // 뷰에서 EditText 안의 값을 얻어와 실시간 content 를 얻을 수 있는 함수
            final String [] lines = this.getContent().split("\n");
            final StringBuilder temp = new StringBuilder();
            for(int i = 0;i<lines.length;i++){
                final ArrayList<Integer> bracketPairPosition = ManageCodeUtils.getOutermostPairs(lines[i], this.bracketsList);
                if(!bracketPairPosition.isEmpty()) {
                    final int aValue = bracketPairPosition.get(0) + 1;
                    final int functionLength = lines[i].length();
                    temp.append(
                            lines[i].substring(0, aValue)) //EditText 전 값
                            .append(mAdapter.blocks.get(i).arg) // EditText 안에 있는 값
                            .append(lines[i].substring(bracketPairPosition.get(1), functionLength)); //EditText 뒷 값
                }else
                    temp.append(lines[i]);
                temp.append("\n");
            }
            return temp.toString();
        }

        public void setContent(String content){
            this.content = content;
        }
        public String getContent() {
            return this.content;
        }
        public void updateLine(){
            this.lines = new ArrayList<>(Arrays.asList(this.content.split("\n")));
        } // todo: 호출횟수 최적
        public void setLine(int line, String to) {
            this.lines.set(line, to);
            this.setListAsContent(this.lines);
        }
        public void setListAsContent(@NotNull List<String> lines) {
            final StringBuilder builder = new StringBuilder();
            for(int i = 0;i<lines.size();i++){
                builder.append(lines.get(i));
                builder.append("\n");
            }
            this.setContent(builder.toString());
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
        public ArrayList<String> getFunctions() {
            final List<String> lines = Arrays.asList(this.content.split("\n")); // content 를 list로 바꿔 lines에 넣음
            final ArrayList<String> result = new ArrayList<>();
            for(int i = 0;i<lines.size();i++){
                String line = lines.get(i);
                final ArrayList<Integer> bracketPairPosition = ManageCodeUtils.getOutermostPairs(line, this.bracketsList);

                if(!bracketPairPosition.isEmpty()){ // EditText 가 있다면
                    line = line.substring(0, bracketPairPosition.get(0)-1); // 첫번째 괄호 까지 자름 (괄호 앞의 내용은 함수의 이름을 의미함)
                }
                result.add(
                        line
                                .replaceAll("\\s+","") // 공백 제거
                                .replaceAll("\t", "") // 탭 문자 제거
                        //todo: 탭 문자 변경 옵션 제공
                );
            }

            final Set<String> set = new LinkedHashSet<>(result);
            result.clear();
            result.addAll(set); // hash 사용으로 중복 제거
            return result;
        }
    }
    class ManageCodeUI {
        private SimpleItemTouchHelperCallback callback;
        private ItemTouchHelper helper;
        public ItemTouchHelper.Callback getCallback() {
            return this.callback;
        }
        public ItemTouchHelper getTouchHelper(){
            return this.helper;
        }
        public void setTouchHelper(ItemTouchHelper helper) {
            this.helper = helper;
        }
        // 애매함. 프로젝트 코드 뒤져보며 확인해봐야함.

        private BlockAdapter mAdapter;
        private LanguageProfileJsonReader lp;
        private ArrayList<String> bracketsList;

        public BlockAdapter getmAdapter() {
            return mAdapter;
        }

        public ManageCodeUI(BlockAdapter mAdapter, ArrayList<String> bracketsList, LanguageProfileJsonReader lp){
            this.mAdapter = mAdapter;
            this.bracketsList = bracketsList;
            this.lp = lp;
        }

        private void appendBlock(String code) { // 코드 뷰에 블럭 추가
            this.addBlockAt(this.mAdapter.getItemCount(), code);
        }
        private void addBlockAt(int line, String code) {
            final ArrayList<String> reservedObjects = this.lp.getReservedObject();
            for(int i = 0;i<reservedObjects.size();i++) {
                if (code.contains(reservedObjects.get(i))) { // if나 else, for 같은 reserved 키워드가 있다면
                    String contentAfterReserved = code.substring(
                            reservedObjects.get(i).length()+1,
                            code.length()
                    ); // reserved 키워드 뒤의 내용을 잘라 objectName에 넣음

                    code = code.substring(0, code.length()-contentAfterReserved.length());
                    // objectName 을 제외한 내용을 function 에 넣음

                    this.mAdapter.blocks.add(this.mAdapter.getItemCount(),
                            this.makeUIBlock(code, contentAfterReserved, ""));
                    // blockAdapter 에 function 을 넣고, EditText안에 들어갈 내용으로 contentAfterReserved 을 넣음
                    return;
                }
            }

            final ArrayList<Integer> bracketPairPosition = ManageCodeUtils.getOutermostPairs(code, this.bracketsList);

            final ArrayList<Integer> temp = ManageCodeUtils.findStringPositions(code, ").");
            final Boolean isSingleEditText = temp==null||temp.isEmpty();
            if(isSingleEditText && !bracketPairPosition.isEmpty()) { // EditText 가 하나만 들어 갈 수있으며 괄호 쌍이 비어있지 않을 때
                // 한 줄에 여러 EditText 는 지원하지 않기 때문에 여러개가 필요할경우 그냥 EditText 비활성화
                this.mAdapter.blocks.add(line,
                        this.makeUIBlock(code, bracketPairPosition));
                return;
            }

            this.mAdapter.blocks.add(line,
                    this.makeUIBlock(code, "", ""));
        }

        private BlockLists makeUIBlock(String func1, String arg, String func2) {
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
        private void notifyUpdatesInUI(){
            this.mAdapter.notifyDataSetChanged();
        }

        private void updateUIWithContent(String content) {
            this.mAdapter.blocks.clear();
            String[] lines = content.split("\n");
            for(int i = 0;i<lines.length;i++){
                this.appendBlock(lines[i]);
            }
            this.mAdapter.notifyDataSetChanged();
        }
        private void updateBlockAt(int line, String code){
            ArrayList<Integer> bracketPairPosition  = ManageCodeUtils.getOutermostPairs(code, this.bracketsList);
            BlockLists blockLists = this.makeUIBlock(code, bracketPairPosition);
            this.mAdapter.blocks.set(line, blockLists);
        }
    }
}