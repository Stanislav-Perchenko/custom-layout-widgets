package com.alperez.widget.customlayout;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Checkable;
import android.widget.TextView;

/**
 * Created by stanislav.perchenko on 10/30/2018
 */
final class CheckItemView implements Checkable, CharSequence {
    private int id;
    private CharSequence text;
    final TextView mainView;
    private final Checkable checkView;

    private boolean hasData;

    boolean isMeasured;
    int measuredW, measuredH;
    boolean isLaidOut;

    private TextItemClickDispatcher itemClickDispatcher;

    public CheckItemView(TextView mainView, Checkable checkView, @Nullable CharSequence text) {
        if (mainView == null) {
            throw new IllegalArgumentException("Main View is null");
        } else {
            (this.mainView = mainView).setOnClickListener(v -> {
                if (hasData && (itemClickDispatcher != null)) itemClickDispatcher.dispatchItemClick(id, text);
            });
        }
        if (checkView == null) {
            throw new IllegalArgumentException("Checkable is null");
        } else {
            this.checkView = checkView;
        }

        if (text != null) mainView.setText(this.text = text);
    }

    public int getId() {
        return id;
    }

    public CharSequence getText() {
        return text;
    }

    public void setData(int id, CharSequence text) {
        if ((this.id != id) || !TextUtils.equals(this.text, text)) {
            this.id = id;
            mainView.setText(this.text = text);
            hasData = (id >= 0) && !TextUtils.isEmpty(text);
        }
    }

    public void setId(int id) {
        this.id = id;
        hasData = (id >= 0) && !TextUtils.isEmpty(text);
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setItemClickDispatcher(TextItemClickDispatcher itemClickDispatcher) {
        this.itemClickDispatcher = itemClickDispatcher;
    }

    /*********************************  Checkable  ************************************************/
    @Override
    public void setChecked(boolean checked) {
        checkView.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return checkView.isChecked();
    }

    @Override
    public void toggle() {
        checkView.toggle();
    }

    /*********************************  CharSequence  *********************************************/
    @Override
    public int length() {
        return text.length();
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return text.subSequence(start, end);
    }

    @Override
    public String toString() {
        return text.toString();
    }

    /*************************************  Equality  *********************************************/
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof CheckItemView) {
            CheckItemView other = (CheckItemView) o;
            if (this.checkView.isChecked() == other.checkView.isChecked()) {
                return TextUtils.equals(this.text, other.text);
            }
        }
        return false;
    }

    public boolean textEquals(CheckItemView other) {
        return TextUtils.equals(this.text, other.text);
    }


    void measure(int specW, int specH) {
        mainView.measure(specW, specH);
        measuredW = mainView.getMeasuredWidth();
        measuredH = mainView.getMeasuredHeight();
        isMeasured = true;
    }

    void invalidateMeasure() {
        mainView.forceLayout();
        isMeasured = false;
        measuredW = 0;
        measuredH = 0;
    }
}
