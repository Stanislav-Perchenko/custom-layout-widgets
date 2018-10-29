package com.alperez.widget.customlayout;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Checkable;

/**
 * Created by stanislav.perchenko on 10/29/2018
 */
class CheckableTextItem implements Checkable, CharSequence {

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a checkable item changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a checkable item has changed.
         *
         * @param item The checkable item view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(CheckableTextItem item, boolean isChecked);
    }

    private final  int id;
    private final CharSequence text;
    private boolean checked;
    private OnCheckedChangeListener onCheckedChangeListener;


    public CheckableTextItem(@NonNull CharSequence text, boolean checked, int id) {
        this.id = id;
        this.text = text;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.onCheckedChangeListener = l;
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

    /*********************************  Checkable  ************************************************/
    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (onCheckedChangeListener != null) onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        checked = !checked;
        if (onCheckedChangeListener != null) onCheckedChangeListener.onCheckedChanged(this, checked);
    }


    /*************************************  Equality  *********************************************/
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof CheckableTextItem) {
            CheckableTextItem other = (CheckableTextItem) o;
            if (this.checked == other.checked) {
                return TextUtils.equals(this.text, other.text);
            }
        }
        return false;
    }

    public boolean textEquals(CheckableTextItem other) {
        return TextUtils.equals(this.text, other.text);
    }
}
