package com.nakardo.atableview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.nakardo.atableview.R;
import com.nakardo.atableview.internal.ATableViewAdapter;
import com.nakardo.atableview.internal.ATableViewRowClickListener;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;

public class ATableView extends ListView {
	private static final ATableViewStyle DEFAULT_STYLE = ATableViewStyle.Plain;
	
	private int mSeparatorColor = getResources().getColor(R.color.atv_separator);
	private ATableViewStyle mStyle = DEFAULT_STYLE;
	private ATableViewDataSource mDataSource;
	private ATableViewDelegate mDelegate = new ATableViewDelegate();
	
	public enum ATableViewStyle {
		Plain, Grouped
	};
	
	public ATableView(ATableViewStyle style, Context context) {
		super(context);
		mStyle = style;
		
		setSelector(android.R.color.transparent);
		setDivider(null);
		setDividerHeight(0);
		setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		setScrollingCacheEnabled(false);
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
	
	@Override
	protected void onAttachedToWindow() {
		setAdapter(new ATableViewAdapter(this));
		setOnItemClickListener(new ATableViewRowClickListener(this));
		super.onAttachedToWindow();
	}
}
