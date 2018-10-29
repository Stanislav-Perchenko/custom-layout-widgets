package com.alperez.widget.customlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stanislav.perchenko on 10/29/2018
 */
public class CheckLayout extends FrameLayout {

    public interface ItemViewBuilder {
        TextView buildViewItem(LayoutInflater inflater);
    }

    public interface OnItemClickListener {
        boolean onItemClicked(CheckableItem item);
    }

    //--- Layout-related attributes  ---
    private int attrItemsHorizontalGravity = Gravity.LEFT;  //TODO Init this !!!!!!!!
    private int attrMinItemToItemDistance = 10;             //TODO Init this !!!!!!!!
    private boolean attrAutoReorderItems;                   //TODO Init this !!!!!!!!
    private boolean attrAllocateFreeSpace;                  //TODO Init this !!!!!!!!

    public boolean attrMultipleChoice;                      //TODO Init this !!!!!!!!



    private final LayoutInflater inflater;
    private ItemViewBuilder mItemViewBuilder;
    private OnItemClickListener mItemClickListener;

    private final List<CheckableItem> mData = new ArrayList<>();
    private final List<TagItemView> mTagItemViews = new LinkedList<>();

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


    public void clearAllChecks() {
        ensureClickNotDispatching();
        for (CheckableItem ci : mData) {
            if (ci.isChecked()) ci.toggle();
        }
        invalidate();
    }

    public void setChecked(int index, boolean isChecked) {
        ensureClickNotDispatching();
        if (index < 0 || index >= mData.size()) {
            throw new IndexOutOfBoundsException(String.format("Try access item %d. Total item number is %d", index, mData.size()));
        } else {
            mData.get(index).setChecked(isChecked);
            invalidate();
        }
    }

    public void setMultipleChecks(int... indexes) {
        ensureClickNotDispatching();
        if (!attrMultipleChoice) {
            throw new IllegalStateException("Try to set multiple checks on the single-choice widget");
        } else {
            boolean[] checks = new boolean[mData.size()];
            for (int i : indexes) checks[i] = true;

            int pos = 0;
            for (Iterator<CheckableItem> itr = mData.iterator(); itr.hasNext(); pos ++) {
                itr.next().setChecked(checks[pos]);
            }
            invalidate();
        }
    }

    public void setMultipleChecks(Collection<Integer> indexes) {
        ensureClickNotDispatching();
        if (!attrMultipleChoice) {
            throw new IllegalStateException("Try to set multiple checks on the single-choice widget");
        } else {
            boolean[] checks = new boolean[mData.size()];
            for (int i : indexes) checks[i] = true;

            int pos = 0;
            for (Iterator<CheckableItem> itr = mData.iterator(); itr.hasNext(); pos ++) {
                itr.next().setChecked(checks[pos]);
            }
            invalidate();
        }
    }

    private void ensureClickNotDispatching() {
        if (dispatchingItemClick) throw new IllegalStateException("Checked state cannot be changed externally while item click is being dispatched");
    }

    private boolean isLayoutInvalid;
    private boolean isMeasurementInvalid;




    /************************ Data set section  ***************************************************/


    private void updateDataset() {
        TagItemView[] prepViewItems = new TagItemView[mData.size()];

        //Step 1. Look for already initialized items
        for (int i=0; i<prepViewItems.length; i++) {
            CheckableItem tag = mData.get(i);
            for (Iterator<TagItemView> itr = mTagItemViews.iterator(); itr.hasNext(); ) {
                TagItemView iv = itr.next();
                if (iv.data.equals(tag)) {
                    prepViewItems[i] = iv;
                    itr.remove();
                    break;
                }
            }
        }

        //Step 2. Try to find or instantiate the rest items
        for (int i=0; i<prepViewItems.length; i++) {
            if (prepViewItems[i] == null) {
                CheckableItem dataItem = mData.get(i);
                prepViewItems[i] = mTagItemViews.isEmpty() ? buildAndAddNewTagViewItem(dataItem) : mTagItemViews.remove(0);
                prepViewItems[i].setData(dataItem);
            }
        }

        //Step 3. If the new dataset size if less then the previous one, remove unnecessary views from container
        if (!mTagItemViews.isEmpty()) {
            for (TagItemView tiv : mTagItemViews) super.removeView(tiv.mainView);
            mTagItemViews.clear();
        }

        //Step 4. Store updated ItemView set
        mTagItemViews.addAll(Arrays.asList(prepViewItems));
        invalidate();
        requestLayout();
    }

