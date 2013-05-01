package com.nakardo.atableview.internal;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;

public class ATableViewCellClickListener implements OnItemClickListener {
	private ATableView mTableView;
	
	public ATableViewCellClickListener(ATableView tableView) {
		mTableView = tableView;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		
		// make sure we're clicking a cell, do not send callbacks for header or footer rows.
		if (view instanceof ATableViewCell) {
			ATableViewAdapter a = mTableView.getInternalAdapter();
			
			ATableViewDelegate delegate = mTableView.getDelegate();
			delegate.didSelectRowAtIndexPath(mTableView, a.getIndexPath(pos));
		}
	}
}