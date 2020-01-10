package com.alperez.samples.utils;

import android.graphics.Color;

/**
 * Created by stanislav.perchenko on 1/10/2020, 5:03 PM.
 */
public enum UserRole {
    DRIVER("#FF5800"),
    FITTER("#FFC90A"),
    HH("#76D750"),
    FRODE_LAURSEN("#2F80ED"),
    SITE_MANAGER("#9B51E0"),
    CARPENTER("#00AECB"),
    ELECTRICIAN("#E3118F"),
    PLUMBER("#73EEC2"),
    MASON("#0029FF"),
    OTHER("#565A5C"),
    HH_ADMIN("#000000");


    private final int color;

    UserRole(String sColor) {
        color = Color.parseColor(sColor);
    }

    public int getColor() {
        return color;
    }
}
