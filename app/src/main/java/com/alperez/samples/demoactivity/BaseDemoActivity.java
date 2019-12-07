package com.alperez.samples.demoactivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alperez.samples.GlobalConstants;
import com.alperez.samples.R;

/**
 * Created by stanislav.perchenko on 10/20/2018
 */
public abstract class BaseDemoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());
        if (setupToolbar()) {
            Bundle extras = (getIntent() == null) ? null : getIntent().getExtras();
            if (extras != null) {
                getSupportActionBar().setTitle(extras.getString(GlobalConstants.ARG_SCREEN_TITLE, ""));
                if (extras.containsKey(GlobalConstants.ARG_SCREEN_SUBTITLE)) getSupportActionBar().setSubtitle(extras.getString(GlobalConstants.ARG_SCREEN_SUBTITLE));
            }
        }

    }

    protected abstract int getLayoutResId();

    private boolean setupToolbar() {
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            ab = getSupportActionBar();
        }
        if(ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}

