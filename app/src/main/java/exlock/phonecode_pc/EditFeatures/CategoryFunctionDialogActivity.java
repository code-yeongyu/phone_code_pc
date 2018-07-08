package exlock.phonecode_pc.EditFeatures;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import exlock.phonecode_pc.R;

public class CategoryFunctionDialogActivity extends Dialog {

    public CategoryFunctionDialogActivity(Context context) { super(context); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_function_dialog);
    }
}