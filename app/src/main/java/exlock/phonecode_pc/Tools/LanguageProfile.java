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

    public void setFunctions(final Object functions) {
        this.functions = functions;
    }
}
