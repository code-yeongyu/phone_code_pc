package exlock.phonecode_pc;

import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CodeFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code, null);

        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//if accessing to storage is available
            StringBuffer buffer = new StringBuffer();
            String data;
            try {
                String path = Environment.getExternalStorageDirectory() + "/PhoneCode/language1.json";
                BufferedReader reader = new BufferedReader(new FileReader(path));
                data = reader.readLine();
                while (data != null) {
                    buffer.append(data);
                    data = reader.readLine();
                }

                LinearLayout linearCategories = getView().findViewById(R.id.LinearCategories);
                final LanguageProfile lp = new LanguageProfile(buffer.toString());
                final ArrayList<String> categories = lp.getCategories();
                for(int i = 0; i<categories.size();i++){
                    final Button category = new Button(getContext());
                    final int forAssign = i;
                    category.setOnClickListener(
                            new Button.OnClickListener() {
                                public void onClick(View v) {
                                    addButton(lp.getFunctions(categories.get(forAssign)));
                                }
                            }
                    );
                    category.setText(categories.get(i));
                    linearCategories.addView(category);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }//get file
        }
    }
    Handler handler = new Handler();
    int j = 0;
    private void addButton(final ArrayList<String> functions){
        final LinearLayout linearFunctions = getView().findViewById(R.id.LinearFunctions);

        final Runnable r = new Runnable() {
            public void run() {
                if(j<functions.size()){
                    Button function = new Button(getContext());
                    function.setText(functions.get(j));
                    linearFunctions.addView(function);
                    handler.post(this);
                    j++;
                }
            }
        };
        handler.post(r);
    }
}