    private TagItemView buildAndAddNewTagViewItem(CheckableItem data) {
        TextView vTxt = mItemViewBuilder.buildViewItem(inflater);
        if (vTxt == null) {
            throw new RuntimeException("Builder did not return TextView instance");
        } else if (vTxt instanceof Checkable) {
            return new TagItemView(vTxt, (Checkable) vTxt, data);
        } else {
            throw new RuntimeException("The returned TextView instance does not implement Checkable interface");
        }
    }




    private class TagItemView implements CheckableItem.OnCheckedChangeListener {
        final TextView mainView;
        final Checkable checker;
        CheckableItem data;
        boolean isMeasured;
        int measuredW, measuredH;
        private boolean isLaidOut;

        public TagItemView(TextView mainView, Checkable checker, @Nullable CheckableItem data) {
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

            if (data != null) {
                mainView.setText(this.data = data);
                checker.setChecked(data.isChecked());
            }
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


    /**************************  Measuring section  ***********************************************/

    @Override
    public void requestLayout() {
        isMeasurementInvalid = true;
        isLayoutInvalid = true;
        super.requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        isLayoutInvalid = true;
    }


    private class RowModel {
        int rowHeight;
        final List<TagItemView> rowItems = new LinkedList<>();
    }

    private int lastMeasuredContentWidth = -1;
    private int lastMeasuredContentHeight = -1;
    private Deque<RowModel> mRecycledRows = new LinkedList<>();
    private List<RowModel> mMeasuredRows = new LinkedList<>();


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Width dimension must be defined exactly (MATCH_PARENT or exact size)");
        }

        final int hSpecType = MeasureSpec.getMode(heightMeasureSpec);
        int containerW = View.MeasureSpec.getSize(widthMeasureSpec);
        int containerH = View.MeasureSpec.getSize(heightMeasureSpec);
        int contentW = containerW - getPaddingLeft() - getPaddingRight();

        if (isMeasurementInvalid || (lastMeasuredContentWidth != contentW)) {
            isMeasurementInvalid = false;
            lastMeasuredContentWidth = containerW;

            //int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(contentW, View.MeasureSpec.AT_MOST);
            int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            //Step 1: measure all children independently
            for (TagItemView tiv : mTagItemViews) tiv.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            //Step 2: Recycle all existing rows
            mRecycledRows.addAll(mMeasuredRows);
            mMeasuredRows.clear();

            //Step 3: Gather new rows
            List<TagItemView> tmpItems = new LinkedList<>();
            tmpItems.addAll(mTagItemViews);
            int totalContentH = 0;
            while (!tmpItems.isEmpty()) {
                RowModel row = (attrAutoReorderItems && (tmpItems.size() < 16))
                        ? getRowWithReorder(tmpItems, contentW, mRecycledRows)
                        : getRowNoReorder(tmpItems, contentW, mRecycledRows);
                mMeasuredRows.add(row);
                totalContentH += row.rowHeight;
            }

            //Step 4: Re-measure items to allocate extra space
            if (attrAllocateFreeSpace) {
                int index = 1;
                for (Iterator<RowModel> itr = mMeasuredRows.iterator(); (index < mMeasuredRows.size()) && itr.hasNext(); index ++) {
                    reMeasureRowToAllocateFreeSpace(itr.next(), contentW);
                }
            }

            //Step 4: Calculate final container height
            if (hSpecType == MeasureSpec.EXACTLY) {
                lastMeasuredContentHeight = containerH;
            } else {
                lastMeasuredContentHeight = totalContentH + attrMinItemToItemDistance*(getRowCount() - 1) + getPaddingTop() + getPaddingBottom();
                if ((hSpecType == MeasureSpec.AT_MOST) && (lastMeasuredContentHeight > containerH)) {
                    lastMeasuredContentHeight = containerH;
                }
            }
        }

