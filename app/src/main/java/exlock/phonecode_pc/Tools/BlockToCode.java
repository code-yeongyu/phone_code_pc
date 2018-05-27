package exlock.phonecode_pc.Tools;

import android.util.Log;

public class BlockToCode {
    String code;
    public void setCode(String code){
        this.code = code;
    }
    public String editBlock(String value, int index){
        String temp;
        temp = this.code.substring(index);
        this.code = this.code.substring(0, this.code.length()-temp.length()) + value + temp;
        return this.code;
    }
}