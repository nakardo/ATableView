package com.nakardo.atableview.internal;

import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;

public class ATableViewCellContainerView extends LinearLayout {

	private static ATableViewCell getContainerCell(View view) {
		ViewParent parent = view.getParent();
		while (parent != null) {
			if (parent instanceof ATableViewCell) return (ATableViewCell) parent;
			parent = parent.getParent();
		}

		return null;
	}

	public ATableViewCellContainerView(Context context) {
		super(context);
	}

	public ATableViewCellContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setPressed(boolean pressed) {
		if (pressed) {
			ATableViewCell cell = getContainerCell(this);
			if (cell != null && cell.getSelectionStyle() == ATableViewCellSelectionStyle.None) {
				return;
			}
		}

		super.setPressed(pressed);
	}
}
