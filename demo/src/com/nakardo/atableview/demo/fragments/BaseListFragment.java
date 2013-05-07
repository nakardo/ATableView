package com.nakardo.atableview.demo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.nakardo.atableview.demo.activities.MainActivity;
import com.nakardo.atableview.demo.interfaces.TableViewConfigurationInterface;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.slidingmenu.lib.SlidingMenu;

public abstract class BaseListFragment extends SherlockFragment implements TableViewConfigurationInterface {
	protected MainActivity mFragmentContainer;
	protected ATableView mTableView;
	
	protected void createTableView() {
		mTableView = new ATableView(mFragmentContainer.mTableViewStyle, getActivity());
		mTableView.setSeparatorStyle(mFragmentContainer.mSeparatorStyle);
		mTableView.setAllowsSelection(mFragmentContainer.mAllowsSelection);
		mTableView.setAllowsMultipleSelection(mFragmentContainer.mAllowsMultipleSelection);
        mTableView.setDataSource(getDataSource());
        mTableView.setDelegate(getDelegate());
        
        ViewGroup container = (ViewGroup) getView();
        container.addView(mTableView);
	}
	
	public abstract ATableViewDataSource getDataSource();
	
	public ATableViewDelegate getDelegate() {
		return new ATableViewDelegate();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	FrameLayout view = new FrameLayout(getActivity());
    	return view;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		createTableView();
		
		// dismiss menu if it's currently showing, it reduces animation glitch while creating fragment.
		SlidingMenu menu = mFragmentContainer.getSlidingMenu();
		if (menu.isMenuShowing()) {
			mFragmentContainer.getSlidingMenu().toggle();
		}
	}
	
	@Override
    public void onAttach(Activity activity) {
    	if (activity instanceof MainActivity) mFragmentContainer = (MainActivity) activity;
    	else throw new RuntimeException("Fragment must be attached to an instance of MainActivity");
    	
    	super.onAttach(activity);
    }
	
	@Override
	public void onTableViewConfigurationChanged() {
		((ViewGroup) getView()).removeView(mTableView);
		createTableView();
	}
}
