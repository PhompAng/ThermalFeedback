package com.example.phompang.thermalfeedback.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.phompang.thermalfeedback.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by phompang on 12/29/2016 AD.
 */

public class Seeker extends FrameLayout {

    private TextView text;
    private DiscreteSeekBar seeker;
	private String defaultText;
	private OnProgressChangeListener onProgressChangeListener;

	public Seeker(Context context) {
        super(context);
        setUp(null);
    }

    public Seeker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(attrs);
    }

    public Seeker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(attrs);
    }

    public Seeker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(attrs);
    }

    private void setUp(AttributeSet attributeSet) {
        inflate(getContext(), R.layout.widget_seeker, this);
        bind();
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.Seeker);
            String text = typedArray.getString(R.styleable.Seeker_sk_text);
	        setDefaultText(typedArray.getString(R.styleable.Seeker_sk_text));
            int min = typedArray.getInt(R.styleable.Seeker_sk_min, 0);
            int max = typedArray.getInt(R.styleable.Seeker_sk_max, 0);
            typedArray.recycle();

            setText(text);
            setMin(min);
            setMax(max);
        }
    }

    private void bind() {
        text = (TextView) findViewById(R.id.text);
        seeker = (DiscreteSeekBar) findViewById(R.id.seeker);
	    seeker.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
		    @Override
		    public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
				if (onProgressChangeListener != null) {
					onProgressChangeListener.onProgressChanged(Seeker.this, i, b);
				}
		    }

		    @Override
		    public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

		    }

		    @Override
		    public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

		    }
	    });
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setMin(int min) {
        seeker.setMin(min);
    }

    public void setMax(int max) {
        seeker.setMax(max);
    }

    public void setProgressValue(int value) {
        seeker.setProgress(value);
    }

    public int getValue() {
        return seeker.getProgress();
    }

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

	public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
		this.onProgressChangeListener = onProgressChangeListener;
	}

	public interface OnProgressChangeListener {
		public void onProgressChanged(Seeker seeker, int i, boolean b);
	}
}
