package com.nakardo.atableview.utils;

import com.nakardo.atableview.R;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;

import android.content.res.Resources;

public class DrawableUtils {
	
	public static int getStrokeWidth(Resources res) {
		return (int) Math.floor(res.getDimension(R.dimen.atv_stroke_width));
	}
	
	public static int getSeparatorColor(ATableView tableView) {
		Resources res = tableView.getResources();
		
		// pull color, -1 implies no custom color has being defined so we go with defaults.
		int color = tableView.getSeparatorColor();
		if (color == -1) {
			color = res.getColor(R.color.atv_plain_separator);
			if (tableView.getStyle() == ATableViewStyle.Grouped) {
				color = res.getColor(R.color.atv_grouped_separator);
			}
		}
		
		return color;
	}
}
