package com.nakardo.atableview.demo.fragments;

import android.view.Gravity;
import android.widget.Toast;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

public class SimpleListFragment extends BaseListFragment {
	private static final int ADD_MORE_ROWS_INCREMENT = 100;
	private enum SimpleListSection { ROWS, SHOW_SELECTED_ROWS, ADD_MORE };
	
	private int mRowCount = ADD_MORE_ROWS_INCREMENT;
	
	private SimpleListSection getListSection(int section) {
		return SimpleListSection.values()[section];
	}
	
	@Override
	public ATableViewDataSource getDataSource() {
		return new SimpleListDataSource();
	}
	
	@Override
	public ATableViewDelegate getDelegate() {
		return new SimpleListDelegate();
	}
	
	private class SimpleListDataSource extends ATableViewDataSource {
		
		@Override
		public ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			final String cellIdentifier = "SIMPLE_CELL";
			
			ATableViewCell cell = dequeueReusableCellWithIdentifier(cellIdentifier);
			if (cell == null) {
				cell = new ATableViewCell(ATableViewCellStyle.Default, cellIdentifier, getActivity());
			}
			
			String text = "Add 100 More Rows";
			
			SimpleListSection theSection = getListSection(indexPath.getSection());
			if (theSection == SimpleListSection.ROWS) {
				text = "Row " + String.valueOf(indexPath.getRow());
			} else if (theSection == SimpleListSection.SHOW_SELECTED_ROWS) {
				text = "Show Selected Rows";
			}
			cell.getTextLabel().setText(text);
			
			return cell;
		}

		@Override
		public int numberOfSectionsInTableView(ATableView tableView) {
			return SimpleListSection.values().length;
		}
		
		@Override
		public int numberOfRowsInSection(ATableView tableView, int section) {
			int count = mRowCount;
			
			SimpleListSection theSection = getListSection(section);
			if (theSection == SimpleListSection.SHOW_SELECTED_ROWS ||
				theSection == SimpleListSection.ADD_MORE) {
				count = 1;
			}
			
			return count;
		}
	}
	
	private class SimpleListDelegate extends ATableViewDelegate {
		
		public String getIndexPathsText(NSIndexPath[] indexPaths) {
			String text = "";
			for (NSIndexPath indexPath : indexPaths) {
				text += indexPath.toString() + ", ";
			}
			
			return text.substring(0, text.length() - 2);
		}
		
		@Override
		public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			
			SimpleListSection theSection = getListSection(indexPath.getSection());
			if (theSection == SimpleListSection.SHOW_SELECTED_ROWS) {
				String text = "Selected indexPaths " + getIndexPathsText(mTableView.getIndexPathsForSelectedRows());
				Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
			} else if (theSection == SimpleListSection.ADD_MORE) {
				mRowCount += ADD_MORE_ROWS_INCREMENT; mTableView.reloadData();
			}
		}
		
		@Override
		public void willDisplayCellForRowAtIndexPath(ATableView tableView, ATableViewCell cell, NSIndexPath indexPath) {
			int gravity = Gravity.LEFT;
			
			SimpleListSection theSection = getListSection(indexPath.getSection());
			if (theSection == SimpleListSection.SHOW_SELECTED_ROWS ||
				theSection == SimpleListSection.ADD_MORE) {
				gravity = Gravity.CENTER_HORIZONTAL;
			}
			
			cell.getTextLabel().setGravity(gravity);
		}
	}
}
