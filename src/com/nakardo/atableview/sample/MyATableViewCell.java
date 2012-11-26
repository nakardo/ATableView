package com.nakardo.atableview.sample;

import android.content.Context;
import android.util.AttributeSet;

import com.nakardo.atableview.R;
import com.nakardo.atableview.uikit.UILabel;
import com.nakardo.atableview.view.ATableViewCell;

public class MyATableViewCell extends ATableViewCell {
	private UILabel mCustomLabel;
	
	@Override
	protected int getLayout(ATableViewCellStyle style) {
		return R.layout.my_cell; 
	}
	
	public MyATableViewCell(String reuseIdentifier, Context context) {
		super(ATableViewCellStyle.Default, reuseIdentifier, context);
		
		mCustomLabel = (UILabel)findViewById(R.id.customLabel);
		mCustomLabel.setText("Example Label");
	}
	
	public MyATableViewCell(Context context) {
		super(context);
	}
	
	public MyATableViewCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyATableViewCell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
