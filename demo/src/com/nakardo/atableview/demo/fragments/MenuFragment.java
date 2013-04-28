package com.nakardo.atableview.demo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.nakardo.atableview.demo.interfaces.OnSlidingMenuItemClickedListener;
import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView.ATableViewCellAccessoryType;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

public class MenuFragment extends SherlockFragment {
	private static final String[] mSectionsTitle = { "Styles" };
	private static final String[] mRowsText = { "Grouped (Etched)", "Grouped", "Plain" };
	
	private static enum ATableViewSelectedStyle {
		GROUPED_ETCHED, GROUPED, PLAIN
	};
	
	private OnSlidingMenuItemClickedListener mSlidingMenuClickedListener;
	private ATableView mTableView;

	private void createTableView() {
		mTableView = new ATableView(ATableViewStyle.Plain, getActivity());
        mTableView.setDataSource(new MenuTableViewDataSource());
        mTableView.setDelegate(new MenuTableViewDelegate());
        
        ViewGroup container = (ViewGroup) getView();
        container.addView(mTableView);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = new FrameLayout(getActivity());
		view.setBackgroundColor(getResources().getColor(android.R.color.white));
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mSlidingMenuClickedListener = (OnSlidingMenuItemClickedListener) getActivity();
		if (savedInstanceState == null) {
			createTableView();
		}
	}
	
	private class MenuTableViewDataSource extends ATableViewDataSource {

		@Override
		public ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			final String cellIdentifier = "DEFAULT_CELL";
			
			ATableViewCell cell = dequeueReusableCellWithIdentifier(cellIdentifier);
			if (cell == null) {
				cell = new ATableViewCell(ATableViewCellStyle.Default, cellIdentifier, getActivity());
				cell.setAccessoryType(ATableViewCellAccessoryType.DisclosureIndicator);
			}
			cell.getTextLabel().setText(mRowsText[indexPath.getRow()]);
			
			return cell;
		}

		@Override
		public String titleForHeaderInSection(ATableView tableView, int section) {
			return mSectionsTitle[section];
		}
		
		@Override
		public int numberOfSectionsInTableView(ATableView tableView) {
			return mSectionsTitle.length;
		}
		
		@Override
		public int numberOfRowsInSection(ATableView tableView, int section) {
			return mRowsText.length;
		}
	}
	
	private class MenuTableViewDelegate extends ATableViewDelegate {
		
		@Override
		public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			ATableViewStyle tableViewStyle = ATableViewStyle.Grouped;
			ATableViewCellSeparatorStyle separatorStyle = ATableViewCellSeparatorStyle.SingleLine;
			
			ATableViewSelectedStyle style = ATableViewSelectedStyle.values()[indexPath.getRow()];
			switch (style) {
				case GROUPED_ETCHED: separatorStyle = ATableViewCellSeparatorStyle.SingleLineEtched; break;
				case GROUPED: break;
				default: tableViewStyle = ATableViewStyle.Plain; break;
			}
			
			mSlidingMenuClickedListener.onStyleATableViewStyleSelected(tableViewStyle, separatorStyle);
		}
	}
}
