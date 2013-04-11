package com.nakardo.atableview.internal;

import android.content.Context;
import android.view.LayoutInflater;
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
		
		return R.layout.atv_plain_header_footer;
	}
	
	public ATableViewHeaderFooterCell(ATableViewHeaderFooterCellType type, ATableView tableView) {
		super(tableView.getContext());
		LayoutInflater.from(getContext()).inflate(getLayout(type, tableView), this, true);
		
		mTextLabel = (UILabel) findViewById(R.id.textLabel);
	}
	
	public ATableViewHeaderFooterCell(Context context) {
		super(context);
	}
	
	public UILabel getTextLabel() {
		return mTextLabel;
	}
}
