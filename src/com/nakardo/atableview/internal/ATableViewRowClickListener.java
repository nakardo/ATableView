package com.nakardo.atableview.internal;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;

public class ATableViewRowClickListener implements OnItemClickListener {
	private ATableView mTableView;
	
	public ATableViewRowClickListener(ATableView tableView) {
		mTableView = tableView;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		ATableViewDelegate delegate = mTableView.getDelegate();
		ATableViewAdapter tableViewAdapter = (ATableViewAdapter)adapter.getAdapter();
		delegate.didSelectRowAtIndexPath(mTableView, tableViewAdapter.getIndexPath(pos));
	}
}
