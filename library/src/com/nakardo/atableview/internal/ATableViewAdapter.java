package com.nakardo.atableview.internal;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nakardo.atableview.R;
import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView.ATableViewCellAccessoryType;
import com.nakardo.atableview.internal.ATableViewCellDrawable.ATableViewCellBackgroundStyle;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDataSourceExt;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;

public class ATableViewAdapter extends BaseAdapter {
	private List<Integer> mHeadersHeight;
	private List<Integer> mRows;
	private List<List<Integer>> mRowsHeight;
	
	private ATableView mTableView;

	private void initialize() {
		mHeadersHeight = new ArrayList<Integer>();
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
				mRows.add(dataSource.numberOfRowsInSection(mTableView, s));
				
				List<Integer> sectionHeights = new ArrayList<Integer>();
				
				int rows = mRows.get(s);
				for (int r = 0; r < rows; r++) {
					NSIndexPath indexPath = NSIndexPath.indexPathForRowInSection(r, s);
					sectionHeights.add(delegate.heightForRowAtIndexPath(mTableView, indexPath));
				} 
				mRowsHeight.add(sectionHeights);
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
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			int rows = mRows.get(s);
			
			// offset is given by the accumulative number of headers in the table.
			int positionWithOffset = position - (s + 1);
			if (positionWithOffset < rows) {
				return NSIndexPath.indexPathForRowInSection(positionWithOffset, s);
			}
			position -= rows;
		}
		
		return null;
	}
	
	public boolean isHeaderRow(int position) {
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			if (position == 0) {
				return true;
			}
			position -= mRows.get(s) + 1;
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
	
	private void setupLayout(ATableViewCell cell, NSIndexPath indexPath) {
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
	
	private void setupBackgroundDrawable(ATableViewCell cell, NSIndexPath indexPath) {
		
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
	
	private void setupAccessoryButtonDelegateCallback(ATableViewCell cell, final NSIndexPath indexPath) {
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
		int count = mRows.size();
		for (int i = 0; i < mRows.size(); i++) {
			count += mRows.get(i);
		}
		
		return count;
	}

	@Override
	public int getViewTypeCount() {
		ATableViewDataSource dataSource = mTableView.getDataSource();
	    if (dataSource instanceof ATableViewDataSourceExt) {
	    	// TODO: additional style for headers. Also custom header should be handled here when supported.
			return ((ATableViewDataSourceExt) dataSource).numberOfRowStyles() + 1;
		}
	    
	    return 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (isHeaderRow(position)) {
			return getViewTypeCount() - 1; // additional style for headers.
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
		ATableViewDataSource dataSource = mTableView.getDataSource();
		
		NSIndexPath indexPath = getIndexPath(position);
		if (isHeaderRow(position)) {
			LayoutInflater inflater = LayoutInflater.from(mTableView.getContext());
			convertView = inflater.inflate(R.layout.atv_grouped_header, null);
			
			String headerText = dataSource.titleForHeaderInSection(mTableView, indexPath.getSection());
			TextView textLabel = (TextView) convertView.findViewById(R.id.textLabel);
			textLabel.setText(headerText);
			return convertView;
		} else {
			ATableViewCell cell = (ATableViewCell)convertView;
			dataSource.setReusableCell(cell);
			
			cell = dataSource.cellForRowAtIndexPath(mTableView, indexPath);
			
			// setup.
			setupLayout(cell, indexPath);
			setupBackgroundDrawable(cell, indexPath);
			setupAccessoryButtonDelegateCallback(cell, indexPath);
			
			return cell;
		}
	}
}