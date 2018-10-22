package com.alperez.samples.utils;

import java.lang.ref.WeakReference;

/**
 * Created by stanislav.perchenko on 10/27/2016.
 */
public abstract class BasePresenter<TView> {

    private WeakReference<TView> viewRef;
    private volatile boolean released;

    protected BasePresenter(TView view) {
        if (view != null) {
            viewRef = new WeakReference<>(view);
        } else {
            throw new IllegalArgumentException("A valid instance of the View interface must be provided");
        }
    }

    protected TView getView() {
        return viewRef.get();
    }

    /**
     * This method starts initialization process of prepared UI.
     * It must be called from the Activity's onPostCreate()
     */
    public abstract void initializeView();

    /**
     * This method must be called from Activity's onDestroy()
     */
    public synchronized void release() {
        released = true;
        viewRef.clear();
    }

    public synchronized boolean isReleased() {
        return released;
    }
}
