package com.nakardo.atableview.protocol;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;

public class ATableViewDelegate {
	private static final int DEFAULT_ROW_HEIGHT = 44;
	
	public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
		return;
	}
	
	public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
		return DEFAULT_ROW_HEIGHT;
	}
}
