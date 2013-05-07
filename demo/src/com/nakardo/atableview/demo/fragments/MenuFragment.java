package com.nakardo.atableview.demo.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView.ATableViewCellAccessoryType;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

public class MenuFragment extends BaseListFragment {
	
	// section.
	private static final String[] mSectionsTitle = { "Demos", "Style", "Selection Style" };
	private static enum MenuFragmentSection { DEMOS, STYLES, SELECTION };
	
	// selections.
	private static enum ATableViewDemo { SIMPLE, COMPLEX };
	private static enum ATableViewStyleValue { GROUPED_ETCHED, GROUPED, PLAIN };
	private static enum ATableViewSelectionValue { NONE, SINGLE, MULTIPLE };

	private List<List<String>> mRowsText;
	
	public interface TableViewDemoInterface {
		public <T extends Fragment> void onTableViewDemoSelected(Class<T> fragmentClass);
	}
	
	private static List<List<String>> createRowsTextList() {
		List<List<String>> rows = new ArrayList<List<String>>();
		
		rows.add(Arrays.asList(new String[] { "Simple", "Complex" }));
		rows.add(Arrays.asList(new String[] { "Grouped (Etched)", "Grouped", "Plain" }));
		rows.add(Arrays.asList(new String[] { "None", "Single", "Multiple" }));
		
		return rows;
	}
	
	@Override
	protected void createTableView() {
		mTableView = new ATableView(ATableViewStyle.Plain, getActivity());
        mTableView.setDataSource(new MenuTableViewDataSource());
        mTableView.setDelegate(new MenuTableViewDelegate());
        
        ViewGroup container = (ViewGroup) getView();
        container.addView(mTableView);
	}
	
	@Override
	public ATableViewDataSource getDataSource() {
		return new MenuTableViewDataSource();
	}
	
	@Override
	public ATableViewDelegate getDelegate() {
		return new MenuTableViewDelegate();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mRowsText = createRowsTextList();
		super.onActivityCreated(savedInstanceState);
	}
	
	private class MenuTableViewDataSource extends ATableViewDataSource {

		private boolean isSelected(NSIndexPath indexPath) {
			boolean isSelected = false;
			
			int row = indexPath.getRow();
			MenuFragmentSection section = MenuFragmentSection.values()[indexPath.getSection()];
			switch (section) {
				case STYLES:
					ATableViewStyle tableViewStyle = mFragmentContainer.mTableViewStyle;
					ATableViewCellSeparatorStyle separatorStyle = mFragmentContainer.mSeparatorStyle;
					
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
					boolean allowsSelection = mFragmentContainer.mAllowsSelection;
					boolean allowsMultipleSelection = mFragmentContainer.mAllowsMultipleSelection;
					
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
			ATableViewCell cell = null;
			String cellIdentifier = null;
			
			MenuFragmentSection section = MenuFragmentSection.values()[indexPath.getSection()];
			if (section == MenuFragmentSection.DEMOS) {
				cellIdentifier = "SELECTION_CELL";
				
				cell = dequeueReusableCellWithIdentifier(cellIdentifier);
				if (cell == null) {
					cell = new ATableViewCell(ATableViewCellStyle.Default, cellIdentifier, getActivity());
					cell.setAccessoryType(ATableViewCellAccessoryType.DisclosureIndicator);
				}
			} else {
				cellIdentifier = "CHECKED_CELL";
				
				cell = dequeueReusableCellWithIdentifier(cellIdentifier);
				if (cell == null) {
					cell = new ATableViewCell(ATableViewCellStyle.Default, cellIdentifier, getActivity());
				}
				
				// toogle selection.
				ATableViewCellAccessoryType accessoryType = ATableViewCellAccessoryType.None;
				if (isSelected(indexPath)) {
					accessoryType = ATableViewCellAccessoryType.Checkmark;
				}
				cell.setAccessoryType(accessoryType);
			}
			
			// set text.
			String text = mRowsText.get(indexPath.getSection()).get(indexPath.getRow());
			cell.getTextLabel().setText(text);
			
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
			
			MenuFragmentSection section = MenuFragmentSection.values()[indexPath.getSection()];
			switch (section) {
				case DEMOS:
					Class<? extends Fragment> fragmentClass = null;
					
					ATableViewDemo demo = ATableViewDemo.values()[row];
					switch (demo) {
						case SIMPLE: fragmentClass = SimpleListFragment.class; break;
						default: fragmentClass = MultipleStylesListFragment.class; break;
					}
					
					mFragmentContainer.onTableViewDemoSelected(fragmentClass);
					break;
				case STYLES:
					ATableViewStyle tableViewStyle = ATableViewStyle.Grouped;
					ATableViewCellSeparatorStyle separatorStyle = ATableViewCellSeparatorStyle.SingleLine;
					
					ATableViewStyleValue style = ATableViewStyleValue.values()[row];
					switch (style) {
						case GROUPED_ETCHED: separatorStyle = ATableViewCellSeparatorStyle.SingleLineEtched; break;
						case GROUPED: break;
						default: tableViewStyle = ATableViewStyle.Plain; break;
					}
					
					mFragmentContainer.mTableViewStyle = tableViewStyle;
					mFragmentContainer.mSeparatorStyle = separatorStyle;
					
					mTableView.reloadData(); mFragmentContainer.onTableViewConfigurationChanged();
					break;
				case SELECTION:
					boolean allowsSelection = true, allowsMultipleSelection = false;
					
					ATableViewSelectionValue selection = ATableViewSelectionValue.values()[row];
					switch (selection) {
						case NONE: allowsSelection = false; break;
						case MULTIPLE: allowsMultipleSelection = true; break;
						default: break;
					}
					
					mFragmentContainer.mAllowsSelection = allowsSelection;
					mFragmentContainer.mAllowsMultipleSelection = allowsMultipleSelection;
					
					mTableView.reloadData(); mFragmentContainer.onTableViewConfigurationChanged();
					break;
				default: break;
			}
		}
	}
}
