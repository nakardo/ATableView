package com.nakardo.atableview.internal;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.nakardo.atableview.R;
import com.nakardo.atableview.view.ATableViewCell;

// TODO this class should be generic, maybe we could change extension into a ViewGroup and place accessory within.
public class ATableViewCellAccessoryView extends ImageView {
	public enum ATableViewCellAccessoryType { None, DisclosureIndicator, DisclosureButton, Checkmark };
	
	public ATableViewCellAccessoryView(Context context) {
		super(context);
	}
	
	public static class Builder {
		private ATableViewCell mTableViewCell;
		private ATableViewCellAccessoryType mAccessoryType;
		
		private static ImageView getAccessoryView(ATableViewCell cell, ATableViewCellAccessoryType accessoryType) {
			LinearLayout containerView = (LinearLayout) cell.findViewById(R.id.containerView);
			
			// check if accessoryView already exists for current cell before creating a new instance.
			ImageView accessoryView = (ImageView) containerView.findViewById(R.id.accessoryView);
			if (accessoryView == null) {
				Resources res = cell.getResources();
				
				// get marginRight for accessoryView, DisclosureButton has a different one.
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				int marginRight = (int) res.getDimension(R.dimen.atv_cell_content_margin);
				if (accessoryType == ATableViewCellAccessoryType.DisclosureButton) {
					marginRight = (int) res.getDimension(R.dimen.atv_cell_disclosure_button_margin_right);
				}
				params.setMargins(0, 0, marginRight, 0);
				
				// setup.
				accessoryView = new ATableViewCellAccessoryView(cell.getContext());
				accessoryView.setId(R.id.accessoryView);
				accessoryView.setLayoutParams(params);
				
				containerView.addView(accessoryView); 
			}
			
			return accessoryView;
		}
		
		private static void createAccessoryView(ATableViewCell cell, ATableViewCellAccessoryType accessoryType) {
			boolean isClickeable = false;
			int visibility = View.VISIBLE;
			
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
					visibility = View.GONE;
					break;
			}
			
			// get accessoryView for given accessoryType.
			ImageView accessoryView = getAccessoryView(cell, accessoryType);
			accessoryView.setImageResource(resId);
			accessoryView.setClickable(isClickeable);
			accessoryView.setVisibility(visibility);
		}
		
		public Builder(ATableViewCell cell) {
			mTableViewCell = cell;
		}
		
		public Builder setAccessoryType(ATableViewCellAccessoryType accessoryType) {
			mAccessoryType = accessoryType;
			return this;
		}
		
		public void create() {
			createAccessoryView(mTableViewCell, mAccessoryType);
		}
	}
}