        setMeasuredDimension(lastMeasuredContentWidth, lastMeasuredContentHeight);
    }

    private RowModel getRowNoReorder(List<TagItemView> input, int parentW, Deque<RowModel> convertRows) {
        RowModel rm = convertRows.isEmpty() ? new RowModel() : convertRows.pop();
        rm.rowItems.clear();
        rm.rowHeight = 0;
        int totW = 0;
        for (Iterator<TagItemView> itr = input.iterator(); itr.hasNext(); ) {
            TagItemView tiv = itr.next();
            if (rm.rowItems.isEmpty()) {
                rm.rowItems.add(tiv);
                rm.rowHeight = tiv.measuredH;
                totW = tiv.measuredW;
                itr.remove();
            } else if ((totW + attrMinItemToItemDistance + tiv.measuredW) <= parentW) {
                rm.rowItems.add(tiv);
                if (rm.rowHeight < tiv.measuredH) rm.rowHeight = tiv.measuredH;
                totW += (attrMinItemToItemDistance + tiv.measuredW);
                itr.remove();
            } else {
                break;
            }
        }
        return rm;
    }

    private TagItemView[] reorderWorkArray = new TagItemView[16];
    private RowModel getRowWithReorder(List<TagItemView> input, int parentW, Deque<RowModel> convertRows) {

        final int lengthEarlyThreshold = (int) Math.round(0.95 * parentW);
        final int N = input.size();

        if (reorderWorkArray.length < N) {
            reorderWorkArray = new TagItemView[2*reorderWorkArray.length];
        }
        int index = 0;
        for (TagItemView tiv : input) reorderWorkArray[index ++] = tiv;

        int storedWidthVariant = 0;
        int storedNSelected = 0;
        int[] storedIndexes = new int[N];


        int[] workIndexes = new int[N];
        int nMax = (int) Math.pow(2, N) - 1;
        for (int mask=1; mask <= nMax; mask++) {

            //Build selected subset in the workArr. nSelected is the size of subset
            int checkMask = 1;
            int nSelected=0;
            for (int i=0; i<N; i++) {
                if ((mask & checkMask) != 0) {
                    workIndexes[nSelected ++] = i;
                }
                checkMask = checkMask << 1;
            }

            // Calculate total length
            int widthVariant = -attrMinItemToItemDistance;
            for (int i=0; i<nSelected; i++) {
                widthVariant += (attrMinItemToItemDistance + reorderWorkArray[workIndexes[i]].measuredW);
            }
            if (widthVariant > parentW) {
                continue;
            } else if (widthVariant > storedWidthVariant) {
                storedWidthVariant = widthVariant;
                storedNSelected = nSelected;
                for (int i=0; i<nSelected; i++) {
                    storedIndexes[i] = workIndexes[i];
                }
            }

            // Check for early exit
            if (storedWidthVariant > lengthEarlyThreshold) {
                break;
            }
        }

        // Create and add new row
        RowModel row = convertRows.isEmpty() ? new RowModel() : convertRows.pop();
        row.rowItems.clear();
        row.rowHeight = 0;
        for (int i=0; i<storedNSelected; i++) {
            int selIndex = storedIndexes[i];
            TagItemView tiv = reorderWorkArray[selIndex];
            row.rowItems.add(tiv);
            if (row.rowHeight < tiv.measuredH) row.rowHeight = tiv.measuredH;
            reorderWorkArray[selIndex] = null;
        }

        // Update what's left
        input.clear();
        for (int i=0; i<N; i++) {
            if (reorderWorkArray[i] != null) input.add(reorderWorkArray[i]);
        }

        return row;
    }

