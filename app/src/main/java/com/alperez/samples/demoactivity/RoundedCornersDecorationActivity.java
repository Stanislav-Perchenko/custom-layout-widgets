package com.alperez.samples.demoactivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.alperez.samples.R;
import com.alperez.widget.customlayout.RoundedCornersDecorationView;

/**
 * Created by stanislav.perchenko on 19.04.2020 at 1:17.
 */
public class RoundedCornersDecorationActivity extends BaseDemoActivity {

    private SeekBar vSeekAll;
    private final SeekBar[] vSeekers = new SeekBar[4];

    private RoundedCornersDecorationView vCornerOverlay;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_rounded_corners_decoration;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vCornerOverlay = findViewById(R.id.corner_overlay);
        (vSeekAll = findViewById(R.id.seek_all)).setOnSeekBarChangeListener(new SimpleUserSeekBarListener() {
            @Override
            public void onUserProgress(int progress) {
                for (SeekBar sb : vSeekers) {
                    sb.setProgress(progress);
                }

                vCornerOverlay.setAllCorners(progress2radius(progress));
            }
        });
        (vSeekers[0] = findViewById(R.id.seek_top_left)).setOnSeekBarChangeListener(new SimpleUserSeekBarListener() {
            @Override
            public void onUserProgress(int progress) {
                setAvgForAll();
                vCornerOverlay.setRadiusTopLeft(progress2radius(progress));
            }
        });
        (vSeekers[1] = findViewById(R.id.seek_top_right)).setOnSeekBarChangeListener(new SimpleUserSeekBarListener() {
            @Override
            public void onUserProgress(int progress) {
                setAvgForAll();
                vCornerOverlay.setRadiusTopRight(progress2radius(progress));
            }
        });
        (vSeekers[2] = findViewById(R.id.seek_bot_right)).setOnSeekBarChangeListener(new SimpleUserSeekBarListener() {
            @Override
            public void onUserProgress(int progress) {
                setAvgForAll();
                vCornerOverlay.setRadiusBotRight(progress2radius(progress));
            }
        });
        (vSeekers[3] = findViewById(R.id.seek_bot_left)).setOnSeekBarChangeListener(new SimpleUserSeekBarListener() {
            @Override
            public void onUserProgress(int progress) {
                setAvgForAll();
                vCornerOverlay.setRadiusBotLeft(progress2radius(progress));
            }
        });

        ((SeekBar) findViewById(R.id.seek_fill_color)).setOnSeekBarChangeListener(new SimpleUserSeekBarListener() {
            @Override
            public void onUserProgress(int progress) {
                vCornerOverlay.setFillColor(Color.argb(0xFF, progress, progress, progress));
            }
        });
    }

    private int progress2radius(int progress) {
        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, progress, getResources().getDisplayMetrics());
        return Math.round(radius);
    }

    private void setAvgForAll() {
        float acc = 0;
        for (SeekBar sb : vSeekers) acc += sb.getProgress();
        int avg = Math.round(acc / 4f);
        vSeekAll.setProgress(avg);
    }

}

abstract class SimpleUserSeekBarListener implements SeekBar.OnSeekBarChangeListener {

    public abstract void onUserProgress(int progress);

    @Override
    public final void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) onUserProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Stub
    }
}
