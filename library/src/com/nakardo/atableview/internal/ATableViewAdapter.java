package com.nakardo.atableview.internal;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nakardo.atableview.R;
import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView.ATableViewCellAccessoryType;
import com.nakardo.atableview.internal.ATableViewCellDrawable.ATableViewCellBackgroundStyle;
import com.nakardo.atableview.internal.ATableViewHeaderFooterCell.ATableViewHeaderFooterCellType;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDataSourceExt;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;

public class ATableViewAdapter extends BaseAdapter {
	private static final int ADDITIONAL_ROWS_PER_SECTION = 2;
	
	private List<Integer> mHeadersHeight;
	private List<Integer> mFootersHeight;
	private List<Integer> mRows;
	private List<List<Integer>> mRowsHeight;
	
	private ATableView mTableView;

	private void initialize() {
		mHeadersHeight = new ArrayList<Integer>();
		mFootersHeight = new ArrayList<Integer>();
		mRows = new ArrayList<Integer>();
		mRowsHeight = new ArrayList<List<Integer>>();
		
		int sections = 0;
		
		ATableViewDataSource dataSource = mTableView.getDataSource();
		ATableViewDelegate delegate = mTableView.getDelegate();
		
		// datasource & delegate setup.
		if (dataSource != null) {
			sections = dataSource.numberOfSectionsInTableView(mTableView);
			for (int s = 0; s < sections; s++) {
				mHeadersHeight.add(delegate.heightForHeaderInSection(mTableView, s));
				mFootersHeight.add(delegate.heightForFooterInSection(mTableView, s));
				mRows.add(dataSource.numberOfRowsInSection(mTableView, s));
				
				List<Integer> sectionRowHeights = new ArrayList<Integer>();
				
				int rows = mRows.get(s);
				for (int r = 0; r < rows; r++) {
					NSIndexPath indexPath = NSIndexPath.indexPathForRowInSection(r, s);
					sectionRowHeights.add(delegate.heightForRowAtIndexPath(mTableView, indexPath));
				} 
				mRowsHeight.add(sectionRowHeights);
			}
		}
	}
	
	public ATableViewAdapter(ATableView tableView) {
		mTableView = tableView;
		initialize();
	}
	
	@Override
	public void notifyDataSetChanged() {
		initialize();
		super.notifyDataSetChanged();
	}
	
	public NSIndexPath getIndexPath(int position) {
		int offset = 1, count = 0, limit = 0;
		
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			int rows = mRows.get(s);
			
			limit += rows + ADDITIONAL_ROWS_PER_SECTION;
			if (position < limit) {
				// offset is given by current pos, accumulated headers, footers and rows.
				int positionWithOffset = position - offset - count;
				return NSIndexPath.indexPathForRowInSection(positionWithOffset, s);
			}
			offset += ADDITIONAL_ROWS_PER_SECTION;
			count += rows;
		}
		
