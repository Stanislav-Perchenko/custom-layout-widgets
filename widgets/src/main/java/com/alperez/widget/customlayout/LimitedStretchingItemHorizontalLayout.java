package com.alperez.widget.customlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;


/**
 * Created by stanislav.perchenko on 22.06.2020 at 18:22.
 */
public class LimitedStretchingItemHorizontalLayout extends ViewGroup {

    private int limitedStratchingItemId = -1;

    private int mGravity = Gravity.TOP;


    public LimitedStretchingItemHorizontalLayout(Context context) {
        super(context);
    }

    public LimitedStretchingItemHorizontalLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LimitedStretchingItemHorizontalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LimitedStretchingItemHorizontalLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LimitedStretchingItemHorizontalLayout, defStyleAttr, defStyleRes);
            limitedStratchingItemId = a.getResourceId(R.styleable.LimitedStretchingItemHorizontalLayout_limitedStratchingItemId, -1);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.gravity, defStyleAttr, defStyleRes});
            if (a.hasValue(0)) {
                setGravity(a.getInt(0, mGravity));
            }
            a.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        int maxHeight = 0;
        int totalWidth = 0;
        int widthUsed = 0;
        int childState = 0;
        int limitedItemIndex = -1;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                if (child.getId() == limitedStratchingItemId) limitedItemIndex = i;
                final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();

                //Change child width to WRAP_CONTENT if it was MATCH_PARENT
                if (lp.width == FrameLayout.LayoutParams.MATCH_PARENT) {
                    lp.width = FrameLayout.LayoutParams.WRAP_CONTENT;
                    child.setLayoutParams(lp);
                }

                measureChildWithMargins(child, widthMeasureSpec, widthUsed, heightMeasureSpec, 0);

                final int childW = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                if (i != limitedItemIndex) {
                    widthUsed += childW;
                }
                totalWidth += childW;
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);

                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }
        if (limitedItemIndex < 0 && (count > 0) && (getChildAt(0).getVisibility() != View.GONE)) {
            limitedItemIndex = 0;
        }

        // Account for padding too
        totalWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        totalWidth = Math.max(totalWidth, getSuggestedMinimumWidth());

        final int measuredW = resolveSizeAndState(totalWidth, widthMeasureSpec, childState);
        final int measuredH = resolveSizeAndState(maxHeight, heightMeasureSpec, (childState << MEASURED_HEIGHT_STATE_SHIFT));
        setMeasuredDimension(measuredW, measuredH);

        final int actualContainerWidth = measuredW & MEASURED_SIZE_MASK;
        final boolean isContainerTooSmall = (measuredW & MEASURED_STATE_TOO_SMALL) > 0;

        // Re-measure child to be limited
        if ((limitedItemIndex >= 0) && (isContainerTooSmall || (actualContainerWidth < totalWidth))) {
            final View child = getChildAt(limitedItemIndex);
            final FrameLayout.LayoutParams lpOriginal = (FrameLayout.LayoutParams) child.getLayoutParams();

            FrameLayout.LayoutParams lpNew = (FrameLayout.LayoutParams) generateLayoutParams(lpOriginal);
            lpNew.width = Math.max(0, actualContainerWidth - (totalWidth - (child.getMeasuredWidth() & MEASURED_SIZE_MASK)));
            child.setLayoutParams(lpNew);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            child.setLayoutParams(lpOriginal);  //Restore original LayourParams
            // for correct work in Lists with View recycling
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        final int parentLeft = getPaddingLeft();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        int cumulativeChildLeft = parentLeft;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();


                int gravity = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
                if (gravity == 0) {
                    gravity = mGravity;
                }

                int childTop;
                switch (gravity) {
                    case Gravity.TOP:
                        childTop = parentTop + lp.topMargin;
                        break;
                    case Gravity.CENTER_VERTICAL:
                        childTop = parentTop + (parentBottom - parentTop - height) / 2 + (lp.topMargin - lp.bottomMargin);
                        break;
                    case Gravity.BOTTOM:
                        childTop = parentBottom - height - lp.bottomMargin;
                        break;
                    default:
                        childTop = parentTop + lp.topMargin;
                }

                cumulativeChildLeft += lp.leftMargin;

                child.layout(cumulativeChildLeft, childTop, cumulativeChildLeft + width, childTop + height);

                cumulativeChildLeft += (width + lp.rightMargin);

            }
        }
    }

    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            int newGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

            mGravity = (newGravity == 0) ? Gravity.TOP : newGravity;
            requestLayout();
        }
    }
    public int getGravity() {
        return mGravity;
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof FrameLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof FrameLayout.LayoutParams) {
            return new FrameLayout.LayoutParams((FrameLayout.LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new FrameLayout.LayoutParams((MarginLayoutParams) lp);
        } else {
            return new FrameLayout.LayoutParams(lp);
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return getClass().getName();
    }
}
