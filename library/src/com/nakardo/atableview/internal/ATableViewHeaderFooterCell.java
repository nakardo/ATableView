package com.nakardo.atableview.internal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.nakardo.atableview.R;
import com.nakardo.atableview.uikit.UILabel;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;

public class ATableViewHeaderFooterCell extends FrameLayout {	
	public enum ATableViewHeaderFooterCellType { Header, Footer };
	
	private UILabel mTextLabel;
	
	protected static int getLayout(ATableViewHeaderFooterCellType type, ATableView tableView) {
		ATableViewStyle style = tableView.getStyle();
		if (ATableViewStyle.Grouped == style) {
			if (ATableViewHeaderFooterCellType.Header == type) {
				return R.layout.atv_grouped_header;
			}
			
			return R.layout.atv_grouped_footer;
		}
		
		return R.layout.atv_plain_header;
	}
	
	public ATableViewHeaderFooterCell(ATableViewHeaderFooterCellType type, ATableView tableView) {
		super(tableView.getContext());
		LayoutInflater inflater = LayoutInflater.from(tableView.getContext());
		
		View headerFooterView = inflater.inflate(getLayout(type, tableView), null);
		mTextLabel = (UILabel) headerFooterView.findViewById(R.id.textLabel);
		addView(headerFooterView);
	}
	
	public ATableViewHeaderFooterCell(Context context) {
		super(context);
	}
	
	public UILabel getTextLabel() {
		return mTextLabel;
	}
}
