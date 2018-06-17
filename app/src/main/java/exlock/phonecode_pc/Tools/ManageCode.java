package exlock.phonecode_pc.Tools;

import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by experse on 18. 6. 16.
 */

public class ManageCode {
    private String path;
    private String content = "";
    private File file;

    public ManageCode(String path) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            this.path = path;
            file = new File(path);
            if (file.exists()) {
                setContentFromFile();//if file already exists, get content
            } else {//if file doesn't exist, create a new file
                saveContent();
            }
        }
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return this.path;
    }
    public void setContentFromFile() {
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
    public void setContent(String content){
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }
    public void saveContent(){
        try {
            FileOutputStream fos = new FileOutputStream(this.path);
            fos.write(this.content.getBytes());//create file
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean removeFile(){
        if(file!=null&&file.exists()){
            file.delete();
            return true;
        }
        return false;
    }
}
