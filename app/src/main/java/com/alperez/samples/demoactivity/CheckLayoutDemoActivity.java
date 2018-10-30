package com.alperez.samples.demoactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alperez.samples.R;
import com.alperez.widget.customlayout.CheckLayout;

/**
 * Created by stanislav.perchenko on 10/26/2018
 */
public class CheckLayoutDemoActivity extends BaseDemoActivity {

    private CheckLayout vTagsLayout1;

    private boolean alternateDataset;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_demo_check_layout;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vTagsLayout1 = (CheckLayout) findViewById(R.id.checks_1);
        ((CompoundButton) findViewById(R.id.sw_use_padding)).setOnCheckedChangeListener(this::onUsePaddingChanged);
        ((CompoundButton) findViewById(R.id.sw_use_extra_space)).setOnCheckedChangeListener(this::onUseExtSpaceChanged);
        ((CompoundButton) findViewById(R.id.sw_auto_reorder)).setOnCheckedChangeListener(this::onUseAutoReorderChanged);
        ((RadioGroup) findViewById(R.id.gravity_checker)).setOnCheckedChangeListener(this::onGravityChanged);

        vTagsLayout1.setItemViewBuilder(new CheckLayout.ItemViewBuilder() {
            @Override
            public TextView buildViewItem(LayoutInflater inflater, ViewGroup parent) {
                return (TextView) inflater.inflate(R.layout.check_layout_item, parent, false);
            }
        });
        vTagsLayout1.setItems(getData(alternateDataset));
    }

    private void onUsePaddingChanged(CompoundButton v, boolean use) {
        int pad = use ? Math.round(12 * getResources().getDisplayMetrics().density) : 0;
        vTagsLayout1.setPadding(pad, pad, pad, pad);
    }

    private void onUseExtSpaceChanged(CompoundButton v, boolean use) {
        vTagsLayout1.setAllocateFreeSpace(use);
    }

    private void onUseAutoReorderChanged(CompoundButton v, boolean reorder) {
        vTagsLayout1.setUseAutoReorder(reorder);
    }

    private void onGravityChanged(RadioGroup group, int checkedId) {
        int g = 0;
        switch (checkedId) {
            case R.id.chk_gravity_left:
                g = Gravity.LEFT;
                break;
            case R.id.chk_gravity_center:
                g = Gravity.CENTER_HORIZONTAL;
                break;
            case R.id.chk_gravity_right:
                g = Gravity.RIGHT;
                break;
        }
        vTagsLayout1.setItemsHorizontalLayoutGravity(g);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_demo_tags_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_refresh) {
            vTagsLayout1.setItems(getData(alternateDataset = !alternateDataset));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    private final CharSequence[][] datasets = new CharSequence[2][];

    private CharSequence[] getData(boolean alternateDataset) {
        int datasetIndex = alternateDataset ? 1 : 0;
        if (datasets[datasetIndex] == null) {
            datasets[datasetIndex] = (alternateDataset)
                    ? new CharSequence[]  { "Lorem ipsum",
                                            "Nulla facilisi",
                                            "luctus eu",
                                            "Lorem ipsum dolor sit amet",
                                            "Donec ut",
                                            "Lorem ipsum dolor",
                                            "Pellentesque tempor",
                                            "Donec imperdiet",
                                            "Cras ac",
                                            "Lorem ipsum dolor sit",
                                            "Donec ornare libero suscipit porttitor finibus. Sed mauris lectus,",
                                            "Nulla aliquet volutpat",
                                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                            "Mi non" }

                    : new CharSequence[]  { "Cras ac", "Mi non", "Nulla", "ipsum", "Pellentesque tempor", "Lorem ipsum dolor sit"};
        }
        return datasets[datasetIndex];
    }
}

