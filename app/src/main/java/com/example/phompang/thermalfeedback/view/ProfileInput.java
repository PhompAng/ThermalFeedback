package com.example.phompang.thermalfeedback.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.phompang.thermalfeedback.R;

/**
 * Created by phompang on 12/28/2016 AD.
 */

public class ProfileInput extends FrameLayout {
    private ImageView img;
    private TextInputLayout textInputLayout;
    private TextInputEditText text;

    public ProfileInput(Context context) {
        super(context);
        setUp(null);
    }

    public ProfileInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(attrs);
    }

    public ProfileInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(attrs);
    }

    public ProfileInput(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(attrs);
    }

    private void setUp(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.widget_profile_input, this);
        bind();
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ProfileInput);
            Drawable drawable = typedArray.getDrawable(R.styleable.ProfileInput_pi_img);
            String hint = typedArray.getString(R.styleable.ProfileInput_pi_hint);
            int inputType = typedArray.getInt(R.styleable.ProfileInput_android_inputType, InputType.TYPE_TEXT_VARIATION_NORMAL);
            typedArray.recycle();

            setImg(drawable);
            setHint(hint);
            setInputType(inputType);
        }
    }

    private void bind() {
        img = (ImageView) findViewById(R.id.img);
        textInputLayout = (TextInputLayout) findViewById(R.id.text_layout);
        text = (TextInputEditText) findViewById(R.id.text);
    }

    public void setImg(Drawable drawable) {
        img.setBackground(drawable);
    }

    public void setHint(String hint) {
        textInputLayout.setHint(hint);
    }

    public void setInputType(int inputType) {
        text.setInputType(inputType);
    }

    public CharSequence getText() {
        return text.getText();
    }
}
