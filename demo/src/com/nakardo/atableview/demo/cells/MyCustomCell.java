package com.nakardo.atableview.demo.cells;

import android.content.Context;

import com.nakardo.atableview.demo.R;
import com.nakardo.atableview.view.ATableViewCell;

public class MyCustomCell extends ATableViewCell {
	
	protected int getLayout(ATableViewCellStyle style) {
		return R.layout.my_custom_cell;
	}
	
	public MyCustomCell(ATableViewCellStyle style, String reuseIdentifier, Context context) {
		super(style, reuseIdentifier, context);
	}
	
	public MyCustomCell(Context context) {
		super(context);
	}
}
