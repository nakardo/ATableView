package com.nakardo.atableview.uikit;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class UILabel extends TextView {
	
    public UILabel(Context context) {
        super(context);
    }
    
    public UILabel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public UILabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
    @Override
    public void setTypeface (Typeface tf, int style) {
    	if (!isInEditMode()) {
    		if (style == Typeface.BOLD) {
    			super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_bold.ttf"));
    		} else {
    			super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_regular.ttf"));
    		}
		}
    }
    */
}
