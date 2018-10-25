package com.alperez.samples.demoactivity;

import android.view.Menu;
import android.view.MenuItem;

import com.alperez.samples.R;

/**
 * Created by stanislav.perchenko on 10/22/2018
 */
public class TagsLayoutDemoActivity extends BaseDemoActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_demo_tags_layout;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_demo_tags_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_refresh) {
            //TODO Change text and refresh !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
