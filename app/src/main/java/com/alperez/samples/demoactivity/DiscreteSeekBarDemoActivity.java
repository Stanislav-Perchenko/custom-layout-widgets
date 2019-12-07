package com.alperez.samples.demoactivity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.alperez.samples.R;
import com.alperez.widget.customlayout.DiscreteSeekBar;

public class DiscreteSeekBarDemoActivity extends BaseDemoActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_demo_discrete_seekbar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DiscreteSeekBar vs = (DiscreteSeekBar) findViewById(R.id.seek);
        vs.setSelectionOptions("0", "10", "20", "40", "80", "160", "320");
        vs.setSelectionIndex(0);
        findViewById(R.id.btn_left).setOnClickListener(v -> {
            if (vs.getSelectionIndex() > 0) vs.setSelectionIndex(vs.getSelectionIndex() -1);
        });

        findViewById(R.id.btn_right).setOnClickListener(v -> {
            if (vs.getSelectionIndex() < (vs.getSelectionOptionsCount() - 1)) {
                vs.setSelectionIndex(vs.getSelectionIndex() + 1);
            }
        });
    }
}
