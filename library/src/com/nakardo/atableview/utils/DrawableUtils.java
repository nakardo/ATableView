package com.nakardo.atableview.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.nakardo.atableview.R;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;

public class DrawableUtils {
	
	public static Drawable getTableBackgroundDrawable(ATableView tableView) {
		Drawable drawable = null;
		
		Resources res = tableView.getResources();
		
		// -1 implies no color defined.
		int color = tableView.getBackgroundColor();
		if (color == -1) {
			if (tableView.getStyle() == ATableViewStyle.Grouped) {
				
				// closes #20, tiled backgrounds defined on xml doesn't seems to work well on Android ~2.1.
				// setup gray striped background for plain style tables.
				Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.group_background);
				
				BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
				bitmapDrawable.setTileModeX(TileMode.REPEAT);
				bitmapDrawable.setTileModeY(TileMode.REPEAT);
				
				drawable = bitmapDrawable;
			} else {
				drawable = new ColorDrawable(res.getColor(R.color.atv_plain_background));
			}
		} else {
			drawable = new ColorDrawable(color);
		}
		
		return drawable;
	}
	
	public static int getRowBackgroundColor(ATableView tableView, ATableViewCell cell) {
		Resources res = tableView.getResources();
		
		// -1 implies no color defined.
		int color = cell.getBackgroundColor();
		if (color == -1) {
			int tableBackgroundColor = tableView.getBackgroundColor();
			if (tableBackgroundColor == - 1) {
				color = res.getColor(R.color.atv_plain_background);
				if (tableView.getStyle() == ATableViewStyle.Grouped) {
					color = res.getColor(R.color.atv_cell_grouped_background);
				}
			} else {
				color = tableBackgroundColor;
			}
		}
		
		return color;
	}
	
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
