package exlock.phonecode_pc.Tools;

import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LanguageProfile {
    private Object lang_informs, functions, symbols, reserved;

    public Object getFunctions() {
        return functions;
    }

    public void setFunctions(Object functions) {
        this.functions = functions;
    }

    public ArrayList<String> getFunctionsByCategory(String category){
        return (new Gson()).fromJson(getFunctions().toString(), new TypeToken<ArrayList<String>>(){}.getType());
    }

    public Object getLangInforms() {
        return lang_informs;
    }

    public Object getReserved() {
        return reserved;
    }

    public Object getSymbols() {
        return symbols;
    }
}
