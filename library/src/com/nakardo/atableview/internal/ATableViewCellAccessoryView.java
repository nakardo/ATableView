package com.nakardo.atableview.internal;

import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.nakardo.atableview.R;
import com.nakardo.atableview.view.ATableViewCell;

public class ATableViewCellAccessoryView {
	public enum ATableViewCellAccessoryType { None, DisclosureIndicator, DisclosureButton, Checkmark };
	
	private static void setupAccessoryView(ATableViewCell cell, ATableViewCellAccessoryType accessoryType) {
		LinearLayout contentView = (LinearLayout)cell.findViewById(R.id.contentView);
		
		// check if accessoryView already exists for current cell before creating a new instance.
		ImageView accessoryView = (ImageView)contentView.findViewById(R.id.accessoryView);
		if (accessoryView == null) {
			Resources res = cell.getResources();
			
			// get marginRight for accessoryView, DisclosureButton has a different one.
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			int marginRight = (int)res.getDimension(R.dimen.atv_cell_content_margin);
			if (accessoryType == ATableViewCellAccessoryType.DisclosureButton) {
				marginRight = (int)res.getDimension(R.dimen.atv_cell_disclosure_button_margin_right);
			}
			params.setMargins(0, 0, marginRight, 0);
			
			// setup.
			accessoryView = new ImageView(cell.getContext());
			accessoryView.setId(R.id.accessoryView);
			accessoryView.setLayoutParams(params);
			
			contentView.addView(accessoryView); 
		}
		
		boolean isClickeable = false;
		
		// setup accessoryType always.
		int resId = android.R.color.transparent;
		switch (accessoryType) {
			case DisclosureIndicator:
				resId = R.drawable.disclosure;
				break;
			case DisclosureButton:
				resId = R.drawable.disclosure_button;
				isClickeable = true;
				break;
			case Checkmark:
				resId = R.drawable.checkmark;
				break;
			default:
				break;
		}
		
		accessoryView.setImageResource(resId);
		accessoryView.setClickable(isClickeable);
	}
	
	public static void setup(ATableViewCell cell, ATableViewCellAccessoryType accessoryType) {
		setupAccessoryView(cell, accessoryType);
	}
}
