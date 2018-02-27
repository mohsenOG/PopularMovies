package com.mohsen.popularmovies.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

/**
 * Created by Mohsen on 27.02.2018.
 *
 */

public class TrailerButton extends android.support.v7.widget.AppCompatButton {

    public TrailerButton(Context context) {
        super(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
        Drawable drawable = getContext().getResources().getDrawable(android.R.drawable.ic_media_play);
        this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }


}
