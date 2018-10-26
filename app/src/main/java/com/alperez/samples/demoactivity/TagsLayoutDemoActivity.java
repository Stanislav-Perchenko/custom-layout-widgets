package com.alperez.samples.demoactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.alperez.samples.R;
import com.alperez.widget.customlayout.ColoredCharSequence;
import com.alperez.widget.customlayout.TagsLayout;

/**
 * Created by stanislav.perchenko on 10/22/2018
 */
public class TagsLayoutDemoActivity extends BaseDemoActivity {

    private TagsLayout vTagsLayout1, vTagsLayout2;

    private boolean alternateDataset;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_demo_tags_layout;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vTagsLayout1 = (TagsLayout) findViewById(R.id.tags_1);
        vTagsLayout2 = (TagsLayout) findViewById(R.id.tags_2);
        ((CompoundButton) findViewById(R.id.sw_use_padding)).setOnCheckedChangeListener(this::onUsePaddingChanged);
        ((CompoundButton) findViewById(R.id.sw_use_extra_space)).setOnCheckedChangeListener(this::onUseExtSpaceChanged);
        ((CompoundButton) findViewById(R.id.sw_auto_reorder)).setOnCheckedChangeListener(this::onUseAutoReorderChanged);
        ((RadioGroup) findViewById(R.id.gravity_checker)).setOnCheckedChangeListener(this::onGravityChanged);

        ColoredCharSequence[] data = getData(alternateDataset);
        vTagsLayout1.setTags(data);
        vTagsLayout2.setTags(data);
    }

    private void onUsePaddingChanged(CompoundButton v, boolean use) {
        int pad = use ? Math.round(12 * getResources().getDisplayMetrics().density) : 0;
        vTagsLayout1.setPadding(pad, pad, pad, pad);
        vTagsLayout2.setPadding(pad, pad, pad, pad);
    }

    private void onUseExtSpaceChanged(CompoundButton v, boolean use) {
        vTagsLayout1.setUseExtraSpace(use);
        vTagsLayout2.setUseExtraSpace(use);
    }

    private void onUseAutoReorderChanged(CompoundButton v, boolean reorder) {
        vTagsLayout1.setUseAutoReorder(reorder);
        vTagsLayout2.setUseAutoReorder(reorder);
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
        vTagsLayout2.setItemsHorizontalLayoutGravity(g);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_demo_tags_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_refresh) {
            ColoredCharSequence[] data = getData(alternateDataset = !alternateDataset);
            vTagsLayout1.setTags(data);
            vTagsLayout2.setTags(data);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    private final TagModel[][] datasets = new TagModel[2][];

    private ColoredCharSequence[] getData(boolean alternateDataset) {
        int datasetIndex = alternateDataset ? 1 : 0;
        if (datasets[datasetIndex] == null) {
            datasets[datasetIndex] = (alternateDataset)
                    ? new TagModel[]{new TagModel("Lorem ipsum", Color.parseColor("#5697ff")),
                                     new TagModel("Nulla facilisi", Color.parseColor("#56d7ff")),
                                     new TagModel("luctus eu", Color.parseColor("#56ffe0")),
                                     new TagModel("Lorem ipsum dolor sit amet", Color.parseColor("#56ffc3")),
                                     new TagModel("Donec ut", Color.parseColor("#56ff83")),
                                     new TagModel("Lorem ipsum dolor", Color.parseColor("#4aba66")),
                                     new TagModel("Pellentesque tempor", Color.parseColor("#90f984")),
                                     new TagModel("Donec imperdiet", Color.parseColor("#a7dd77")),
                                     new TagModel("Cras ac", Color.parseColor("#d0e87d")),
                                     new TagModel("Lorem ipsum dolor sit", Color.parseColor("#e2e27a")),
                                     new TagModel("Donec ornare libero suscipit porttitor finibus. Sed mauris lectus,", Color.parseColor("#e2c479")),
                                     new TagModel("Nulla aliquet volutpat", Color.parseColor("#e2a179")),
                                     new TagModel("Lorem ipsum dolor sit amet, consectetur adipiscing elit.", Color.parseColor("#eb82ed")),
                                     new TagModel("Mi non", Color.parseColor("#ed82b2")) }

                    : new TagModel[]{new TagModel("Cras ac", Color.parseColor("#d0e87d")),
                                     new TagModel("Lorem ipsum dolor", Color.parseColor("#4aba66")),
                                     new TagModel("luctus eu", Color.parseColor("#56ffe0")),
                                     new TagModel("Lorem ipsum dolor sit amet, consectetur adipiscing elit.", Color.parseColor("#eb82ed")),
                                     new TagModel("Lorem ipsum", Color.parseColor("#5697ff")),
                                     new TagModel("Lorem ipsum dolor sit", Color.parseColor("#e2e27a")),
                                     new TagModel("Lorem ipsum dolor sit amet", Color.parseColor("#56ffc3")),
                                     new TagModel("Donec ornare libero suscipit porttitor finibus. Sed mauris lectus,", Color.parseColor("#e2c479")),
                                     new TagModel("Donec ut", null),
                                     new TagModel("Nulla aliquet volutpat", Color.parseColor("#e2a179")),
                                     new TagModel("Pellentesque tempor", Color.parseColor("#90f984")),
                                     new TagModel("Mi non", Color.parseColor("#ed82b2")),
                                     new TagModel("Donec imperdiet", Color.parseColor("#a7dd77")),
                                     new TagModel("Nulla facilisi", Color.parseColor("#56d7ff")) };
        }
        return datasets[datasetIndex];
    }



    /**********************************************************************************************/
    private class TagModel implements ColoredCharSequence {
        private final String text;
        private final int color;
        private final boolean hasColor;

        public TagModel(@NonNull String text, @Nullable Integer color) {
            if (TextUtils.isEmpty(text)) throw new IllegalArgumentException("Tag text value is empty");
            this.text = text;
            this.color = (color == null) ? 0 : color;
            this.hasColor = (color != null);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (o instanceof TagModel) {
                TagModel other = (TagModel) o;
                return this.text.equals(other.text) && areColorsEqual(this.color, other.color);
            } else {
                return false;
            }
        }

        private boolean areColorsEqual(Integer c1, Integer c2) {
            if (c1 == null && c2 == null) {
                return true;
            } else if ((c1 == null && c2 != null) || (c1 != null && c2 == null)) {
                return false;
            } else {
                return c1.intValue() == c2.intValue();
            }
        }

        @Override
        public String toString() {
            return text;
        }

        @Override
        public int color() {
            return color;
        }

        @Override
        public boolean hasColor() {
            return hasColor;
        }

        @Override
        public int length() {
            return text.length();
        }

        @Override
        public char charAt(int index) {
            return text.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return text.substring(start, end);
        }
    }
}
