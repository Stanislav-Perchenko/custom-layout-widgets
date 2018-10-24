package com.alperez.widget.customlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
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

    /***************************  Control attributes  ********************************/

    //--- Layout-related attributes  ---
    private int attrItemsHorizontalGravity = Gravity.LEFT;
    private int attrMinItemToItemDistance = 10;
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

        attrMinItemToItemDistance = a.getDimensionPixelSize(R.styleable.TagsLayout_tl_item_distance, attrMinItemToItemDistance);
        attrUseExtraSpace = a.getBoolean(R.styleable.TagsLayout_tl_useExtraSpace, attrUseExtraSpace);
        attrAutoReorderItems = a.getBoolean(R.styleable.TagsLayout_tl_autoReorder, attrAutoReorderItems);
        a.recycle();
    }

    private final List<ColoredCharSequence> mData = new ArrayList<>();
    private final List<TagItemView> mTagItemViews = new LinkedList<>();



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

    @Override
    public void requestLayout() {
        isLayoutInvalid = true;
        isMeasurementInvalid = true;
        super.requestLayout();
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
            tv.setTextSize(attrTextSize);
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
        }

        void setData(ColoredCharSequence data) {
            if ((this.data == null) || !this.data.equals(data)) {
                textView.setText(this.data = data);
                if (attrUseDataItemsColor && data.hasColor()) {
                    setTextItemColor(textView, data.color());
                }
                isMeasured = false;
            }
        }

        void measure(int specW, int specH) {
            mainView.measure(specW, specH);
            measuredW = mainView.getMeasuredWidth();
            measuredH = mainView.getMeasuredHeight();
            isMeasured = true;
        }

        void invalidateMeasure() {
            isMeasured = false;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            lastMeasuredContentWidth = -1;
            for (TagItemView tiv : mTagItemViews) tiv.invalidateMeasure();
        }
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
            lastMeasuredContentWidth = contentW;

            int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(contentW, View.MeasureSpec.AT_MOST);
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

    private RowModel getRowWithReorder(List<TagItemView> input, int parentW, Deque<RowModel> convertRows) {
        RowModel row = convertRows.isEmpty() ? new RowModel() : convertRows.pop();
        row.rowItems.clear();
        row.rowHeight = 0;


        final int lengthEarlyThreshold = (int) Math.round(0.95 * parentW);
        final int N = input.size();

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
                widthVariant += (attrMinItemToItemDistance + input.get(workIndexes[i]).measuredW);
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
        for (int i=0; i<storedNSelected; i++) {
            int selIndex = storedIndexes[i];
            row.rowItems.add(input.remove(selIndex));
        }
        return row;
    }

    public int getRowCount() {
        return mMeasuredRows.size();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //TODO Implement this
    }

    /**********************************************************************************************/
    private  static void setTextItemColor(TextView tv, int color) {
        Drawable bg = tv.getBackground();
        if (bg == null) {
            tv.setBackground(new ColorDrawable(color));
        } else if (bg instanceof ColorDrawable) {
            ((ColorDrawable) bg.mutate()).setColor(color);
        } else if (bg instanceof GradientDrawable) {
            ((GradientDrawable) bg.mutate()).setColor(color);
        } else {
            tv.setTextColor(color);
        }
    }
}
