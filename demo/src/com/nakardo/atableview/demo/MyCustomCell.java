package com.nakardo.atableview.demo;

import android.content.Context;
import android.util.AttributeSet;

import com.nakardo.atableview.uikit.UILabel;
import com.nakardo.atableview.view.ATableViewCell;

public class MyCustomCell extends ATableViewCell {
	private UILabel mCustomLabel;

	protected int getLayout(ATableViewCellStyle style) {
		return R.layout.my_custom_cell;
	}
	
	public MyCustomCell(ATableViewCellStyle style, String reuseIdentifier, Context context) {
		super(style, reuseIdentifier, context);
		mCustomLabel = (UILabel)findViewById(R.id.custom_cell_label);
	}
	
	public UILabel getCustomLabel() {
		return mCustomLabel;
	}
	
	public MyCustomCell(Context context) {
		super(context);
	}
	
	public MyCustomCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyCustomCell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
