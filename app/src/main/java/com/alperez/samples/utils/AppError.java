package com.alperez.samples.utils;

/**
 * Created by stanislav.perchenko on 4/9/2018.
 */

public interface AppError {
    String userMessage();
    String detailedMessage();
    Throwable error();
}
