package com.alperez.samples.demoactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.alperez.samples.R;
import com.alperez.widget.ReadMoreTextView;

/**
 * Created by stanislav.perchenko on 2/8/2019
 */
public class ReadMoreDemoActivity extends BaseDemoActivity {

    private ReadMoreTextView vTxtReadMore;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_demo_read_more;
    }

    private CharSequence mReadMoreText;
    private int marginLeft, marginRight;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vTxtReadMore = (ReadMoreTextView) findViewById(R.id.txt_read_more);
        mReadMoreText = vTxtReadMore.getReadMoreText();
        vTxtReadMore.setOnReadMoreClickListener(v -> Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show());
        ((Switch) findViewById(R.id.rm_en_switch)).setOnCheckedChangeListener((buttonView, isChecked) -> vTxtReadMore.setReadMoreText(isChecked ? mReadMoreText : null));
        ((Switch) findViewById(R.id.size_switch)).setOnCheckedChangeListener((buttonView, isChecked) -> setViewMargin(isChecked ? 1 : 2));

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) vTxtReadMore.getLayoutParams();
        marginLeft  = mlp.leftMargin;
        marginRight = mlp.rightMargin;
    }

    private void setViewMargin(int scale) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) vTxtReadMore.getLayoutParams();
        mlp.rightMargin = scale*marginRight;
        mlp.leftMargin = scale*marginLeft;
        vTxtReadMore.setLayoutParams(mlp);
    }
}
