package com.alperez.widget.customlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by stanislav.perchenko on 1/10/2020, 5:05 PM.
 */
public class PrepassListItemColorView extends View {

    private float argColorWidth;
    private float argCornerRadius;


    private float colorR_in, colorR_out;
    private float colorD_in, colorD_out;
    private int colorLeft, colorRight;
    private Paint paintLeft, paintRight;

    {
        colorLeft = colorRight = Color.TRANSPARENT;
    }

    public PrepassListItemColorView(Context context) {
        super(context);
        init(context);
    }

    public PrepassListItemColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractAttributes(context, attrs);
        init(context);
    }

    public PrepassListItemColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAttributes(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PrepassListItemColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractAttributes(context, attrs);
        init(context);
    }

    private void extractAttributes(Context c, AttributeSet attrs) {
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PrepassListItemColorView);
        argColorWidth = a.getDimensionPixelSize(R.styleable.PrepassListItemColorView_colorWidth, 0);
        argCornerRadius = a.getDimensionPixelSize(R.styleable.PrepassListItemColorView_colorCorners, 0);
        a.recycle();
    }

    private void init(Context c) {
        if (argColorWidth == 0) argColorWidth = dp2px(c.getResources(), 3.5f);
        if (argCornerRadius == 0) argCornerRadius = dp2px(c.getResources(), 9);

        paintLeft = getPaint(colorLeft);
        paintRight = getPaint(colorRight);

        colorR_out = argCornerRadius;
        colorD_out = 2*colorR_out;
        colorR_in  = colorR_out - argColorWidth;
        colorD_in  = 2*colorR_in;
    }

    public static float dp2px(Resources res, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    private Paint getPaint(int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
        p.setColor(color);
        return p;
    }

    public void setColors(int colorLeft, int colorRight) {
        boolean upd = false;
        if (this.colorLeft != colorLeft) {
            this.colorLeft = colorLeft;
            paintLeft.setColor(colorLeft);
            upd = true;
        }
        if (this.colorRight != colorRight) {
            this.colorRight = colorRight;
            paintRight.setColor(colorRight);
            upd = true;
        }

        if (upd) invalidate();
    }

    private final Path pathLeft = new Path();
    private final Path pathRight = new Path();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int W = getMeasuredWidth();
        final int H = getMeasuredHeight();
        final float center = W / 2f;

        pathRight.reset();
        pathRight.moveTo(center, 0);
        pathRight.lineTo(W - colorR_out, 0);
        pathRight.arcTo(W - colorD_out, 0, W, colorD_out, 270, 90, false);
        pathRight.lineTo(W, H - colorR_out);
        pathRight.arcTo(W - colorD_out, H - colorD_out, W, H, 0, 90, false);
        pathRight.lineTo(center, H);
        pathRight.lineTo(center, H - argColorWidth);
        pathRight.lineTo(W - colorR_out, H - argColorWidth);
        pathRight.arcTo(W - argColorWidth - colorD_in, H - argColorWidth - colorD_in, W - argColorWidth, H - argColorWidth, 90, -90, false);
        pathRight.lineTo(W - argColorWidth, colorR_out);
        pathRight.arcTo(W - argColorWidth - colorD_in, argColorWidth, W - argColorWidth, 0 + argColorWidth + colorD_in, 0, -90, false);
        pathRight.lineTo(center, argColorWidth);
        pathRight.close();

        pathLeft.reset();
        pathLeft.moveTo(center, 0);
        pathLeft.lineTo(colorR_out, 0);
        pathLeft.arcTo(0, 0, colorD_out, colorD_out, 270, -90, false);
        pathLeft.lineTo(0, H - colorR_out);
        pathLeft.arcTo(0, H - colorD_out, colorD_out, H, 180, -90, false);
        pathLeft.lineTo(center, H);
        pathLeft.lineTo(center, H - argColorWidth);
        pathLeft.lineTo(0+colorR_out, H - argColorWidth);
        pathLeft.arcTo(argColorWidth, H - argColorWidth - colorD_in, 0 + argColorWidth + colorD_in, H - argColorWidth, 90, 90, false);
        pathLeft.lineTo(argColorWidth, colorR_out);
        pathLeft.arcTo(argColorWidth, argColorWidth, argColorWidth + colorD_in, argColorWidth + colorD_in, 180, 90, false);
        pathLeft.lineTo(center, argColorWidth);
        pathLeft.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(pathLeft, paintLeft);
        canvas.drawPath(pathRight, paintRight);
    }
}
