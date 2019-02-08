package com.alperez.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.alperez.widget.customlayout.R;

/**
 * Created by stanislav.perchenko on 2/8/2019
 */
public class ReadMoreTextView extends TextView {

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
        //TODO extract attributes for the mOriginalReadMore and isReadMoreClickable
        buildDisplayingReadMore(mOriginalReadMore);
        a.recycle();
    }

    private boolean isReadMoreEnabled;
    private boolean isReadMoreClickable;
    private CharSequence mOriginalReadMore;
    private CharSequence mDisplayReadMore;//This may be spanned
    private boolean isLoadMoreDirty = true;


    public void setReadMoreText(CharSequence rm) {
        if (!TextUtils.equals(rm, mOriginalReadMore)) {
            buildDisplayingReadMore(mOriginalReadMore = rm);
            requestLayout();
            invalidate();
        }
    }

    private void buildDisplayingReadMore(CharSequence rm) {
        //TODO Fill-in the mDisplayReadMore. Use isReadMoreClickable

        isLoadMoreDirty = isReadMoreEnabled = !TextUtils.isEmpty(mDisplayReadMore);
    }
}
