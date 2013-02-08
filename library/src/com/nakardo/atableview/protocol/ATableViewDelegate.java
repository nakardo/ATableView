package com.nakardo.atableview.protocol;

import android.content.res.Resources;

import com.nakardo.atableview.R;
import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;

public class ATableViewDelegate {
	public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
		Resources res = tableView.getResources();
		return (int) res.getDimension(R.dimen.atv_cell_default_row_height);
	}
	
	public int heightForHeaderInSection(ATableView tableView, int section) {
		Resources res = tableView.getResources();
		return (int) res.getDimension(R.dimen.atv_grouped_header_height);
	}
	
	public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
		return;
	}
	
	public void accessoryButtonTappedForRowWithIndexPath(ATableView tableView, NSIndexPath indexPath) {
		return;
	}
}
