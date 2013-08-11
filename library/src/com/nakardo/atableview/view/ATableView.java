package com.nakardo.atableview.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewAdapter;
import com.nakardo.atableview.internal.ATableViewCellClickListener;
import com.nakardo.atableview.internal.ATableViewPlainFooterDrawable;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.utils.DrawableUtils;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;

public class ATableView extends ListView {
	private static final ATableViewStyle DEFAULT_STYLE = ATableViewStyle.Plain;
	
	// UIView
	private int mBackgroundColor = -1;
	
	private ATableViewCellSeparatorStyle mSeparatorStyle = ATableViewCellSeparatorStyle.SingleLine;
	private int mSeparatorColor = -1;
	private ATableViewStyle mStyle = DEFAULT_STYLE;
	private boolean mAllowsSelection = true;
	private boolean mAllowsMultipleSelection = false;
	private ATableViewDataSource mDataSource;
	private ATableViewDelegate mDelegate = new ATableViewDelegate();
	
	public enum ATableViewStyle {
		Plain, Grouped
	};
	
	private void setupFooterView(int lastRowHeight) {
		
		// closes #12, add footer for plain style tables in order to make the effect of repeating
		// rows across table height.
		if (mStyle == ATableViewStyle.Plain) {
			final View footerView = new FrameLayout(getContext());
			
			// add listener to resize after layout has been completed.
			getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
					
					int footerHeight = getHeight() - getInternalAdapter().getContentHeight();
					AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
							footerHeight > 0 ? footerHeight : 0);
					footerView.setLayoutParams(params);
				}
			});
			
			footerView.setBackgroundDrawable(new ATableViewPlainFooterDrawable(this, lastRowHeight));
			addFooterView(footerView);
		}
	}
	
	private void setupBackgroundDrawable() {
		setBackgroundDrawable(DrawableUtils.getTableBackgroundDrawable(this));
	}
	
	private int getSelectionMode() {
		
		// well, this is just a workaround since we've two variables in ios and only one in android
		// to define selection enabled and multiple selection.
		int choiceMode = CHOICE_MODE_SINGLE;
		if (mAllowsMultipleSelection) choiceMode = CHOICE_MODE_MULTIPLE;
		
		return choiceMode;
	}
	
	private void clearSelectedRows() {
		clearChoices(); requestLayout();
	}
	
	public ATableView(ATableViewStyle style, Context context) {
		super(context);
		mStyle = style;
		
		setSelector(android.R.color.transparent);
		setChoiceMode(getSelectionMode());
		setDivider(null);
		setDividerHeight(0);
		setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		setScrollingCacheEnabled(false);
		
		setupBackgroundDrawable();
	}
	
	public ATableView(Context context) {
		super(context);
	}
	
	public ATableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ATableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public int getBackgroundColor() {
		return mBackgroundColor;
	}
	public void setBackgroundColor(int resId) {
		mBackgroundColor = resId; setupBackgroundDrawable();
	}
	
	public ATableViewCellSeparatorStyle getSeparatorStyle() {
		return mSeparatorStyle;
	}
	public void setSeparatorStyle(ATableViewCellSeparatorStyle separatorStyle) {
		mSeparatorStyle = separatorStyle;
	}
	
	public int getSeparatorColor() {
		return mSeparatorColor;
	}
	public void setSeparatorColor(int resId) {
		mSeparatorColor = resId;
	}
	
	public ATableViewStyle getStyle() {
		return mStyle;
	}

	public boolean getAllowsSelection() {
		return mAllowsSelection;
	}
	public void setAllowsSelection(boolean allowsSelection) {
		mAllowsSelection = allowsSelection;
		
		if (mAllowsSelection) setChoiceMode(getSelectionMode());
		else setChoiceMode(CHOICE_MODE_NONE);
		
		clearSelectedRows();
	}
	
	public boolean getAllowsMultipleSelection() {
		return mAllowsMultipleSelection;
	}
	public void setAllowsMultipleSelection(boolean allowsMultipleSelection) {
		mAllowsMultipleSelection = allowsMultipleSelection;
		
		if (mAllowsSelection) {
			setChoiceMode(getSelectionMode()); clearSelectedRows();
		}
	}
	
	public NSIndexPath getIndexPathForSelectedRow() {
		NSIndexPath indexPath = null;
		
		int position = getCheckedItemPosition();
		if (position != INVALID_POSITION) {
			indexPath = getInternalAdapter().getIndexPath(position);
		}
		
		return indexPath;
	}
	
	public NSIndexPath[] getIndexPathsForSelectedRows() {
		NSIndexPath[] indexPaths = null;
		
		SparseBooleanArray checkedList = getCheckedItemPositions();
		if (checkedList != null) {
			ArrayList<NSIndexPath> indexPathList = new ArrayList<NSIndexPath>();
			
			ATableViewAdapter adapter = getInternalAdapter();
			for (int i = 0; i < adapter.getCount(); i++) {
				if (checkedList.get(i)) {
					indexPathList.add(adapter.getIndexPath(i));
				}
			}
			
			indexPaths = indexPathList.toArray(new NSIndexPath[indexPathList.size()]);
		}
		
		return indexPaths;
	}
	
	public ATableViewDataSource getDataSource() {
		return mDataSource;
	}
	public void setDataSource(ATableViewDataSource dataSource) {
		mDataSource = dataSource;
	}

	public ATableViewDelegate getDelegate() {
		return mDelegate;
	}
	public void setDelegate(ATableViewDelegate delegate) {
		mDelegate = delegate;
	}
	
	public ATableViewAdapter getInternalAdapter() {
		
		// fixes bugs for tables which includes headers or footers.
		ATableViewAdapter adapter = null;
		if (getAdapter() instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) getAdapter();
			adapter = (ATableViewAdapter) headerAdapter.getWrappedAdapter();
		} else {
			adapter = (ATableViewAdapter) getAdapter();
		}
		
		return adapter;
	}
	
	public void reloadData() {
		getInternalAdapter().notifyDataSetChanged();
		clearSelectedRows();
	}
	
	@Override
	protected void onAttachedToWindow() {
		ATableViewAdapter adapter = new ATableViewAdapter(this);
		
		// TODO we should handle the case last row is ListView.LayoutParams.WRAP_CONTENT, to get its height.
		// setup footer for plain tables to complete its height with empty rows.
		setupFooterView(adapter.getLastRowHeight());
		
		setAdapter(adapter);
		setOnItemClickListener(new ATableViewCellClickListener(this));
		super.onAttachedToWindow();
	}
}
