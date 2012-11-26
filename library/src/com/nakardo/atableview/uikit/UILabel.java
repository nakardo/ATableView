package com.nakardo.atableview.uikit;

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class UILabel extends TextView {
	private static final String BOLD_FONT_PATH = "fonts/Roboto-Bold.ttf";
	private static final String REGULAR_FONT_PATH = "fonts/Roboto-Regular.ttf";
	
    public UILabel(Context context) {
        super(context);
    }
    
    public UILabel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public UILabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean assetExists(Context context, String path) {
        boolean exists = false;
        try {
            InputStream stream = context.getAssets().open(path); stream.close();
            exists = true;
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), "Unable to load custom font at path: " + path);
        }
        
        return exists;
    }
    
    @Override
    public void setTypeface (Typeface tf, int style) {
    	if (!isInEditMode()) {
    		String customTypefacePath = REGULAR_FONT_PATH;
    		if (style == Typeface.BOLD) {
    			customTypefacePath = BOLD_FONT_PATH;
    		}
    		
    		if (assetExists(getContext(), customTypefacePath)) {
    			AssetManager assets = getContext().getAssets();
    			tf = Typeface.createFromAsset(assets, customTypefacePath);
			}
    		super.setTypeface(tf);
		}
    }
}
