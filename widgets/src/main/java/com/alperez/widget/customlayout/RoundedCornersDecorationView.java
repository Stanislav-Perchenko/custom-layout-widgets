package com.alperez.widget.customlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by stanislav.perchenko on 10.04.2020 at 14:53.
 */
public class RoundedCornersDecorationView extends View {

    private int radiusLeftTop = 0;
    private int radiusTopRight = 0;
    private int radiusRightBottom = 0;
    private int radiusBottomLeft = 0;
    private int fillColor = Color.TRANSPARENT;

    private Paint mPaint;
    private final Path figure;

    {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.TRANSPARENT);
        figure = new Path();
        figure.setFillType(Path.FillType.INVERSE_EVEN_ODD);
    }

    public RoundedCornersDecorationView(Context context) {
        super(context);
    }

    public RoundedCornersDecorationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractAttrs(context, attrs);
    }

    public RoundedCornersDecorationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RoundedCornersDecorationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractAttrs(context, attrs);
    }

    private void extractAttrs(Context c, AttributeSet attrs) {
        TypedArray a = c.getResources().obtainAttributes(attrs, R.styleable.RoundedCornersDecorationView);
        int allCorners = a.getDimensionPixelSize(R.styleable.RoundedCornersDecorationView_cornerRadius, 0);
        if (allCorners > 0) {
            radiusLeftTop = radiusTopRight = radiusRightBottom = radiusBottomLeft = allCorners;
        }
        radiusLeftTop = a.getDimensionPixelSize(R.styleable.RoundedCornersDecorationView_cornerLeftTop, radiusLeftTop);
        radiusTopRight = a.getDimensionPixelSize(R.styleable.RoundedCornersDecorationView_cornerTopRight, radiusTopRight);
        radiusRightBottom = a.getDimensionPixelSize(R.styleable.RoundedCornersDecorationView_cornerRightBottom, radiusRightBottom);
        radiusBottomLeft = a.getDimensionPixelSize(R.styleable.RoundedCornersDecorationView_cornerBottomLeft, radiusBottomLeft);
        fillColor = a.getColor(R.styleable.RoundedCornersDecorationView_fillColor, Color.TRANSPARENT);
        mPaint.setColor(fillColor);
        a.recycle();
    }

    public void setFillColor(int fillColor) {
        if (this.fillColor != fillColor) {
            mPaint.setColor(this.fillColor = fillColor);
            invalidate();
        }
    }



    private final RectF arcOval = new RectF();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int W = getMeasuredWidth();
        final int H = getMeasuredHeight();

        figure.reset();
        figure.moveTo(radiusLeftTop, 0);
        figure.lineTo(W - radiusTopRight,0);
        if (radiusTopRight > 0) {
            arcOval.set(W - 2*radiusTopRight, 0, W, 2*radiusTopRight);
            figure.arcTo(arcOval, 270, 90, false);
        }
        figure.lineTo(W, H - radiusRightBottom);
        if (radiusRightBottom > 0) {
            arcOval.set(W - 2*radiusRightBottom, H - 2*radiusRightBottom, W, H);
            figure.arcTo(arcOval, 0, 90, false);
        }
        figure.lineTo(radiusBottomLeft, H);
        if (radiusBottomLeft > 0) {
            arcOval.set(0, H - 2*radiusBottomLeft, 2*radiusBottomLeft, H);
            figure.arcTo(arcOval, 90, 90, false);
        }
        figure.lineTo(0, radiusLeftTop);
        if (radiusLeftTop > 0) {
            arcOval.set(0, 0, 2*radiusLeftTop, 2*radiusLeftTop);
            figure.arcTo(arcOval, 180, 90, false);
        }
        figure.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(figure, mPaint);
    }

}
