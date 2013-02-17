package com.nakardo.atableview.protocol;

import android.content.res.Resources;
import android.widget.ListView;

import com.nakardo.atableview.R;
import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;

public class ATableViewDelegate {
	public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
		Resources res = tableView.getResources();
		return (int) res.getDimension(R.dimen.atv_cell_default_row_height);
	}
	
	public int heightForHeaderInSection(ATableView tableView, int section) {
		if (tableView.getStyle() == ATableViewStyle.Plain) {
			Resources res = tableView.getResources();
			return (int) res.getDimension(R.dimen.atv_plain_section_header_height);
		}
		
		return ListView.LayoutParams.WRAP_CONTENT;
	}
	
	public int heightForFooterInSection(ATableView tableView, int section) {
		if (tableView.getStyle() == ATableViewStyle.Plain) {
			Resources res = tableView.getResources();
			return (int) res.getDimension(R.dimen.atv_plain_section_header_height);
		}
		
		return ListView.LayoutParams.WRAP_CONTENT;
	}
	
	public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
		return;
	}
	
	public void accessoryButtonTappedForRowWithIndexPath(ATableView tableView, NSIndexPath indexPath) {
		return;
	}
}
