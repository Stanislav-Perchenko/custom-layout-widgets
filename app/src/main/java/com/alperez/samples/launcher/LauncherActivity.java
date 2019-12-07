package com.alperez.samples.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alperez.samples.GlobalConstants;
import com.alperez.samples.R;
import com.alperez.samples.databinding.ActivityLauncherBinding;
import com.alperez.utils.UniformVerticalRecyclerItemSpace;

import java.util.Collection;

/**
 * Created by stanislav.perchenko on 10/15/2018
 */
public class LauncherActivity extends AppCompatActivity implements LauncherScreenView {

    private ActivityLauncherBinding binding;

    private LauncherActivityPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher);

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.addItemDecoration(new UniformVerticalRecyclerItemSpace(getResources().getDimensionPixelSize(R.dimen.list_card_item_space)));
        binding.recycler.setOverScrollMode(View.OVER_SCROLL_NEVER);

        presenter = new LauncherActivityPresenter(this, getResources());
        presenter.initializeView();
        binding.setIsLoading(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }

    @Override
    public void onLauncherItems(Collection<LauncherScreenItem> launcherItems) {
        binding.setIsLoading(false);
        binding.setError(null);
        if (binding.getAdapter() == null) {
            binding.setAdapter(new LauncherAdapter(this, launcherItems) {
                @Override
                public void onItemClicked(int position, LauncherScreenItem item) {
                    if (item.activityClass() != null) {
                        Intent launcher = new Intent(LauncherActivity.this, item.activityClass());
                        launcher.putExtra(GlobalConstants.ARG_SCREEN_TITLE, item.title());
                        launcher.putExtra(GlobalConstants.ARG_SCREEN_SUBTITLE, item.subtitle());
                        startActivity(launcher);
                    }
                }
            });
        } else {
            ((LauncherAdapter) binding.getAdapter()).setData(launcherItems);
        }
    }

    @Override
    public void onLoadItemsError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        binding.setIsLoading(false);
        binding.setError(e);
    }
}
