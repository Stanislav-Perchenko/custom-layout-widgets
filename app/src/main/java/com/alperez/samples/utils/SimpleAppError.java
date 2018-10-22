package com.alperez.samples.utils;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public class SimpleAppError implements AppError {
    @Override
    public String userMessage() {
        return "";
    }

    @Override
    public String detailedMessage() {
        return "";
    }

    @Override
    public Throwable error() {
        return null;
    }
}
