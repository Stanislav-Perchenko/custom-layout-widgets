package com.alperez.widget.customlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * Created by stanislav.perchenko on 6/15/2018
 */
public class EditablePropertyView extends RelativeLayout {
    private boolean isMandatory;

    private boolean isPropertyNameChanged = true;
    private String propertyName;
    private boolean isPropertyIconChanged = true;
    private Drawable propertyIcon;
    private boolean isBgValidMarkChanged = true;
    private Drawable bgValidMark;
    private boolean isMandatoryMarkColorChanged = true;
    private int colorMandatoryMark = 0xFFFF4545;

    private String valueTemplate;
    private int attrInputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
    private int attrImeOptions = EditorInfo.IME_ACTION_NEXT;
    private int attrLayoutHeight;
    private boolean attrEditFocusable = true;
    private int attrMaxLength = 0;
    private Drawable attrActionDrawable;

    private TextView vTxtProperty;
    private ImageView vPropertyIcon;
    private TextView vMarkMandatory;
    private View vMarkValueValid;
    private View vUnderline;
    private ImageView vImageAction;

    private boolean isValueValid;
    private Pattern valueMatchPattern;

    private OnValueValidityListener onValueValidityListener;

    private OnClickListener extClickListener;
    private OnClickListener actionOnClickListener;
    private OnTextChangeListener onTextChangeListener;




    public EditablePropertyView(@NonNull Context context) {
        super(context);
        init();
    }