		return null;
	}
	
	public boolean isHeaderRow(int position) {
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			if (position == 0) {
				return true;
			}
			position -= mRows.get(s) + ADDITIONAL_ROWS_PER_SECTION;
		}
		
		return false;
	}
	
	public boolean isFooterRow(int position) {
		int positionWithOffset = 0;
		
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			positionWithOffset += mRows.get(s) + ADDITIONAL_ROWS_PER_SECTION;
			if (position - positionWithOffset == -1) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isSingleRow(NSIndexPath indexPath) {
		return indexPath.getRow() == 0 && mRows.get(indexPath.getSection()) == 1;
	}
	
	private boolean isTopRow(NSIndexPath indexPath) {
		return indexPath.getRow() == 0 && mRows.get(indexPath.getSection()) > 1;
	}
	
	private boolean isBottomRow(NSIndexPath indexPath) {
		return indexPath.getRow() == mRows.get(indexPath.getSection()) - 1;
	}
	
	private int getRowHeight(NSIndexPath indexPath) {
		Resources res = mTableView.getContext().getResources();
		
		// bottom and single rows have double line, so we've to add extra line to row
		// height to keep same aspect.
		int rowHeight = mRowsHeight.get(indexPath.getSection()).get(indexPath.getRow());
		if (isSingleRow(indexPath) || isBottomRow(indexPath)) {
			rowHeight += (int) ATableViewCellDrawable.CELL_STROKE_WIDTH_DP;
		}
		
		return (int) (rowHeight * res.getDisplayMetrics().density);
	}
	
	private int getHeaderFooterRowHeight(NSIndexPath indexPath, boolean isFooterRow) {
		ATableViewDelegate delegate = mTableView.getDelegate();
		int section = indexPath.getSection();
		
		// pull height, it will default on delegate to WRAP_CONTENT if not overridden.
		int rowHeight = ListView.LayoutParams.WRAP_CONTENT;
		if (isFooterRow) {
			rowHeight = delegate.heightForFooterInSection(mTableView, section);
		} else {
			rowHeight = delegate.heightForHeaderInSection(mTableView, section);
		}
		
		// calculate dips when overridden.
		if (rowHeight != ListView.LayoutParams.WRAP_CONTENT) {
			Resources res = mTableView.getResources();
			rowHeight = (int) (rowHeight * res.getDisplayMetrics().density);
		}
		
		return rowHeight;
	}
	
	private ATableViewHeaderFooterCell getReusableHeaderFooterCell(View convertView, boolean isFooterRow) {
		ATableViewHeaderFooterCell cell = (ATableViewHeaderFooterCell) convertView;
		if (cell == null) {
			ATableViewHeaderFooterCellType type = ATableViewHeaderFooterCellType.Header;
			if (isFooterRow) {
				type = ATableViewHeaderFooterCellType.Footer;
			}
			cell = new ATableViewHeaderFooterCell(type, mTableView);
		}
		
		return cell;
	}
	
	private void setupHeaderFooterRowLayout(ATableViewHeaderFooterCell cell, NSIndexPath indexPath, boolean isFooterRow) {
		ATableViewDataSource dataSource = mTableView.getDataSource();
		int section = indexPath.getSection();
		
		TextView textLabel = cell.getTextLabel();
		
		// get text.
		String headerText = null;
		if (isFooterRow) {
			headerText = dataSource.titleForFooterInSection(mTableView, section);
		} else {
			headerText = dataSource.titleForHeaderInSection(mTableView, section);
		}
		textLabel.setText(headerText);
		
		boolean hasText = headerText != null && headerText.length() > 0;
		Resources res = mTableView.getResources();
		
		// if we're on the very first header of the table, we've to add an extra margin top to textView.
		if (hasText && !isFooterRow && section == 0) {
			int marginTop = (int) res.getDimension(R.dimen.atv_grouped_section_header_first_row_text_margin_top);
			
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textLabel.getLayoutParams();
			params.topMargin = marginTop;
			textLabel.setLayoutParams(params);
		}
		
		// if we're on the last footer of the table, extra margin applies here as well.
		if (hasText && isFooterRow && section == mRows.size() - 1) {
			int marginBottom = (int) res.getDimension(R.dimen.atv_grouped_section_footer_last_row_text_margin_bottom);
			
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textLabel.getLayoutParams();
			params.bottomMargin = marginBottom;
			textLabel.setLayoutParams(params);
		}
		
		// hide header or footer text if it's null.
		int visibility = hasText ? View.VISIBLE : View.GONE;
		textLabel.setVisibility(visibility);
		
		// setup layout height.
		int rowHeight = getHeaderFooterRowHeight(indexPath, isFooterRow);
		ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, rowHeight);
		cell.setLayoutParams(params);
	}
	
	private void setupRowLayout(ATableViewCell cell, NSIndexPath indexPath) {
		Resources res = mTableView.getContext().getResources();
		int rowHeight = getRowHeight(indexPath);
		
		// add margins for grouped style.
		if (mTableView.getStyle() == ATableViewStyle.Grouped) {
			int margin = (int) res.getDimension(R.dimen.atv_cell_grouped_margins);
			cell.setPadding(margin, 0, margin, 0);
		}
		
		ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, rowHeight);
		cell.setLayoutParams(params);
	}
	
	private void setupRowBackgroundDrawable(ATableViewCell cell, NSIndexPath indexPath) {
		
		// get row style for using specific drawable.
		ATableViewCellBackgroundStyle backgroundStyle = ATableViewCellBackgroundStyle.Middle;
		if (isSingleRow(indexPath)) {
			backgroundStyle = ATableViewCellBackgroundStyle.Single;
		} else if (isTopRow(indexPath)) {
			backgroundStyle = ATableViewCellBackgroundStyle.Top;
		} else if (isBottomRow(indexPath)) {
			backgroundStyle = ATableViewCellBackgroundStyle.Bottom;
		}
		
		// setup background drawables.
		ShapeDrawable normal = new ATableViewCellDrawable(mTableView, backgroundStyle, cell.getBackgroundColor());
		StateListDrawable drawable = new StateListDrawable();
		
		Resources res = mTableView.getContext().getResources();
		int rowHeight = getRowHeight(indexPath);
		
		ATableViewCellSelectionStyle selectionStyle = cell.getSelectionStyle();
		if (selectionStyle != ATableViewCellSelectionStyle.None) {
			int startColor = res.getColor(R.color.atv_cell_selection_style_blue_start);
			int endColor = res.getColor(R.color.atv_cell_selection_style_blue_end);
			
			ShapeDrawable pressed = new ATableViewCellDrawable(mTableView, backgroundStyle, rowHeight, startColor, endColor);
			if (selectionStyle == ATableViewCellSelectionStyle.Gray) {
				startColor = res.getColor(R.color.atv_cell_selection_style_gray_start);
				endColor = res.getColor(R.color.atv_cell_selection_style_gray_end);
				
				pressed = new ATableViewCellDrawable(mTableView, backgroundStyle, rowHeight, startColor, endColor);
			}
			
			drawable.addState(new int[] { android.R.attr.state_pressed }, pressed);
			drawable.addState(new int[] { android.R.attr.state_focused }, pressed);
		}
		drawable.addState(new int[] {}, normal);
		
		LinearLayout contentView = (LinearLayout)cell.findViewById(R.id.contentView);
		contentView.setBackgroundDrawable(drawable);
	}
	
	private void setupRowAccessoryButtonDelegateCallback(ATableViewCell cell, final NSIndexPath indexPath) {
		ATableViewCellAccessoryType accessoryType = cell.getAccessoryType();
		if (accessoryType == ATableViewCellAccessoryType.DisclosureButton) {
			View accessoryView = cell.findViewById(R.id.accessoryView);
			accessoryView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					ATableViewDelegate delegate = mTableView.getDelegate();
					delegate.accessoryButtonTappedForRowWithIndexPath(mTableView, indexPath);
				}
			});
		}
	}
	
	@Override
	public int getCount() {
		int count = 0;
		for (int i = 0; i < mRows.size(); i++) {
			count += mRows.get(i) + ADDITIONAL_ROWS_PER_SECTION;
		}
		
		return count;
	}

	@Override
	public int getViewTypeCount() {
		ATableViewDataSource dataSource = mTableView.getDataSource();
	    if (dataSource instanceof ATableViewDataSourceExt) {
	    	// TODO: additional style for header and footers. Also custom header should be handled here when supported.
			return ((ATableViewDataSourceExt) dataSource).numberOfRowStyles() + ADDITIONAL_ROWS_PER_SECTION;
		}
	    
	    return 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (isHeaderRow(position)) {
			return getViewTypeCount() - 2; // additional style for headers.
		} else if (isFooterRow(position)) {
			return getViewTypeCount() - 1; // additional style for footers.
		} else {
			ATableViewDataSource dataSource = mTableView.getDataSource();
			if (dataSource instanceof ATableViewDataSourceExt) {
				NSIndexPath indexPath = getIndexPath(position);
				return ((ATableViewDataSourceExt) dataSource).styleForRowAtIndexPath(indexPath);			
			}
		}
		
		return IGNORE_ITEM_VIEW_TYPE;
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean isHeaderRow = isHeaderRow(position);
		boolean isFooterRow = isFooterRow(position);
		
		NSIndexPath indexPath = getIndexPath(position);
		if (isHeaderRow || isFooterRow) {
			ATableViewHeaderFooterCell cell = getReusableHeaderFooterCell(convertView, isFooterRow);
			
			// setup.
			setupHeaderFooterRowLayout(cell, indexPath, isFooterRow);
			
			convertView = cell;
		} else {
			ATableViewCell cell = (ATableViewCell)convertView;
			
			ATableViewDataSource dataSource = mTableView.getDataSource();
			dataSource.setReusableCell(cell);
			
			cell = dataSource.cellForRowAtIndexPath(mTableView, indexPath);
			
			// setup.
			setupRowLayout(cell, indexPath);
			setupRowBackgroundDrawable(cell, indexPath);
			setupRowAccessoryButtonDelegateCallback(cell, indexPath);
			
			convertView = cell;
		}
		
		return convertView;
	}
}