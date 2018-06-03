package exlock.phonecode_pc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {
    int i = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo :
        //file manager to open file
        //ssh, ftp
        //code to block
        //block to code
        setContentView(R.layout.activity_edit);
        Button b = findViewById(R.id.helloButton);
        b.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout codeLinearLayout = findViewById(R.id.codeLayout);
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //View layoutCode = inflater.inflate(R.layout.layout_code, null);
                LinearLayout ll = new LinearLayout(getApplicationContext());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                TextView tv1 = new TextView(getApplicationContext());
                tv1.setId(i);
                tv1.setTextSize(20);
                tv1.setText(i+"번째");
                EditText et = new EditText(getApplicationContext());
                et.setTextSize(20);
                ll.addView(tv1);
                ll.addView(et);
                codeLinearLayout.addView(ll);
            }
        });
    }
}

