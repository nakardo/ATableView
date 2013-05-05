package com.nakardo.atableview.demo.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.nakardo.atableview.demo.activities.MainActivity;
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
	private static final String[] mSectionsTitle = { "Styles", "Selection" };
	private List<List<String>> mRowsText;
	
	private static enum ATableViewSection { STYLES, SELECTION };
	private static enum ATableViewStyleValue { GROUPED_ETCHED, GROUPED, PLAIN };
	private static enum ATableViewSelectionValue { NONE, SINGLE, MULTIPLE };
	
	private MainActivity mFragmentContainer;
	private ATableView mTableView;

	private static List<List<String>> createRowsTextList() {
		List<List<String>> rows = new ArrayList<List<String>>();
		
		rows.add(Arrays.asList(new String[] { "Grouped (Etched)", "Grouped", "Plain" }));
		rows.add(Arrays.asList(new String[] { "None", "Single", "Multiple" }));
		
		return rows;
	}
	
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
		
		mRowsText = createRowsTextList();
		createTableView();
	}
	
	@Override
    public void onAttach(Activity activity) {
    	if (activity instanceof MainActivity) mFragmentContainer = (MainActivity) activity;
    	else throw new RuntimeException();
    	
    	super.onAttach(activity);
    }
	
	private class MenuTableViewDataSource extends ATableViewDataSource {

		private boolean isSelected(NSIndexPath indexPath) {
			boolean isSelected = false;
			
			int row = indexPath.getRow();
			ATableViewSection section = ATableViewSection.values()[indexPath.getSection()];
			switch (section) {
				case STYLES:
					ATableViewStyle tableViewStyle = mFragmentContainer.tableViewStyle;
					ATableViewCellSeparatorStyle separatorStyle = mFragmentContainer.separatorStyle;
					
					ATableViewStyleValue style = ATableViewStyleValue.values()[row];
					switch (style) {
						case GROUPED_ETCHED:
							isSelected = (tableViewStyle == ATableViewStyle.Grouped &&
									separatorStyle == ATableViewCellSeparatorStyle.SingleLineEtched); break;
						case GROUPED:
							isSelected = (tableViewStyle == ATableViewStyle.Grouped &&
								separatorStyle == ATableViewCellSeparatorStyle.SingleLine); break;
						default:
							isSelected = (tableViewStyle == ATableViewStyle.Plain); break;
					} break;
				case SELECTION:
					boolean allowsSelection = mFragmentContainer.allowsSelection;
					boolean allowsMultipleSelection = mFragmentContainer.allowsMultipleSelection;
					
					ATableViewSelectionValue selection = ATableViewSelectionValue.values()[row];
					switch (selection) {
						case NONE: isSelected = (!allowsSelection); break;
						case SINGLE: isSelected = (allowsSelection && !allowsMultipleSelection); break;
						default: isSelected = (allowsSelection && allowsMultipleSelection); break;
					} break;
				default: break;
			}
			
			return isSelected;
		}
		
		@Override
		public ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			final String cellIdentifier = "DEFAULT_CELL";
			
			ATableViewCell cell = dequeueReusableCellWithIdentifier(cellIdentifier);
			if (cell == null) {
				cell = new ATableViewCell(ATableViewCellStyle.Default, cellIdentifier, getActivity());
			}
			
			// set text.
			String text = mRowsText.get(indexPath.getSection()).get(indexPath.getRow());
			cell.getTextLabel().setText(text);
			
			// toogle selection.
			ATableViewCellAccessoryType accessoryType = ATableViewCellAccessoryType.None;
			if (isSelected(indexPath)) {
				accessoryType = ATableViewCellAccessoryType.Checkmark;
			}
			cell.setAccessoryType(accessoryType);
			
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
			return mRowsText.get(section).size();
		}
	}
	
	private class MenuTableViewDelegate extends ATableViewDelegate {
		
		@Override
		public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			int row = indexPath.getRow();
			
			ATableViewSection section = ATableViewSection.values()[indexPath.getSection()];
			switch (section) {
				case STYLES:
					ATableViewStyle tableViewStyle = ATableViewStyle.Grouped;
					ATableViewCellSeparatorStyle separatorStyle = ATableViewCellSeparatorStyle.SingleLine;
					
					ATableViewStyleValue style = ATableViewStyleValue.values()[row];
					switch (style) {
						case GROUPED_ETCHED: separatorStyle = ATableViewCellSeparatorStyle.SingleLineEtched; break;
						case GROUPED: break;
						default: tableViewStyle = ATableViewStyle.Plain; break;
					}
					
					mFragmentContainer.tableViewStyle = tableViewStyle;
					mFragmentContainer.separatorStyle = separatorStyle;
					break;
				case SELECTION:
					boolean allowsSelection = true, allowsMultipleSelection = false;
					
					ATableViewSelectionValue selection = ATableViewSelectionValue.values()[row];
					switch (selection) {
						case NONE: allowsSelection = false; break;
						case MULTIPLE: allowsMultipleSelection = true; break;
						default: break;
					}
					
					mFragmentContainer.allowsSelection = allowsSelection;
					mFragmentContainer.allowsMultipleSelection = allowsMultipleSelection;
					break;
				default: break;
			}
			mTableView.reloadData();
			
			mFragmentContainer.onTableViewConfigurationChanged();
		}
	}
}
