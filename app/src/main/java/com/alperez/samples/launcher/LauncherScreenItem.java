package com.alperez.samples.launcher;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Created by stanislav.perchenko on 10/15/2018
 */
@AutoValue
public abstract class LauncherScreenItem {

    public abstract String title();
    @Nullable
    public abstract String subtitle();
    @Nullable
    public abstract String description();
    @Nullable
    public abstract Class<? extends Activity> activityClass();

    public static Builder builder() {
        return new AutoValue_LauncherScreenItem.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setTitle(String title);
        public abstract Builder setSubtitle(String subtitle);
        public abstract Builder setDescription(String description);
        public abstract Builder setActivityClass(Class<? extends Activity> activityClass);

        public abstract LauncherScreenItem build();
    }
}
