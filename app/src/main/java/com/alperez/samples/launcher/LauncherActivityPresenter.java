package com.alperez.samples.launcher;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import com.alperez.samples.BuildConfig;
import com.alperez.samples.utils.BasePresenter;
import com.alperez.utils.AsyncTaskCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by stanislav.perchenko on 10/15/2018
 */
public class LauncherActivityPresenter extends BasePresenter<LauncherScreenView> {
    private final Set<LoaderTask> workingTasks = new HashSet<>();

    private final Resources mRes;

    public LauncherActivityPresenter(LauncherScreenView launcherScreenView, Resources mRes) {
        super(launcherScreenView);
        this.mRes = mRes;
    }

    @Override
    public void initializeView() {
        new LoaderTask().safeExecute(mRes.getAssets());
    }

    @Override
    public synchronized void release() {
        synchronized (workingTasks) {
            for (LoaderTask task : workingTasks) task.cancel(true);
            workingTasks.clear();
            super.release();
        }
    }

    private class LoaderTask extends AsyncTaskCompat<AssetManager, Void, Collection<LauncherScreenItem>> {
        @Override
        protected void onPreExecute() {
            synchronized (workingTasks) {
                workingTasks.add(this);
            }
        }

        private Throwable error;

        @Override
        protected Collection<LauncherScreenItem> doInBackground(AssetManager... ams) {

            try (InputStream is = ams[0].open("launcher_items.json", AssetManager.ACCESS_STREAMING)) {
                if (BuildConfig.DEBUG) Thread.sleep(850);


                byte buff[] = new byte[128];
                int count;

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((count = is.read(buff)) > 0) {
                    bos.write(buff, 0, count);
                }

                JSONArray jItems = new JSONArray(new String(bos.toByteArray()));

                List<LauncherScreenItem> data = new ArrayList<>(jItems.length());
                for (int i=0; i<jItems.length(); i++) {
                    JSONObject jItem = jItems.getJSONObject(i);
                    Class<? extends AppCompatActivity> cls;
                    try {
                        cls = (Class<? extends AppCompatActivity>) Class.forName(jItem.getString("screenClassName"));
                    } catch (ClassNotFoundException e) {
                        cls = null;
                    }
                    data.add(LauncherScreenItem.builder()
                            .setTitle(jItem.getString("title"))
                            .setSubtitle(jItem.optString("subtitle", null))
                            .setDescription(jItem.optString("description", null))
                            .setActivityClass(cls)
                            .build());
                }
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) try { Thread.sleep(1350); } catch (InterruptedException e1) {}
                error = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Collection<LauncherScreenItem> data) {
            synchronized (workingTasks) {
                workingTasks.remove(this);
                LauncherScreenView v = getView();
                if (!isReleased() && v != null) {
                    if (data != null) {
                        v.onLauncherItems(data);
                    } else if (error != null) {
                        v.onLoadItemsError(error);
                    }
                }
            }
        }
    }
}
