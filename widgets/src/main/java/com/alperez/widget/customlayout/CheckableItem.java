package com.alperez.widget.customlayout;

import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

/**
 * Created by stanislav.perchenko on 10/29/2018
 */
public abstract class CheckableItem implements Checkable, CharSequence {

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
        void onCheckedChanged(CheckableItem item, boolean isChecked);
    }

    private boolean checked;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CheckableItem() {}

    public CheckableItem(boolean checked) {
        this.checked = checked;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.onCheckedChangeListener = l;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof CheckableItem) {
            CheckableItem other = (CheckableItem) o;
            if (this.checked == other.checked) {
                return textEquals(other);
            }
        }
        return false;
    }

    public boolean textEquals(CheckableItem other) {
        if (other != null) {
            if (this.length() == other.length()) {
                int n = length();
                for (int i=0; i<n; i++) {
                    if (this.charAt(i) != other.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }
}
