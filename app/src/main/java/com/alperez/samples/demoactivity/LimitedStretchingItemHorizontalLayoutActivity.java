package com.alperez.samples.demoactivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.alperez.samples.R;

public class LimitedStretchingItemHorizontalLayoutActivity extends BaseDemoActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_limited_stretching_item_horizontal_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout ll = null;
        ll.setGravity(0);
    }
}
