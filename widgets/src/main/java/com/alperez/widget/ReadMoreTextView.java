package com.alperez.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.TextView;

import com.alperez.widget.customlayout.R;

/**
 * Created by stanislav.perchenko on 2/8/2019
 */
public class ReadMoreTextView extends TextView {

    private float MIN_PREFERED_CLICK_AREA;

    public interface OnReadMoreClickListener {
        void onReadMore(ReadMoreTextView v);
    }

    public ReadMoreTextView(Context context) {
        super(context);
        init(context);
    }

    public ReadMoreTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractAttrs(context, attrs, 0, 0);
        init(context);
    }

    public ReadMoreTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAttrs(context, attrs, defStyleAttr, 0);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReadMoreTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractAttrs(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void extractAttrs(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = c.getResources().obtainAttributes(attrs, R.styleable.ReadMoreTextView);
        mOriginalReadMore = a.getString(R.styleable.ReadMoreTextView_readMore_text);
        isReadMoreClickable = a.getBoolean(R.styleable.ReadMoreTextView_readMore_clickable, false);
        attrLoadMoreTextColor = a.getColor(R.styleable.ReadMoreTextView_readMore_textColor,0);
        attrLoadMoreTextStyle = a.getInt(R.styleable.ReadMoreTextView_readMore_textStyle,0);
        a.recycle();
    }

    private void init(Context c) {
        MIN_PREFERED_CLICK_AREA = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, c.getResources().getDisplayMetrics());
    }

    private OnReadMoreClickListener onReadMoreClickListener;

    private boolean isReadMoreClickable;
    private String mOriginalReadMore;
    private int attrLoadMoreTextColor;
    private int attrLoadMoreTextStyle;

    private boolean isTextDirty;
    private boolean isReadMoreShowing;
    private int actualTextLengthReadMoreMode;
    private String finReadMore;
    private float mReadMoreWidth;
    private final RectF mReadMoreClickArea = new RectF();
    private final RectF mReadMoreClickAreaEx = new RectF();



    public void setOnReadMoreClickListener(@Nullable OnReadMoreClickListener l) {
        this.onReadMoreClickListener = l;
    }

    public void setReadMoreText(@Nullable CharSequence rm) {
        if (!TextUtils.equals(rm, mOriginalReadMore)) {
            mOriginalReadMore = (rm == null) ? null : rm.toString();
            if (isReadMoreShowing) {
                isReadMoreShowing = false;
                actualTextLengthReadMoreMode = 0;
                finReadMore = null;
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
                actualTextLengthReadMoreMode = 0;
                finReadMore = null;
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
            actualTextLengthReadMoreMode = 0;
            finReadMore = null;
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
                Log.e(getClass().getSimpleName(), "~~~> Size changed & \"Read More\" is being shown -> fall back to the original text and RETURN");
                isReadMoreShowing = false;
                finReadMore = null;
                actualTextLengthReadMoreMode = 0;
                super.setText(mOrigText, getOriginalBufferType());
                requestLayout();
                invalidate();
                return;
            }
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e(getClass().getSimpleName(), String.format("---> onLayout() changed - %b: l=%d, t=%d, r=%d, b=%d", changed, left, top, right, bottom));
        super.onLayout(changed, left, top, right, bottom);


        final Layout layout = getLayout();
        final int lastLineIndex = layout.getLineCount() - 1;
        if (!TextUtils.isEmpty(mOriginalReadMore) && isTextDirty && !isReadMoreShowing) {
            if (layout.getEllipsisCount(lastLineIndex) > 0) {
                final int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

                finReadMore = (mOriginalReadMore.charAt(0) == ' ') ? mOriginalReadMore : (" " + mOriginalReadMore);
                mReadMoreWidth = getPaint().measureText(finReadMore);
                final int targetMaxLastLine = contentWidth - (int) (1.12f * mReadMoreWidth);

                final int lastLineStart = layout.getLineStart(lastLineIndex);
                String lastLine = getText().toString().substring(lastLineStart, lastLineStart + layout.getEllipsisStart(lastLineIndex));

                int finTextEnd = lastLineStart + lastLine.length() * targetMaxLastLine / contentWidth;

                String finTextAndMore = getText().toString().substring(0, finTextEnd) + finReadMore;

                Spannable spanText = new SpannableString(finTextAndMore);
                spanText.setSpan(new StyleSpan(attrLoadMoreTextStyle), finTextEnd + 1, finTextAndMore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanText.setSpan(new ForegroundColorSpan(attrLoadMoreTextColor), finTextEnd + 1, finTextAndMore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                super.setText(spanText, getOriginalBufferType());
                post(() -> {
                    requestLayout();
                    invalidate();
                });
                isReadMoreShowing = true;
                actualTextLengthReadMoreMode = finTextEnd;
                Log.d(getClass().getSimpleName(), "     ---> Start showing \"Read More\". textLen = "+finTextEnd);
            }
            isTextDirty = false;

        } else if (isReadMoreShowing && (layout.getEllipsisCount(lastLineIndex) > 0) && !changed) {
            int finTextEnd = (-- actualTextLengthReadMoreMode);
            Log.d(getClass().getSimpleName(),"      <--- Ellipsize failed -> new textLength = "+finTextEnd);
            String finTextAndMore = getText().toString().substring(0, finTextEnd) + finReadMore;
            Spannable spanText = new SpannableString(finTextAndMore);
            spanText.setSpan(new StyleSpan(attrLoadMoreTextStyle), finTextEnd + 1, finTextAndMore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanText.setSpan(new ForegroundColorSpan(attrLoadMoreTextColor), finTextEnd + 1, finTextAndMore.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            super.setText(spanText, getOriginalBufferType());
            post(() -> {
                requestLayout();
                invalidate();
            });
        } else if (isReadMoreShowing && !changed) {
            int topPadding = layout.getTopPadding();
            int botPadding = layout.getBottomPadding();
            int w = layout.getWidth();
            int h = layout.getHeight();
            float lineW = layout.getLineWidth(lastLineIndex);
            float lineLeft = layout.getLineLeft(lastLineIndex);
            int lineTop = layout.getLineTop(lastLineIndex);
            float lineRight = layout.getLineRight(lastLineIndex);
            int lineBot = layout.getLineBottom(lastLineIndex);
            int baseline = layout.getLineBaseline(lastLineIndex);
            String text = String.format("topPadding=%d; botPadding=%d; W=%d, H=%d, lineW=%.1f, lineLeft=%.1f, lineTop=%d; lineRight=%.1f; lineBot=%d, baseline=%d",
                    topPadding, botPadding, w, h, lineW, lineLeft, lineTop, lineRight, lineBot, baseline);
            Log.d(getClass().getSimpleName(), "<--- Read more is being shown OK: "+text);

            final float totalLineRight = lineRight + getTotalPaddingLeft();
            mReadMoreClickArea.set(totalLineRight - mReadMoreWidth, lineTop + getTotalPaddingTop(), totalLineRight, lineBot + getTotalPaddingTop());

            float dLeft=0, dTop=0, dRight=0, dBot=0;
            float clickH = mReadMoreClickArea.height();
            if (clickH < MIN_PREFERED_CLICK_AREA) {
                dTop = Math.min((MIN_PREFERED_CLICK_AREA - clickH)/2f, clickH/2f);
                dBot = Math.min((MIN_PREFERED_CLICK_AREA - clickH)/2f, getTotalPaddingBottom());
            }

            float clickW = mReadMoreClickArea.width();
            if (clickW < MIN_PREFERED_CLICK_AREA) {
                dLeft = Math.min((MIN_PREFERED_CLICK_AREA - clickW)/2f, mReadMoreClickArea.left);
                dRight= Math.min((MIN_PREFERED_CLICK_AREA - clickW)/2f, right - left - mReadMoreClickArea.right);
            }
            mReadMoreClickAreaEx.set(mReadMoreClickArea.left - dLeft, mReadMoreClickArea.top - dTop, mReadMoreClickArea.right + dRight, mReadMoreClickArea.bottom + dBot);

        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(getClass().getSimpleName(), ev.toString());
        return super.onTouchEvent(ev);
    }


    private final Paint dbgPaint = new Paint();
    {
        dbgPaint.setStyle(Paint.Style.STROKE);
        dbgPaint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isReadMoreShowing) {
            dbgPaint.setColor(Color.GREEN);
            canvas.drawRect(mReadMoreClickAreaEx, dbgPaint);
            dbgPaint.setColor(Color.RED);
            canvas.drawRect(mReadMoreClickArea, dbgPaint);
        }
    }
}
