package com.alperez.widget.customlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by stanislav.perchenko on 10/29/2018
 */
public class CheckLayout extends FrameLayout {

    public interface ItemViewBuilder {
        TextView buildViewItem(CharSequence contentText);
    }

    public interface OnItemClickListener {
        boolean onItemClicked(CheckableItem item);
    }

    //--- Layout-related attributes  ---
    private int attrItemsHorizontalGravity = Gravity.LEFT;
    private int attrMinItemToItemDistance = 10;
    private boolean attrAutoReorderItems;


    public boolean attrMultipleChoice;      //TODO Init this !!!!!!!!



    private final LayoutInflater inflater;
    private ItemViewBuilder mItemViewBuilder;
    private OnItemClickListener mItemClickListener;

    private final List<CheckableItem> mData = new ArrayList<>();

    public CheckLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);
        extractCustomAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflater = LayoutInflater.from(context);
        extractCustomAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void extractCustomAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    public void setItemViewBuilder(ItemViewBuilder vBuilder) {
        this.mItemViewBuilder = vBuilder;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public void setUseAutoReorder(boolean useReorder) {
        if (attrAutoReorderItems != useReorder) {
            attrAutoReorderItems = useReorder;
            invalidate();
            requestLayout();
        }
    }

    public void setItemsHorizontalLayoutGravity(int grav) {
        if (attrItemsHorizontalGravity != grav) {
            attrItemsHorizontalGravity = grav;
            if ((grav == Gravity.LEFT) || (grav == Gravity.CENTER_HORIZONTAL) || (grav == Gravity.RIGHT)) {
                invalidate();
                requestLayout();
            } else {
                throw new IllegalArgumentException("Wrong gravity - "+grav);
            }
        }
    }

    public void setItems(CheckableItem... tags) {
        checkBuilder();
        if (mData.size() == tags.length) {
            boolean eq = true;
            int i = 0;
            for (Iterator<CheckableItem> itr = mData.iterator(); itr.hasNext(); i++) {
                if (!itr.next().equals(tags[i])) {
                    eq = false; break;
                }
            }
            if (eq) return;
        }

        mData.clear();
        for (CheckableItem tag : tags) mData.add(tag);
        updateDataset();
    }

    public void setItems(Collection<CheckableItem> tags) {
        checkBuilder();
        if (mData.size() == tags.size()) {
            boolean eq = true;
            Iterator<? extends CheckableItem> itr2 = tags.iterator();
            for (Iterator<CheckableItem> itr1 = mData.iterator(); itr1.hasNext();) {
                if (!itr1.next().equals(itr2.next())) {
                    eq = false; break;
                }
            }
            if (eq) return;
        }

        mData.clear();
        for (CheckableItem tag : tags) mData.add(tag);
        updateDataset();
    }

    private void checkBuilder() {
        if (mItemViewBuilder == null) {
            throw new IllegalStateException("The ItemViewBuilder instance is not set");
        }
    }


    private boolean isLayoutInvalid;
    private boolean isMeasurementInvalid;

    @Override
    public void requestLayout() {
        isMeasurementInvalid = true;
        isLayoutInvalid = true;
        super.requestLayout();
    }

    private void updateDataset() {
        //TODO Implement this !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }






    private class TagItemView implements CheckableItem.OnCheckedChangeListener {
        final TextView mainView;
        final Checkable checker;
        CheckableItem data;
        boolean isMeasured;
        int measuredW, measuredH;
        private boolean isLaidOut;

        public TagItemView(TextView mainView, Checkable checker) {
            if (mainView == null) {
                throw new IllegalArgumentException("Main View is null");
            } else {
                this.mainView = mainView;
            }
            if (checker == null) {
                throw new IllegalArgumentException("Checkable is null");
            } else {
                this.checker = checker;
            }

            mainView.setOnClickListener(v -> {
                if (data != null) dispatchItemClick(data);
            });
        }

        void setData(@NonNull CheckableItem data) {
            boolean textChanged = (this.data == null) || !this.data.textEquals(data);
            boolean checkChanged= (this.data == null) || (checker.isChecked() != data.isChecked());

            if (this.data != null) this.data.setOnCheckedChangeListener(null);
            (this.data = data).setOnCheckedChangeListener(this);

            if (textChanged || checkChanged) {
                if (textChanged) mainView.setText(data);
                if (checkChanged) checker.toggle();
                invalidateMeasure();
            }

        }

        @Override
        public void onCheckedChanged(CheckableItem item, boolean isChecked) {
            if (this.data == item) {
                checker.setChecked(isChecked);
                //TODO pass item check changed to ext. listener !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
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


    private boolean dispatchingItemClick;
    public final void dispatchItemClick(CheckableItem item) {
        dispatchingItemClick = true;
        try {
            if (mItemClickListener != null) {
                if (!mItemClickListener.onItemClicked(item)) return;
            }

            if (attrMultipleChoice) {
                item.toggle();
            } else if (!item.isChecked()) {
                for (CheckableItem cit : mData) {
                    if (cit.isChecked()) cit.toggle();
                    item.setChecked(true);
                }
            } else {
                // If single choice and item is checked -> do nothing
            }
        } finally {
            dispatchingItemClick = false;
        }
    }

}
