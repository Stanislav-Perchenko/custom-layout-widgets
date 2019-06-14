package com.alperez.widget.customlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DiscreteSeekBar extends ViewGroup {
    public static final float DEFAULT_TEXT_SIZE_SP = 9;
    public static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final int COLOR_AXIS_POINT = 0xFF060000;
    public static final int COLOR_AXIS_UNSELECTED = 0xFFAFAFAF;
    public static final int COLOR_AXIS_SELECTED = 0xFF00B09B;
    public static final int COLOR_AXIS_SELECTED_PRESSED = 0xFF96C93E;
    public static final float AXIS_STROKE_DP = 2.5f;
    public static final float MARKER_TO_AXIS_SPACE_DP = 5f;


    private int attrContentGravity;     ////////////////////////////////////////////////////////////
    private float attrTextSize;         ////////////////////////////////////////////////////////////
    private int attrTextColor;          ////////////////////////////////////////////////////////////
    private int attColorAxisPoint;      ////////////////////////////////////////////////////////////
    private int attColorUnselected;     ////////////////////////////////////////////////////////////
    private int attColorSelected;       ////////////////////////////////////////////////////////////
    private int attrColorHighlighted;    ///////////////////////////////////////////////////////////
    private float attrAxisStrokePx;     ////////////////////////////////////////////////////////////
    private float attrMarkerToAxisMarginPx;     ////////////////////////////////////////////////////
    private boolean canSetRate;         ////////////////////////////////////////////////////////////

    private TextView vMarker;


    private String[] selectionOptions;
    private int mSelectedIndex;

    private RectF mAxisFullRect;
    private RectF[] mAxisSegmentRects;
    private boolean layoutDirty = true;
    private float currentMarkerPositionX;

    public DiscreteSeekBar(Context context) {
        super(context);
        setAttributesDefault(context);
        init(context);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        extractAllAttributes(context, attrs, 0, 0);
        init(context);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAllAttributes(context, attrs, defStyleAttr, 0);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractAllAttributes(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void setAttributesDefault(Context c) {
        attrContentGravity = Gravity.CENTER_VERTICAL;
        attrTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP, c.getResources().getDisplayMetrics());
        attrTextColor = DEFAULT_TEXT_COLOR;

        attColorAxisPoint = COLOR_AXIS_POINT;
        attColorUnselected = COLOR_AXIS_UNSELECTED;
        attColorSelected = COLOR_AXIS_SELECTED;
        attrColorHighlighted = COLOR_AXIS_SELECTED_PRESSED;

        attrAxisStrokePx = AXIS_STROKE_DP * c.getResources().getDisplayMetrics().density;
        attrMarkerToAxisMarginPx = MARKER_TO_AXIS_SPACE_DP * c.getResources().getDisplayMetrics().density;

        canSetRate = true;
    }

    private void extractAllAttributes(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.gravity}, defStyleAttr, defStyleRes);
        attrContentGravity = a.getInt(0, Gravity.CENTER_VERTICAL);
        a.recycle();

        a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.textSize}, defStyleAttr, defStyleRes);
        attrTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP, c.getResources().getDisplayMetrics());
        attrTextSize = a.getDimensionPixelSize(0, Math.round(attrTextSize));
        a.recycle();

        a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.textColor}, defStyleAttr, defStyleRes);
        attrTextColor = a.getColor(0, DEFAULT_TEXT_COLOR);
        a.recycle();

        a = getContext().getResources().obtainAttributes(attrs, R.styleable.DiscreteSeekBar);
        attColorAxisPoint = a.getColor(R.styleable.DiscreteSeekBar_colorAxisPoint, COLOR_AXIS_POINT);
        attColorUnselected = a.getColor(R.styleable.DiscreteSeekBar_colorUnselected, COLOR_AXIS_UNSELECTED);
        attColorSelected = a.getColor(R.styleable.DiscreteSeekBar_colorSelected, COLOR_AXIS_SELECTED);
        attrColorHighlighted = a.getColor(R.styleable.DiscreteSeekBar_colorHighlighted, COLOR_AXIS_SELECTED_PRESSED);


        attrAxisStrokePx = AXIS_STROKE_DP * c.getResources().getDisplayMetrics().density;
        attrAxisStrokePx = a.getDimensionPixelSize(R.styleable.DiscreteSeekBar_axisStrokeWidth, Math.round(attrAxisStrokePx));
        attrMarkerToAxisMarginPx = MARKER_TO_AXIS_SPACE_DP * c.getResources().getDisplayMetrics().density;
        attrMarkerToAxisMarginPx = a.getDimensionPixelSize(R.styleable.DiscreteSeekBar_axisToMarkerSpace, Math.round(attrMarkerToAxisMarginPx));

        canSetRate = a.getBoolean(R.styleable.DiscreteSeekBar_setableManually, true);

        a.recycle();
    }

    private void init(Context c) {
        setWillNotDraw(false);
        vMarker = new TextView(c);
        vMarker.setTextColor(attrTextColor);
        vMarker.setTextSize(TypedValue.COMPLEX_UNIT_PX, attrTextSize);
        vMarker.setText("0");
        if (Build.VERSION.SDK_INT >= 21) {
            vMarker.setBackground(c.getResources().getDrawable(R.drawable.ic_location_seekbar_selector, null));
        } else {
            vMarker.setBackground(c.getResources().getDrawable(R.drawable.ic_location_seekbar_selector));
        }

        MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = Math.round(attrMarkerToAxisMarginPx);
        addView(vMarker, lp);
    }






    public void setSelectionOptions(String... selectionOptions) {
        this.selectionOptions = selectionOptions;
        if (mAxisFullRect == null) mAxisFullRect = new RectF(0, 0, 0, 0);
        if (mAxisSegmentRects == null || mAxisSegmentRects.length != selectionOptions.length) {
            mAxisSegmentRects = new RectF[selectionOptions.length-1];
            for (int i=0; i< mAxisSegmentRects.length; i++) mAxisSegmentRects[i] = new RectF(0, 0, 0, 0);
        }
        mSelectedIndex = 0;
        vMarker.setText(selectionOptions[0]);
        invalidate();
        requestLayout();
    }

    public void setSelectionIndex(int newSelection) {
        if (mSelectedIndex != newSelection) {
            mSelectedIndex = newSelection;
            vMarker.setText(selectionOptions[newSelection]);
            invalidate();
            requestLayout();
        }
    }

    public int getSelectionIndex() {
        return mSelectedIndex;
    }



    public int getSelectionOptionsCount() {
        return selectionOptions.length;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        layoutDirty = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureChildWithMargins(vMarker, widthMeasureSpec, 0, heightMeasureSpec, 0);
        final MarginLayoutParams lp = (MarginLayoutParams)vMarker.getLayoutParams();
        int maxWidth = vMarker.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
        int maxHeight = vMarker.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

        // Account axis height
        maxHeight += attrAxisStrokePx;

        // Account for padding too
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Check against our foreground's minimum height and width
        if (Build.VERSION.SDK_INT >= 23) {
            final Drawable drawable = getForeground();
            if (drawable != null) {
                maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
                maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
            }
        }

        int childState = vMarker.getMeasuredState();
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();


        View child = vMarker;
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int markW = child.getMeasuredWidth();
        final int markH = child.getMeasuredHeight();

        int markLeft;
        int markTop;



        if (selectionOptions.length > 1) {
            markLeft = mSelectedIndex * (parentRight - parentLeft - markW) / (selectionOptions.length - 1) + parentLeft + lp.leftMargin;
        } else {
            markLeft = parentLeft + (parentRight - parentLeft - markW)/2 + lp.leftMargin - lp.rightMargin;
        }

        switch (attrContentGravity) {
            case Gravity.TOP:
                markTop = parentTop + lp.topMargin;
                break;
            case Gravity.CENTER:
            case Gravity.CENTER_VERTICAL:
                markTop = parentTop + ((parentBottom - parentTop - markH) / 2) + lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                markTop = parentBottom - markH - lp.bottomMargin - Math.round(attrAxisStrokePx);
                break;
            default:
                markTop = parentTop + lp.topMargin;
        }

        child.layout(markLeft, markTop, markLeft + markW, markTop + markH);


        float axisLeft = parentLeft + (markW - attrAxisStrokePx)/2f;
        float axisRight = parentRight - (markW - attrAxisStrokePx)/2f;
        float axisTop = markTop + markH + lp.bottomMargin;
        float axisBottom = axisTop + attrAxisStrokePx;

        mAxisFullRect.set(axisLeft, axisTop, axisRight, axisBottom);

        final float segmentW = ((axisRight - axisLeft) - selectionOptions.length* attrAxisStrokePx) / mAxisSegmentRects.length;
        float segStart = axisLeft + attrAxisStrokePx;
        for (int i=0; i<mAxisSegmentRects.length; i++) {
            mAxisSegmentRects[i].set(segStart, axisTop, segStart + segmentW, axisBottom);
            segStart += segmentW + attrAxisStrokePx;
        }

        touchableTop = vMarker.getTop();
        touchableBottom = axisBottom;
        selectionItemWidth_2 = (axisRight - axisLeft)/(2*(selectionOptions.length - 1));
        currentMarkerPositionX = (vMarker.getRight() + vMarker.getLeft()) / 2f;

        layoutDirty = false;
    }



    private Paint mAxisPaint;
    private Paint debugPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAxisPaint == null) {
            mAxisPaint = new Paint();
            mAxisPaint.setStyle(Paint.Style.FILL);
            mAxisPaint.setAntiAlias(true);
        }

        mAxisPaint.setColor(attColorAxisPoint);
        canvas.drawRect(mAxisFullRect, mAxisPaint);

        final int colorSelected = isTouching ? attrColorHighlighted : attColorSelected;

        for (int i=0; i<mAxisSegmentRects.length; i++) {
            mAxisPaint.setColor((i < mSelectedIndex) ? colorSelected : attColorUnselected);
            canvas.drawRect(mAxisSegmentRects[i], mAxisPaint);
        }


        /*if (debugPaint == null) {
            debugPaint = new Paint();
            debugPaint.setAntiAlias(true);
            debugPaint.setStyle(Paint.Style.STROKE);
            debugPaint.setStrokeWidth(1);
            debugPaint.setColor(Color.RED);
        }
        canvas.drawLine(currentMarkerPositionX, 0, currentMarkerPositionX, canvas.getHeight(), debugPaint);*/
    }




    /**********************************************************************************************/
    /*********************  Touch handling subsystem  *********************************************/
    /**********************************************************************************************/
    private boolean isTouching;


    private float touchableTop, touchableBottom;
    private float selectionItemWidth_2;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(canSetRate && isTouchOnMark(event.getX(), event.getY())) {
            isTouching = true;
            vMarker.getBackground().setLevel(1);
            return true;
        }
        return false;
    }

    private boolean isTouchOnMark(float x, float y) {
        return (x > vMarker.getLeft() && x < vMarker.getRight()) && (y > touchableTop && y < touchableBottom);
    }

    private float touchStartDX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouching) {
            float x = event.getX();

            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    touchStartDX = currentMarkerPositionX - x;
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!layoutDirty) {
                        float minX = currentMarkerPositionX + touchStartDX - selectionItemWidth_2;
                        float maxX = currentMarkerPositionX + touchStartDX + selectionItemWidth_2;
                        if (x < minX && mSelectedIndex > 0) {
                            setSelectionIndex(mSelectedIndex - 1);
                        } else if (x > maxX && mSelectedIndex < (selectionOptions.length-1)) {
                            setSelectionIndex(mSelectedIndex + 1);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isTouching = false;
                    vMarker.getBackground().setLevel(0);
                    invalidate();
                    break;
            }
        }
        return true;
    }
}
