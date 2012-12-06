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

import com.nakardo.atableview.R;
import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewCellDrawable.ATableViewCellBackgroundStyle;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDataSourceExt;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;

public class ATableViewAdapter extends BaseAdapter {
	private List<Integer> mRows = new ArrayList<Integer>();
	private List<List<Integer>> mRowsHeight = new ArrayList<List<Integer>>();
	
	protected ATableView mTableView;

	public ATableViewAdapter(ATableView tableView) {
		mTableView = tableView;
		
		ATableViewDataSource dataSource = mTableView.getDataSource();
		ATableViewDelegate delegate = mTableView.getDelegate();
		
		int sections = dataSource.numberOfSectionsInTableView(mTableView);
		for (int s = 0; s < sections; s++) {
			mRows.add(dataSource.numberOfRowsInSection(mTableView, s));
		}
		
		for (int s = 0; s < sections; s++) {
			List<Integer> sHeights = new ArrayList<Integer>();
			
			int rows = mRows.get(s);
			for (int r = 0; r < rows; r++) {
				NSIndexPath indexPath = NSIndexPath.indexPathForRowInSection(r, s);
				sHeights.add(delegate.heightForRowAtIndexPath(mTableView, indexPath));
			} 
			mRowsHeight.add(sHeights);
		}
	}
	
	public NSIndexPath getIndexPath(int position) {
		int sections = mRows.size();
		for (int s = 0; s < sections; s++) {
			int rows = mRows.get(s);
			if (position < rows) {
				return NSIndexPath.indexPathForRowInSection(position, s);
			}
			position -= rows;
		}
		
		return null;
	}
	
	private int getRowHeight(NSIndexPath indexPath) {
		Resources res = mTableView.getContext().getResources();
		
		// last row has double line, so we've to add extra line to row height to keep same aspect.
		int rowHeight = mRowsHeight.get(indexPath.getSection()).get(indexPath.getRow());
		if (indexPath.getRow() == mRows.get(indexPath.getSection()) - 1) {
			rowHeight += (int)ATableViewCellDrawable.CELL_STROKE_WIDTH_DP;
		}
		
		return (int)(rowHeight * res.getDisplayMetrics().density);
	}
	
	private void setupLayout(ATableViewCell cell, NSIndexPath indexPath) {
		Resources res = mTableView.getContext().getResources();
		int rowHeight = getRowHeight(indexPath);
		
		// add margins for grouped style.
		if (mTableView.getStyle() == ATableViewStyle.Grouped) {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT);
			
			int margin = (int)res.getDimension(R.dimen.atv_style_grouped_margins);
			
			int row = indexPath.getRow();
			if (row == 0) {
				params.setMargins(margin, margin, margin, 0);
				rowHeight += margin;
			} else if (row == mRows.get(indexPath.getSection()) - 1) {
				params.setMargins(margin, 0, margin, margin);
				rowHeight += margin;
			} else {
				params.setMargins(margin, 0, margin, 0);
			}
			
			LinearLayout contentView = (LinearLayout)cell.findViewById(R.id.contentView);
			contentView.setLayoutParams(params);
		}
		
		ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, rowHeight);
		cell.setLayoutParams(params);
	}
	
	private void setupBackgroundDrawable(ATableViewCell cell, NSIndexPath indexPath) {
		ATableViewCellBackgroundStyle backgroundStyle = ATableViewCellBackgroundStyle.Middle;
		
		// get row style for using specific drawable.
		int row = indexPath.getRow();
		if (row == 0) {
			backgroundStyle = ATableViewCellBackgroundStyle.Top;
		} else if (row == mRows.get(indexPath.getSection()) - 1) {
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
	
	private void setupAccessoryDrawable() {
		
	}
	
	@Override
	public int getCount() {
		int rows = 0;
		for (int iRows : mRows) rows += iRows;
		
		return rows;
	}

	@Override
	public int getViewTypeCount() {
		ATableViewDataSource dataSource = mTableView.getDataSource();
	    if (dataSource instanceof ATableViewDataSourceExt) {
			return ((ATableViewDataSourceExt) dataSource).numberOfRowStyles();
		}
	    
	    return 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		ATableViewDataSource dataSource = mTableView.getDataSource();
		if (dataSource instanceof ATableViewDataSourceExt) {
			NSIndexPath indexPath = getIndexPath(position);
			return ((ATableViewDataSourceExt) dataSource).styleForRowAtIndexPath(indexPath);			
		}
		
		return 1;
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
		
		ATableViewCell cell = (ATableViewCell)convertView;
		dataSource.setReusableCell(cell);
		
		NSIndexPath indexPath = getIndexPath(position);
		cell = dataSource.cellForRowAtIndexPath(mTableView, indexPath);
		
		setupLayout(cell, indexPath);
		setupBackgroundDrawable(cell, indexPath);
		setupAccessoryDrawable();
		
		return cell;
	}
}