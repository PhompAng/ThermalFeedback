package com.example.phompang.thermalfeedback.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phompang.thermalfeedback.R;

/**
 * Created by phompang on 12/29/2016 AD.
 */

public class NotificationDetail extends FrameLayout {

    private ImageView img;
    private TextView text;
    private TextView sub;

    public NotificationDetail(Context context) {
        super(context);
        setUp(null);
    }

    public NotificationDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(attrs);
    }

    public NotificationDetail(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(attrs);
    }

    public NotificationDetail(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(attrs);
    }

    private void setUp(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.widget_notification_detail, this);
        bind();
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.NotificationDetail);
            Drawable drawable = typedArray.getDrawable(R.styleable.NotificationDetail_nd_img);
            String text = typedArray.getString(R.styleable.NotificationDetail_nd_text);
            String sub = typedArray.getString(R.styleable.NotificationDetail_nd_sub);
            typedArray.recycle();

            setImg(drawable);
            setText(text);
            setSub(sub);
        }
    }

    private void bind() {
        img = (ImageView) findViewById(R.id.img);
        text = (TextView) findViewById(R.id.text);
        sub = (TextView) findViewById(R.id.sub);
    }

    public void setImg(Drawable drawable) {
        img.setBackground(drawable);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setSub(String text) {
        this.sub.setText(text);
    }
}
