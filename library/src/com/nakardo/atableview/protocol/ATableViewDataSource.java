package com.nakardo.atableview.protocol;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;

public abstract class ATableViewDataSource {
	private ATableViewCell mReusableCell;
	
	public abstract ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath);
	public abstract int numberOfRowsInSection(ATableView tableView, int section);
	
	public int numberOfSectionsInTableView(ATableView tableView) {
		return 1;
	}
	
	public String titleForHeaderInSection(ATableView tableView, int section) {
		return null;
	}
	
	public String titleForFooterInSection(ATableView tableView, int section) {
		return null;
	}
	
	public ATableViewCell dequeueReusableCellWithIdentifier(String cellIdentifier) {
		if (cellIdentifier != null && mReusableCell != null &&
			cellIdentifier.equals(mReusableCell.getReuseIdentifier())) {
			return mReusableCell;
		}
		
		return null;
	}
	
	public void setReusableCell(ATableViewCell reusableCell) {
		mReusableCell = reusableCell;
	}
}
