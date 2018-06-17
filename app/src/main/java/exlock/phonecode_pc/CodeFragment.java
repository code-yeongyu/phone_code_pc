package exlock.phonecode_pc;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

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

        SharedPreferences pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);

        final LanguageProfile lp = new LanguageProfile(pref.getString("profileJson", ""));
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
            LinearLayout linearCategories = getView().findViewById(R.id.LinearCategories);
            linearCategories.addView(category);
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