    private boolean[] remeasuredTracker = new boolean[16];
    private void reMeasureRowToAllocateFreeSpace(RowModel row, int parentW) {
        if (row.rowItems.size() == 1) {
            row.rowItems.get(0).measure(View.MeasureSpec.makeMeasureSpec(parentW, MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(row.rowHeight, MeasureSpec.EXACTLY));
        } else {
            final int nItems = row.rowItems.size();

            //Step 1: Reset index tracker array for re-measured items
            if (remeasuredTracker.length < nItems) remeasuredTracker = new boolean[2*remeasuredTracker.length];
            for (int i=0; i<nItems; i++) remeasuredTracker[i] = false;

            int undistributedSpace = parentW - attrMinItemToItemDistance * (nItems - 1);
            int n_items_left = nItems;
            do {
                int desiredItemW = undistributedSpace / n_items_left;

                boolean desiredItemWDirty = false;
                int pos = 0;
                for (Iterator<TagItemView> itr = row.rowItems.iterator(); itr.hasNext(); pos ++) {
                    final int itemW = itr.next().measuredW;
                    if (!remeasuredTracker[pos] && (itemW >= desiredItemW)) {
                        remeasuredTracker[pos] = true;
                        n_items_left --;
                        undistributedSpace -= itemW;
                        desiredItemWDirty = true;
                    }
                }

                if (!desiredItemWDirty) {
                    // Now we are OK with current desired item width -> re-measure what's left
                    for (Iterator<TagItemView> itr = row.rowItems.iterator(); itr.hasNext(); pos ++) {
                        TagItemView tiv = itr.next();
                        if (!remeasuredTracker[pos]) {
                            final int specW = View.MeasureSpec.makeMeasureSpec(desiredItemW, MeasureSpec.EXACTLY);
                            final int specH = View.MeasureSpec.makeMeasureSpec(tiv.measuredH, MeasureSpec.EXACTLY);
                            tiv.measure(specW, specH);
                            n_items_left --;
                        }
                    }
                }

            } while (n_items_left > 0);
        }
    }

    public int getRowCount() {
        return mMeasuredRows.size();
    }


    /********************************  Layout section  ********************************************/
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed || isLayoutInvalid) {
            isLayoutInvalid = false;
            int rowTop = getPaddingTop();
            final int maxBottom = bottom - top - getPaddingBottom();
            final int rowStart = getPaddingLeft();
            final int rowEnd = right - left - getPaddingRight();
            int rowPos = 1;
            for (RowModel row : mMeasuredRows) {
                if ((rowTop + row.rowHeight) <= maxBottom) {
                    layoutRow(row, rowStart, rowTop, rowEnd, (rowPos == mMeasuredRows.size()));
                } else {
                    for (TagItemView tiv : row.rowItems) tiv.mainView.setTag(tiv.isLaidOut = false);
                }
                rowTop += (row.rowHeight + attrMinItemToItemDistance);
                rowPos ++;
            }
        }
    }

    private void layoutRow(RowModel row, int left, int top, int right, boolean lastRow) {
        final int nChildren = row.rowItems.size();
        int totContentW = 0;
        for (TagItemView tiv : row.rowItems) totContentW += tiv.measuredW;

        if (attrAllocateFreeSpace || attrItemsHorizontalGravity == Gravity.LEFT) {
            //Do nothing here - start from the original 'left'

        } else if (attrItemsHorizontalGravity == Gravity.RIGHT) {
            //Define left start position for the RIGHT gravity
            left = right - (totContentW + attrMinItemToItemDistance*(nChildren - 1));

        } else if (attrItemsHorizontalGravity == Gravity.CENTER_HORIZONTAL) {
            int dx = (right - left - (totContentW + attrMinItemToItemDistance*(nChildren - 1))) / 2;
            if (dx > 0) left += dx;
        } else {
            throw new IllegalStateException("Unsupported items horizontal gravity - "+attrItemsHorizontalGravity);
        }

        for (TagItemView tiv : row.rowItems) {
            tiv.mainView.layout(left, top, left + tiv.measuredW, top + tiv.measuredH);
            tiv.mainView.setTag(true);
            tiv.isLaidOut = true;
            left += (tiv.measuredW + attrMinItemToItemDistance);
        }
    }

    /************************************  Drawing section  ***************************************/
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        //--- Check if a child is ready to be drawn ---
        Boolean tag = (Boolean) child.getTag();
        if (tag == null) {
            end_check:
            for (RowModel row : mMeasuredRows) {
                for (TagItemView tiv : row.rowItems) {
                    if (tiv.mainView == child) {
                        if (!tiv.isLaidOut) {
                            return false;
                        } else {
                            break end_check;
                        }
                    }
                }
            }
        } else if (!tag) {
            return false;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

}
