package com.alperez.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public class ViewUtils {

    @SuppressLint("NewApi")
    public static int getColorFromResourcesCompat(Resources res, int resId, @Nullable Resources.Theme theme) {
        return (Build.VERSION.SDK_INT >= 23) ? res.getColor(resId, theme) : res.getColor(resId);
    }

    @SuppressLint("NewApi")
    public static Drawable getDrawableForResource(Resources res, int resId, @Nullable Resources.Theme theme) {
        return (Build.VERSION.SDK_INT >= 21) ? res.getDrawable(resId, theme) : res.getDrawable(resId);
    }

    @SuppressWarnings("deprecation")
    public static void setBgDrawable(View v, Drawable drawable) {
        if (Build.VERSION.SDK_INT < 16) {
            v.setBackgroundDrawable(drawable);
        } else {
            v.setBackground(drawable);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBgDrawable(View v, int resId, Resources res, @Nullable Resources.Theme theme) {
        Drawable dr = (Build.VERSION.SDK_INT >= 21) ? res.getDrawable(resId, theme) : res.getDrawable(resId);
        if (Build.VERSION.SDK_INT < 16) {
            v.setBackgroundDrawable(dr);
        } else {
            v.setBackground(dr);
        }
    }

    private ViewUtils() { }
}
