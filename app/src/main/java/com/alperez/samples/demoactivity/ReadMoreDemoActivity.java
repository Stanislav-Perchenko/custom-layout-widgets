package com.alperez.samples.demoactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.alperez.samples.R;
import com.alperez.widget.customlayout.ReadMoreTextView;

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
    private int marginTop, marginLeft, marginRight;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vTxtReadMore = (ReadMoreTextView) findViewById(R.id.txt_read_more);
        mReadMoreText = vTxtReadMore.getReadMoreText();
        vTxtReadMore.setOnReadMoreClickListener(v -> Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show());
        ((Switch) findViewById(R.id.rm_en_switch)).setOnCheckedChangeListener((buttonView, isChecked) -> vTxtReadMore.setReadMoreText(isChecked ? mReadMoreText : null));
        ((Switch) findViewById(R.id.size_switch)).setOnCheckedChangeListener((buttonView, isChecked) -> setViewMargin(isChecked ? 1 : 2));

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) vTxtReadMore.getLayoutParams();
        marginTop   = mlp.topMargin;
        marginLeft  = mlp.leftMargin;
        marginRight = mlp.rightMargin;

        ((SeekBar) findViewById(R.id.margin_seek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("ReadMoreDemoActivity", "seek = " + progress);
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) vTxtReadMore.getLayoutParams();
                mlp.topMargin = marginTop * progress;
                vTxtReadMore.setLayoutParams(mlp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void setViewMargin(int scale) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) vTxtReadMore.getLayoutParams();
        mlp.rightMargin = scale*marginRight;
        mlp.leftMargin = scale*marginLeft;
        vTxtReadMore.setLayoutParams(mlp);
    }
}