    public EditablePropertyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        extractAttrs(context, attrs, 0, 0);
        init();
    }

    public EditablePropertyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractAttrs(context, attrs, defStyleAttr, 0);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditablePropertyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        extractAttrs(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        initLayout(getContext());
        onConfigurationChanged();
        validateValueAndNotify();
        updateMarksAndActionVisibility();
    }


    private void extractAttrs(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.inputType}, defStyleAttr, defStyleRes);
        attrInputType = a.getInt(0, attrInputType);
        a.recycle();

        a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.imeOptions}, defStyleAttr, defStyleRes);
        attrImeOptions = a.getInt(0, attrImeOptions);
        a.recycle();

        a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_height}, defStyleAttr, defStyleRes);
        try {
            attrLayoutHeight = a.getDimensionPixelSize(0, 0);
        } catch (java.lang.UnsupportedOperationException e) {
            attrLayoutHeight = a.getInt(0, 0);
        }
        a.recycle();

        a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.focusable}, defStyleAttr, defStyleRes);
        attrEditFocusable = a.getBoolean(0, attrEditFocusable);
        a.recycle();

        a = c.obtainStyledAttributes(attrs, new int[]{android.R.attr.maxLength}, defStyleAttr, defStyleRes);
        attrMaxLength = a.getInteger(0, 0);
        a.recycle();

        a = c.getResources().obtainAttributes(attrs, R.styleable.EditablePropertyView);
        isMandatory = a.getBoolean(R.styleable.EditablePropertyView_isMandatory, false);
        propertyName = a.getString(R.styleable.EditablePropertyView_propertyName);
        if (propertyName == null) propertyName = "";
        propertyIcon = a.getDrawable(R.styleable.EditablePropertyView_propertyIcon);
        bgValidMark = a.getDrawable(R.styleable.EditablePropertyView_validMarkIcon);
        colorMandatoryMark = a.getColor(R.styleable.EditablePropertyView_mandatoryMarkColor, colorMandatoryMark);
        valueTemplate = a.getString(R.styleable.EditablePropertyView_valueTemplateRegex);
        if (!TextUtils.isEmpty(valueTemplate)) valueMatchPattern = Pattern.compile(valueTemplate);
        attrActionDrawable = a.getDrawable(R.styleable.EditablePropertyView_actionDrawable);
        a.recycle();
    }

    private void initLayout(Context context) {
        int layoutResId = ((attrLayoutHeight == LayoutParams.WRAP_CONTENT) & ((attrInputType & InputType.TYPE_TEXT_FLAG_MULTI_LINE) > 0))
                ? R.layout.layout_reg_editable_property_multi_line
                : R.layout.layout_reg_editable_property_single_line;
        LayoutInflater.from(context).inflate(layoutResId, this, true);
        vTxtProperty = (TextView) findViewById(android.R.id.text1);
        vPropertyIcon = (ImageView) findViewById(android.R.id.icon);
        vMarkMandatory = (TextView) findViewById(R.id.mark_mandatory);
        vMarkValueValid = findViewById(R.id.mark_valid);
        vUnderline = findViewById(R.id.underline);
        vImageAction = (ImageView) findViewById(R.id.img_action);

        vPropertyIcon.setEnabled(false);
        vUnderline.setEnabled(false);
        vImageAction.setImageDrawable(attrActionDrawable);


        if (attrInputType != InputType.TYPE_TEXT_FLAG_MULTI_LINE) {
            vTxtProperty.setInputType(attrInputType);
        }
        vTxtProperty.setImeOptions(attrImeOptions);
        vTxtProperty.setFocusable(attrEditFocusable);
        if (attrMaxLength > 0) {
            vTxtProperty.setFilters(new InputFilter[]{new InputFilter.LengthFilter(attrMaxLength)});
        }

        vTxtProperty.setOnFocusChangeListener((v, hasFocus) -> {
            vPropertyIcon.setEnabled(hasFocus);
            vUnderline.setEnabled(hasFocus);
        });
        vTxtProperty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                validateValueAndNotify();
                updateMarksAndActionVisibility();
                if (onTextChangeListener != null) {
                    onTextChangeListener.onTextChanged(s);
                }
            }
        });
        vTxtProperty.setOnClickListener(v -> {
            if (extClickListener != null) extClickListener.onClick(this);
        });

        super.setOnClickListener(v -> {
            vTxtProperty.requestFocus();
            if (extClickListener != null) extClickListener.onClick(this);
        });
    }

    private void validateValueAndNotify() {
        boolean oldValid = isValueValid;
        if (isMandatory) {
            isValueValid = (valueMatchPattern == null)
                    ? (vTxtProperty.getText().length() > 0)
                    : valueMatchPattern.matcher(vTxtProperty.getText()).matches();
        } else {
            isValueValid = true;
        }

        if ((onValueValidityListener != null) && (isValueValid != oldValid)) {
            onValueValidityListener.onValueValidityChanged(this, isValueValid);
        }
    }

    /**
     * This must be called on each configuration change of this component.
     * This will update UI and internal state;
     */
    private void onConfigurationChanged() {
        if (isPropertyIconChanged)
            vPropertyIcon.setImageDrawable(propertyIcon);
        if (isPropertyNameChanged)
            vTxtProperty.setHint(propertyName);
        if (isBgValidMarkChanged)
            vMarkValueValid.setBackground(bgValidMark);
        if (isMandatoryMarkColorChanged)
            vMarkMandatory.setTextColor(colorMandatoryMark);
    }

    private void updateMarksAndActionVisibility() {
        if (isMandatory & !isValueValid) {
            // Show mandatory red mark
            vMarkMandatory.setVisibility(View.VISIBLE);
            vMarkValueValid.setVisibility(View.GONE);
            vImageAction.setVisibility(View.GONE);
        } else if (isMandatory & isValueValid) {

            if ((attrActionDrawable == null) || (actionOnClickListener == null)) {
                // Show mandatory green mark
                vMarkMandatory.setVisibility(View.GONE);
                vMarkValueValid.setVisibility(View.VISIBLE);
                vImageAction.setVisibility(View.GONE);
            } else {
                // Show action
                vMarkMandatory.setVisibility(View.GONE);
                vMarkValueValid.setVisibility(View.GONE);
                vImageAction.setVisibility(View.VISIBLE);
            }

        } else if (!isMandatory) {
            vMarkMandatory.setVisibility(View.GONE);
            vMarkValueValid.setVisibility(View.GONE);
            vImageAction.setVisibility(((actionOnClickListener == null) && (attrActionDrawable != null)) ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        extClickListener = l;
    }

    public void setOnValueValidityListener(OnValueValidityListener l) {
        this.onValueValidityListener = l;
    }

    public void unhighlight() {
        vPropertyIcon.setEnabled(false);
        vUnderline.setEnabled(false);
        vTxtProperty.clearFocus();
    }

    public void requestEditableFocus() {
        vTxtProperty.requestFocus();
    }

    public boolean isPropertyValueValid() {
        return isMandatory ? isValueValid : true;
    }

    public String getPropertyValue() {
        return vTxtProperty.getText().toString();
    }

    public void setPropertyValue(CharSequence propertyValue) {
        vTxtProperty.setText((propertyValue == null) ? "" : propertyValue);
    }

    public void clear() {
        vTxtProperty.setText("");
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        if (this.isMandatory != isMandatory) {
            isMandatory = mandatory;
            validateValueAndNotify();
            updateMarksAndActionVisibility();
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        if (!TextUtils.equals(this.propertyName, propertyName)) {
            this.propertyName = propertyName;
            isPropertyNameChanged = true;
            onConfigurationChanged();
        }
    }

    public Drawable getPropertyIcon() {
        return propertyIcon;
    }

    public void setPropertyIcon(Drawable propertyIcon) {
        this.propertyIcon = propertyIcon;
        isPropertyIconChanged = true;
        onConfigurationChanged();
    }

    public Drawable getValidMarkIcon() {
        return bgValidMark;
    }

    public void setValidMarkIcon(Drawable dr) {
        this.bgValidMark = dr;
        isBgValidMarkChanged = true;
        onConfigurationChanged();
    }



    public String getValueTemplate() {
        return valueTemplate;
    }

    public void setValueTemplate(String valueTemplate) {
        if (!TextUtils.equals(this.valueTemplate, valueTemplate)) {
            this.valueTemplate = valueTemplate;
            valueMatchPattern = TextUtils.isEmpty(valueTemplate) ? null : Pattern.compile(valueTemplate);
            validateValueAndNotify();
            updateMarksAndActionVisibility();
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (vTxtProperty.isEnabled() != enabled) vTxtProperty.setEnabled(enabled);
    }

    public void setOnActionClickListener(OnClickListener l) {
        this.actionOnClickListener = l;
        vImageAction.setOnClickListener((l == null) ? null : (v -> actionOnClickListener.onClick(this)));
        updateMarksAndActionVisibility();
    }

    public void setOnTextChangedListener(OnTextChangeListener l) {
        onTextChangeListener = l;
    }

    public void setTextGravity(int gravity) {
        vTxtProperty.setGravity(gravity);
    }

    public void setTextAlignment(int textAlignment) {
        vTxtProperty.setTextAlignment(textAlignment);
    }





    /*********************************************************************************************1*/
    public interface OnValueValidityListener {
        void onValueValidityChanged(EditablePropertyView v, boolean isValid);
    }

    public interface OnTextChangeListener {
        void onTextChanged(CharSequence text);
    }
}
