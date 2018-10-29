package com.alperez.widget.customlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
 * Created by stanislav.perchenko on 10/24/2018
 */
public class TagsLayout extends FrameLayout {
    public static final int DEFAULT_TEXT_SIZE = 16;
    public static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final int DEFAULT_TEXT_STYLE = Typeface.NORMAL;

    public interface OnTagClickListener {
        void onTagClicked(ColoredCharSequence tag);
    }

    /***************************  Control attributes  ********************************/

    //--- Layout-related attributes  ---
    private int attrItemsHorizontalGravity = Gravity.LEFT;
    private int attrMinItemToItemDistance = 10;
    private int attrMaxItemToItemDistance = 40;
    private boolean attrUseMaxItemDistance;
    private boolean attrUseExtraSpace;
    private boolean attrAutoReorderItems;

    //--- Item style-related attributes ---
    private int attrItemLayoutResId = -1;
    private int attrTextViewResId = -1;

    private int attrTextSize;
    private int attrTextColor;
    private int attrTextStyle;
    private Drawable attrItemBackground;
    private Integer optFallbackItemBgColor;
    private boolean attrUseDataItemsColor = true;


    private OnTagClickListener onTagClickListener;
    private final LayoutInflater inflater;


    public TagsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagsLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);
        extractCustomAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagsLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflater = LayoutInflater.from(context);
        extractCustomAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void extractCustomAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.textSize}, defStyleAttr, defStyleRes);
        attrTextSize = a.getDimensionPixelSize(0, DEFAULT_TEXT_SIZE);
        a.recycle();
        a = getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.textColor}, defStyleAttr, defStyleRes);
        attrTextColor = a.getColor(0, DEFAULT_TEXT_COLOR);
        a.recycle();
        a = getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.textStyle}, defStyleAttr, defStyleRes);
        attrTextStyle = a.getInt(0, DEFAULT_TEXT_STYLE);
        a.recycle();

        a = getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.gravity}, defStyleAttr, defStyleRes);
        if (a.hasValue(0)) {
            int g = a.getInt(0,0);
            if (g == Gravity.LEFT || g == Gravity.CENTER_HORIZONTAL || g == Gravity.RIGHT) {
                attrItemsHorizontalGravity = g;
            } else {
                throw new IllegalStateException("Items gravity must be one of the LEFT, RIGHT, CENTER_HORIZONTAL");
            }
        }
        a.recycle();

        a = getContext().getResources().obtainAttributes(attrs, R.styleable.TagsLayout);
        attrItemBackground = a.getDrawable(R.styleable.TagsLayout_tl_itemBackground);
        optFallbackItemBgColor = a.hasValue(R.styleable.TagsLayout_tl_fallbackItemColor) ? a.getColor(R.styleable.TagsLayout_tl_fallbackItemColor, 0) : null;
        attrUseDataItemsColor = a.getBoolean(R.styleable.TagsLayout_tl_useDataItemsColor, attrUseDataItemsColor);

        attrItemLayoutResId = a.getResourceId(R.styleable.TagsLayout_tl_itemLayoutResId, attrItemLayoutResId);
        attrTextViewResId = a.getResourceId(R.styleable.TagsLayout_tl_textViewResId, attrTextViewResId);

        if (a.hasValue(R.styleable.TagsLayout_tl_min_item_distance) && !a.hasValue(R.styleable.TagsLayout_tl_max_item_distance_for_reordered)) {
            attrMinItemToItemDistance = a.getDimensionPixelSize(R.styleable.TagsLayout_tl_min_item_distance, 0);
            attrUseMaxItemDistance = false;
        } else if (a.hasValue(R.styleable.TagsLayout_tl_min_item_distance) && a.hasValue(R.styleable.TagsLayout_tl_max_item_distance_for_reordered)) {
            attrMinItemToItemDistance = a.getDimensionPixelSize(R.styleable.TagsLayout_tl_min_item_distance, 0);
            attrMaxItemToItemDistance = a.getDimensionPixelSize(R.styleable.TagsLayout_tl_max_item_distance_for_reordered, 0);
            if (3*attrMinItemToItemDistance > attrMaxItemToItemDistance) {
                throw new IllegalStateException("The 'tl_max_item_distance_for_reordered' must be at least 3 times greater then the 'tl_min_item_distance'");
            } else {
                attrUseMaxItemDistance = true;
            }
        } else {
            attrUseMaxItemDistance = false;
        }

        attrUseExtraSpace = a.getBoolean(R.styleable.TagsLayout_tl_useExtraSpace, attrUseExtraSpace);
        attrAutoReorderItems = a.getBoolean(R.styleable.TagsLayout_tl_autoReorder, attrAutoReorderItems);
        a.recycle();
    }

    private final List<ColoredCharSequence> mData = new ArrayList<>();
    private final List<TagItemView> mTagItemViews = new LinkedList<>();

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }


    public void setUseExtraSpace(boolean useExtSpace) {
        if (attrUseExtraSpace != useExtSpace) {
            attrUseExtraSpace = useExtSpace;
            invalidate();
            requestLayout();
        }
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


    public void setTags(ColoredCharSequence... tags) {
        if (mData.size() == tags.length) {
            boolean eq = true;
            int i = 0;
            for (Iterator<ColoredCharSequence> itr = mData.iterator(); itr.hasNext(); i++) {
                if (!itr.next().equals(tags[i])) {
                    eq = false; break;
                }
            }
            if (eq) return;
        }

        mData.clear();
        for (ColoredCharSequence tag : tags) mData.add(tag);
        updateDataset();
    }

    public void setTags(Collection<? extends ColoredCharSequence> tags) {
        if (mData.size() == tags.size()) {
            boolean eq = true;
            Iterator<? extends ColoredCharSequence> itr2 = tags.iterator();
            for (Iterator<ColoredCharSequence> itr1 = mData.iterator(); itr1.hasNext();) {
                if (!itr1.next().equals(itr2.next())) {
                    eq = false; break;
                }
            }
            if (eq) return;
        }

        mData.clear();
        for (ColoredCharSequence tag : tags) mData.add(tag);
        updateDataset();
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
        TagItemView[] prepViewItems = new TagItemView[mData.size()];

        //Step 1. Look for already initialized items
        for (int i=0; i<prepViewItems.length; i++) {
            ColoredCharSequence tag = mData.get(i);
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
                prepViewItems[i] = mTagItemViews.isEmpty() ? buildAndAddNewTagViewItem() : mTagItemViews.remove(0);
                prepViewItems[i].setData(mData.get(i));
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



    private TagItemView buildAndAddNewTagViewItem() {
        TagItemView tiv;
        if (attrItemLayoutResId > 0 && attrTextViewResId > 0) {
            View v = inflater.inflate(attrItemLayoutResId, this, false);
            tiv = new TagItemView(v, (TextView) v.findViewById(attrTextViewResId));
            tiv.textView.setMaxLines(1);
            tiv.textView.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            TextView tv = new TextView(getContext());
            tv.setMaxLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setTextColor(attrTextColor);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrTextSize);
            tv.setTypeface(tv.getTypeface(), attrTextStyle);
            if (attrItemBackground != null) {
                tv.setBackground(attrItemBackground.getConstantState().newDrawable().mutate());
            } else {
                int color = (!attrUseDataItemsColor && optFallbackItemBgColor != null) ? optFallbackItemBgColor : 0x00FFFFFF;
                tv.setBackground(new ColorDrawable(color));
            }
            tiv = new TagItemView(tv, tv);
        }

        super.addView(tiv.mainView);
        return tiv;
    }


    private class TagItemView {
        final View mainView;
        final TextView textView;
        ColoredCharSequence data;
        boolean isMeasured;
        int measuredW, measuredH;
        private boolean isLaidOut;
        private Drawable originalBackground, workingBackground;

        public TagItemView(View mainView, TextView textView) {
            if (mainView == null) {
                throw new IllegalArgumentException("Main View is null");
            } else {
                this.mainView = mainView;
            }
            if (textView == null) {
                throw new IllegalArgumentException("TextView is null");
            } else {
                this.textView = textView;
            }

            originalBackground = mainView.getBackground().getConstantState().newDrawable().mutate();
            workingBackground = mainView.getBackground().mutate();

            mainView.setOnClickListener(v -> {
                if ((onTagClickListener != null) && (data != null)) onTagClickListener.onTagClicked(data);
            });
        }

        void setData(ColoredCharSequence data) {
            if ((this.data == null) || !this.data.equals(data)) {
                textView.setText(this.data = data);
                if (attrUseDataItemsColor && data.hasColor()) {
                    if (mainView.getBackground() != workingBackground) mainView.setBackground(workingBackground);
                    setTextItemColor(mainView, data.color());
                } else if (mainView.getBackground() != originalBackground) {
                    mainView.setBackground(originalBackground);
                }
                invalidateMeasure();
            }
        }

        void measure(int specW, int specH) {
            mainView.measure(specW, specH);
            measuredW = mainView.getMeasuredWidth();
            measuredH = mainView.getMeasuredHeight();
            isMeasured = true;
        }

        void invalidateMeasure() {
            textView.forceLayout();
            mainView.forceLayout();
            isMeasured = false;
            measuredW = 0;
            measuredH = 0;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        isLayoutInvalid = true;
    }



    private int lastMeasuredContentWidth = -1;
    private int lastMeasuredContentHeight = -1;


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

    private Deque<RowModel> mRecycledRows = new LinkedList<>();
    private List<RowModel> mMeasuredRows = new LinkedList<>();




    private class RowModel {
        int rowHeight;
        final List<TagItemView> rowItems = new LinkedList<>();
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

    public int getRowCount() {
        return mMeasuredRows.size();
    }


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

        int finItemSpace;
        if (attrUseExtraSpace && (nChildren > 1) && !lastRow) {
            finItemSpace = Math.max(Math.round((float)(right - left - totContentW) / (nChildren - 1)), 0);
            if (attrAutoReorderItems && attrUseMaxItemDistance && (finItemSpace > attrMaxItemToItemDistance)) {
                finItemSpace = attrMaxItemToItemDistance;
                if (attrItemsHorizontalGravity == Gravity.LEFT) {
                    // Leave 'left' as it is
                } else if (attrItemsHorizontalGravity == Gravity.RIGHT) {
                    //Define left start position for the RIGHT gravity
                    left = right - (totContentW + finItemSpace*(nChildren - 1));
                } else if (attrItemsHorizontalGravity == Gravity.CENTER_HORIZONTAL) {
                    int dx = (right - left - (totContentW + finItemSpace*(nChildren - 1))) / 2;
                    if (dx > 0) left += dx;
                }
            }

        } else if (attrItemsHorizontalGravity == Gravity.LEFT) {
            //Do nothing here - start from the original 'left'
            finItemSpace = attrMinItemToItemDistance;

        } else if (attrItemsHorizontalGravity == Gravity.RIGHT) {
            //Define left start position for the RIGHT gravity
            finItemSpace = attrMinItemToItemDistance;
            left = right - (totContentW + finItemSpace*(nChildren - 1));

        } else if (attrItemsHorizontalGravity == Gravity.CENTER_HORIZONTAL) {
            finItemSpace = attrMinItemToItemDistance;
            int dx = (right - left - (totContentW + finItemSpace*(nChildren - 1))) / 2;
            if (dx > 0) left += dx;
        } else {
            throw new IllegalStateException("Unsupported items horizontal gravity - "+attrItemsHorizontalGravity);
        }

        for (TagItemView tiv : row.rowItems) {
            tiv.mainView.layout(left, top, left + tiv.measuredW, top + tiv.measuredH);
            tiv.mainView.setTag(true);
            tiv.isLaidOut = true;
            left += (tiv.measuredW + finItemSpace);
        }

    }


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

    /**********************************************************************************************/
    private  static void setTextItemColor(View v, int color) {
        Drawable bg = v.getBackground();
        if (bg == null) {
            v.setBackground(new ColorDrawable(color));
        } else if (bg instanceof ColorDrawable) {
            ((ColorDrawable) bg.mutate()).setColor(color);
        } else if (bg instanceof GradientDrawable) {
            ((GradientDrawable) bg.mutate()).setColor(color);
        } else if (v instanceof TextView) {
            ((TextView) v).setTextColor(color);
        }
    }

}
