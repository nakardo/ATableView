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
	private List<Boolean> mHasHeader;
	private List<Boolean> mHasFooter;
	private List<Integer> mHeadersHeight;
	private List<Integer> mFootersHeight;
	private List<Integer> mRows;
	private List<List<Integer>> mRowsHeight;
	
	private ATableView mTableView;

	private void initialize() {
		mHasHeader = new ArrayList<Boolean>();
		mHasFooter = new ArrayList<Boolean>();
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
				Boolean hasHeader = false, hasFooter = false;
				
				// mark header if overridden in delegate, otherwise try to pull title instead.
				int headerHeight = delegate.heightForHeaderInSection(mTableView, s);
				if (headerHeight != ATableViewCell.LayoutParams.UNDEFINED ||
					dataSource.titleForHeaderInSection(mTableView, s) != null) {
					hasHeader = true;
				}
				mHeadersHeight.add(headerHeight);
				mHasHeader.add(hasHeader);
				
				// mark footer if overridden in delegate, otherwise try to pull title instead.
				int footerHeight = delegate.heightForFooterInSection(mTableView, s);
				if (footerHeight != ATableViewCell.LayoutParams.UNDEFINED ||
					dataSource.titleForFooterInSection(mTableView, s) != null) {
					hasFooter = true;
				}
				mFootersHeight.add(footerHeight);
				mHasFooter.add(hasFooter);
				
				// pull row count from datasource.
				mRows.add(dataSource.numberOfRowsInSection(mTableView, s));
				
				List<Integer> sectionRowHeights = new ArrayList<Integer>();
				
				// pull row heights.
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
			
			limit += rows + getHeaderFooterCountOffset(s);
			if (position < limit) {
				// offset is given by current pos, accumulated headers, footers and rows.
				int positionWithOffset = position - offset - count;
				return NSIndexPath.indexPathForRowInSection(positionWithOffset, s);
			}
			offset += getHeaderFooterCountOffset(s);
			count += rows;
		}
		
		return null;
	}
	
	public boolean hasHeader(int section) {
		if (mTableView.getStyle() == ATableViewStyle.Grouped) {
			return true;
		}
		
		return mHasHeader.get(section);
	}
	
	public boolean hasFooter(int section) {
		if (mTableView.getStyle() == ATableViewStyle.Grouped) {
			return true;
		}
		
		return mHasFooter.get(section);
	}
	
	public boolean isHeaderRow(int position) {
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			if (hasHeader(s) && position == 0) {
				return true;
			}
			position -= mRows.get(s) + getHeaderFooterCountOffset(s);
		}
		
		return false;
	}
	
	public boolean isFooterRow(int position) {
		int positionWithOffset = 0;
		
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			positionWithOffset += mRows.get(s) + getHeaderFooterCountOffset(s);
			if (hasFooter(s) && position - positionWithOffset == -1) {
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
		
		int rowHeight = mRowsHeight.get(indexPath.getSection()).get(indexPath.getRow());
		if (mTableView.getStyle() == ATableViewStyle.Plain) {
			if (!isBottomRow(indexPath) && !isSingleRow(indexPath)) {
				rowHeight += (int) ATableViewCellDrawable.CELL_STROKE_WIDTH_DP;
			}
		} else {
			if (isBottomRow(indexPath) || isSingleRow(indexPath)) {
				rowHeight += (int) ATableViewCellDrawable.CELL_STROKE_WIDTH_DP;
			}
			rowHeight += (int) ATableViewCellDrawable.CELL_STROKE_WIDTH_DP;
		}
		
		return (int) Math.ceil(rowHeight * res.getDisplayMetrics().density);
	}
	
	private int getHeaderFooterRowHeight(NSIndexPath indexPath, boolean isFooterRow) {
		Resources res = mTableView.getResources();
		int section = indexPath.getSection();
		
		// pull height, it will default on delegate to UNDEFINED if not overridden.
		int rowHeight = ATableViewCell.LayoutParams.UNDEFINED;
		if (isFooterRow) {
			rowHeight = mFootersHeight.get(section);
		} else {
			rowHeight = mHeadersHeight.get(section);
		}
		
		// if undefined, set a valid height depending on table style, otherwise use overridden value.
		if (rowHeight == ATableViewCell.LayoutParams.UNDEFINED) {
			if (mTableView.getStyle() == ATableViewStyle.Plain) {
				rowHeight = (int) res.getDimension(R.dimen.atv_plain_section_header_height);
			} else {
				rowHeight = ListView.LayoutParams.WRAP_CONTENT;
			}
		}
		
		// convert row height value when an scalar was used.
		if (rowHeight > -1) {
			rowHeight = (int) Math.ceil(rowHeight * res.getDisplayMetrics().density);
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
		ATableViewStyle tableViewStyle = mTableView.getStyle();
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
		
		// setup layout depending on style.
		if (tableViewStyle == ATableViewStyle.Grouped) {
			Resources res = mTableView.getResources();
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textLabel.getLayoutParams();
			
			// if we're on the very first header of the table, we've to add an extra margin top to textView.
			int marginTop = (int) res.getDimension(R.dimen.atv_grouped_section_header_text_margin_top);
			if (!isFooterRow && section == 0) {
				marginTop = (int) res.getDimension(R.dimen.atv_grouped_section_header_first_row_text_margin_top);
			}
			params.topMargin = marginTop;
			
			// if we're on the last footer of the table, extra margin applies here as well.
			int marginBottom = (int) res.getDimension(R.dimen.atv_grouped_section_footer_text_margin_bottom);
			if (isFooterRow && section == mRows.size() - 1) {
				marginBottom = (int) res.getDimension(R.dimen.atv_grouped_section_footer_last_row_text_margin_bottom);			
			}
			params.bottomMargin = marginBottom;
			
			textLabel.setLayoutParams(params);
			
			// hide header or footer text if it's null.
			int visibility = headerText != null && headerText.length() > 0 ? View.VISIBLE : View.GONE;
			textLabel.setVisibility(visibility);
		} else if (tableViewStyle == ATableViewStyle.Plain) {
			
		}
		
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
	
	public int getHeaderFooterStyleCount() {
		int count = 0;
		
		// check if we've a header or a footer at least.
		int s = 0;
		while (count < 2 && s < mRows.size()) {
			int offset = getHeaderFooterCountOffset(s);
			if (offset > count) count = offset;
			s++;
		}
		
		// only grouped style tables has different header and footer style.
		if (mTableView.getStyle() == ATableViewStyle.Grouped && count > 1) {
			return 2;
		} else if (count > 0) {
			return 1;
		}
		
		return 0;
	}
	
	public int getHeaderFooterCountOffset(int section) {
		return (hasHeader(section) ? 1 : 0) + (hasFooter(section) ? 1 : 0);
	}
	
	@Override
	public int getCount() {
		int count = 0;
		
		// count is given by number of rows in section plus its header & footer.
		for (int s = 0; s < mRows.size(); s++) {
			count += mRows.get(s) + getHeaderFooterCountOffset(s);
		}
		
		return count;
	}

	@Override
	public int getViewTypeCount() {
		ATableViewDataSource dataSource = mTableView.getDataSource();
	    if (dataSource instanceof ATableViewDataSourceExt) {
	    	// TODO: additional styles for header and footers. Also custom header should be handled here when supported.
			return ((ATableViewDataSourceExt) dataSource).numberOfRowStyles() + getHeaderFooterStyleCount();
		}
	    
	    return 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (isHeaderRow(position) && mTableView.getStyle() == ATableViewStyle.Grouped) {
			return getViewTypeCount() - 2; // additional style for groped headers.
		} else if (isHeaderRow(position) || isFooterRow(position)) {
			return getViewTypeCount() - 1; // for plain tables, headers and footers share same style.
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