package com.mohsen.popularmovies.common;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;

import com.mohsen.popularmovies.R;

/**
 * It is originally taken from https://stackoverflow.com/a/18667717/6072457
 *
 * Some small changes has been added.
 */


public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView {
    private static final int DEFAULT_TRIM_LENGTH = 200;
    private static final String ELLIPSIS = ".....";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private final int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText();
        bufferType = type;
        setText();
    }

    public void setTrim(boolean shouldTrim) { trim = shouldTrim; }
    public boolean getTrim() {return trim;}

    private CharSequence getTrimmedText() {
        if (originalText != null && originalText.length() > trimLength) {
            SpannableStringBuilder builder = new SpannableStringBuilder(originalText, 0, trimLength + 1)
            .append("   ")
            .append(ELLIPSIS);
            builder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), builder.length() - 5, builder.length(), 0);
            return builder;
        } else {
            return originalText;
        }
    }
}