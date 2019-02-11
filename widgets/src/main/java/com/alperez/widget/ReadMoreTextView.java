package com.alperez.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.alperez.widget.customlayout.R;

/**
 * Created by stanislav.perchenko on 2/8/2019
 */
public class ReadMoreTextView extends TextView {

    public interface OnReadMoreClickListener {
        void onReadMore(ReadMoreTextView v);
    }

    public ReadMoreTextView(Context context) {
        super(context);
    }

    public ReadMoreTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractAttrs(context, attrs, 0, 0);
    }

    public ReadMoreTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAttrs(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReadMoreTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractAttrs(context, attrs, defStyleAttr, defStyleRes);
    }

    private void extractAttrs(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = c.getResources().obtainAttributes(attrs, R.styleable.ReadMoreTextView);
        mOriginalReadMore = a.getString(R.styleable.ReadMoreTextView_readMore_text);
        isReadMoreClickable = a.getBoolean(R.styleable.ReadMoreTextView_readMore_clickable, false);
        attrLoadMoreTextColor = a.getColor(R.styleable.ReadMoreTextView_readMore_textColor,0);
        attrLoadMoreTextStyle = a.getInt(R.styleable.ReadMoreTextView_readMore_textStyle,0);
        a.recycle();

    }

    private OnReadMoreClickListener onReadMoreClickListener;

    private boolean isReadMoreClickable;
    private String mOriginalReadMore;
    private int attrLoadMoreTextColor;
    private int attrLoadMoreTextStyle;

    public void setOnReadMoreClickListener(@Nullable OnReadMoreClickListener l) {
        this.onReadMoreClickListener = l;
    }

    private boolean isTextDirty;
    private boolean isReadMoreShowing;

    public void setReadMoreText(@Nullable CharSequence rm) {
        if (!TextUtils.equals(rm, mOriginalReadMore)) {
            mOriginalReadMore = (rm == null) ? null : rm.toString();
            if (isReadMoreShowing) {
                isReadMoreShowing = false;
                super.setText(mOrigText, (mOrigBuffType == null) ? BufferType.NORMAL : mOrigBuffType);
            }
            isTextDirty = true;
            requestLayout();
            invalidate();
        }
    }

    @Override
    public void setMaxLines(int maxLines) {
        int oldLines = getMaxLines();
        super.setMaxLines(maxLines);
        if (getMaxLines() != oldLines) {
            if (isReadMoreShowing) {
                isReadMoreShowing = false;
                super.setText(mOrigText, (mOrigBuffType == null) ? BufferType.NORMAL : mOrigBuffType);
            }
            isTextDirty = true;
        }
    }

    private BufferType mOrigBuffType;
    private CharSequence mOrigText;
    @Override
    public void setText(CharSequence text, BufferType type) {
        isTextDirty = !TextUtils.equals(text, mOrigText) || (type != mOrigBuffType);
        mOrigBuffType = type;
        mOrigText = text;

        if (isTextDirty || isReadMoreShowing) {
            isReadMoreShowing = false;
            super.setText(text, type);

        }
    }

    public CharSequence getReadMoreText() {
        return mOriginalReadMore;
    }

    private BufferType getOriginalBufferType() {
        return (mOrigBuffType == null) ? BufferType.NORMAL : mOrigBuffType;
    }










    private int lastMeasureSpecW, lastMeasureSpecH;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (TextUtils.isEmpty(mOrigText)) return;

        if ((lastMeasureSpecW != widthMeasureSpec) || (lastMeasureSpecH != heightMeasureSpec)) {
            lastMeasureSpecW = widthMeasureSpec;
            lastMeasureSpecH = heightMeasureSpec;
            isTextDirty = true;
            if (isReadMoreShowing) {
                isReadMoreShowing = false;
                post(() -> {
                    super.setText(mOrigText, getOriginalBufferType());
                    requestLayout();
                    invalidate();
                });
                return;
            }
        }


        if (!TextUtils.isEmpty(mOriginalReadMore) && isTextDirty && !isReadMoreShowing) {
            final Layout layout = getLayout();
            final int lastLineIndex = layout.getLineCount() - 1;
            int start = layout.getLineStart(0);
            int end = layout.getLineEnd(lastLineIndex);

            String finReadMore = (mOriginalReadMore.charAt(0) == ' ') ? mOriginalReadMore : (" " + mOriginalReadMore);
            if (getText().length() > (end - start)) {
                final int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

                final float readMoreWidth = getPaint().measureText(finReadMore);
                final int targetMaxLastLine = contentWidth - (int) (1.12f * readMoreWidth);

                final int lastLineStart = layout.getLineStart(lastLineIndex);
                final int lastLineEnd = layout.getLineEnd(lastLineIndex);
                String lastLine = getText().toString().substring(lastLineStart, lastLineEnd);

                int finTextEnd = lastLineStart + lastLine.length() * targetMaxLastLine / contentWidth;

                String finTextAndMore = getText().toString().substring(0, finTextEnd) + finReadMore;

                Spannable spanText = new SpannableString(finTextAndMore);
                spanText.setSpan(new StyleSpan(attrLoadMoreTextStyle), finTextEnd + 1, finTextAndMore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanText.setSpan(new ForegroundColorSpan(attrLoadMoreTextColor), finTextEnd + 1, finTextAndMore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                super.setText(spanText, getOriginalBufferType());
                isReadMoreShowing = true;
            }
            isTextDirty = false;
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            Log.e(getClass().getSimpleName(), String.format("onLayout() changed: l=%d, t=%d, r=%d, b=%d", left, top, right, bottom));
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(getClass().getSimpleName(), ev.toString());
        return super.onTouchEvent(ev);
    }
}
