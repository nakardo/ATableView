package com.nakardo.atableview.internal;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;

import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;

public class ATableViewCellClickListener implements OnItemClickListener {
	private ATableView mTableView;
	
	public ATableViewCellClickListener(ATableView tableView) {
		mTableView = tableView;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		ATableViewAdapter a = mTableView.getInternalAdapter();
		
		// closes #14, adding a header to the table causes an offset on rows position.
		ListAdapter rawAdapter = mTableView.getAdapter();
		if (rawAdapter instanceof HeaderViewListAdapter &&
			((HeaderViewListAdapter) rawAdapter).getHeadersCount() > 0) {
			pos--;
		}
		
		// do not send callbacks for header rows.
		if (!a.isHeaderRow(pos) && !a.isFooterRow(pos)) {
			ATableViewDelegate delegate = mTableView.getDelegate();
			delegate.didSelectRowAtIndexPath(mTableView, a.getIndexPath(pos));
		}
	}
}