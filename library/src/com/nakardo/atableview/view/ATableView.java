package com.nakardo.atableview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.nakardo.atableview.R;
import com.nakardo.atableview.internal.ATableViewAdapter;
import com.nakardo.atableview.internal.ATableViewCellClickListener;
import com.nakardo.atableview.internal.ATableViewPlainFooterDrawable;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;

public class ATableView extends ListView {
	private static final ATableViewStyle DEFAULT_STYLE = ATableViewStyle.Plain;
	
	private ATableViewCellSeparatorStyle mSeparatorStyle = ATableViewCellSeparatorStyle.SingleLine;
	private int mSeparatorColor = -1;
	private ATableViewStyle mStyle = DEFAULT_STYLE;
	private ATableViewDataSource mDataSource;
	private ATableViewDelegate mDelegate = new ATableViewDelegate();
	
	public enum ATableViewStyle {
		Plain, Grouped
	};
	
	private void setupBackgroundDrawable() {
		
		// closes #20, tiled backgrounds defined on xml doesn't seems to work well on Android ~2.1.
		// setup gray striped background for plain style tables.
		if (mStyle == ATableViewStyle.Grouped) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.group_background);
			
			BitmapDrawable drawable = new BitmapDrawable(bitmap);
			drawable.setTileModeX(TileMode.REPEAT);
			drawable.setTileModeY(TileMode.REPEAT);
			setBackgroundDrawable(drawable);
		}
	}
	
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
	
	public ATableView(ATableViewStyle style, Context context) {
		super(context);
		mStyle = style;
		
		setSelector(android.R.color.transparent);
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
	}
	
	@Override
	protected void onAttachedToWindow() {
		ATableViewAdapter adapter = new ATableViewAdapter(this);
		
		// setup footer for plain tables to complete its height with empty rows.
		setupFooterView(adapter.getLastRowHeight());
		
		setAdapter(adapter);
		setOnItemClickListener(new ATableViewCellClickListener(this));
		super.onAttachedToWindow();
	}
}
