package com.nakardo.atableview.demo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nakardo.atableview.demo.R;
import com.nakardo.atableview.demo.fragments.MenuFragment;
import com.nakardo.atableview.demo.fragments.MenuFragment.TableViewDemoInterface;
import com.nakardo.atableview.demo.fragments.MultipleStylesListFragment;
import com.nakardo.atableview.demo.interfaces.TableViewConfigurationInterface;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends SherlockFragmentActivity
implements TableViewConfigurationInterface, TableViewDemoInterface {
	
	private TableViewConfigurationInterface mContentListener;
	private SlidingMenu mMenu;
	
	// example default selections.
	public ATableViewStyle mTableViewStyle = ATableViewStyle.Grouped;
	public ATableViewCellSeparatorStyle mSeparatorStyle = ATableViewCellSeparatorStyle.SingleLineEtched;
	public boolean mAllowsSelection = true;
	public boolean mAllowsMultipleSelection = false;
	
	private void setupSlidingMenu() {
		mMenu = new SlidingMenu(this);
		
        mMenu.setMode(SlidingMenu.LEFT);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mMenu.setShadowWidthRes(R.dimen.menu_shadow_width);
		mMenu.setShadowDrawable(R.drawable.shadow_menu);
        mMenu.setBehindOffsetRes(R.dimen.menu_behind_offset);
        mMenu.setFadeDegree(0.35f);
        mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        
        mMenu.setMenu(R.layout.menu_frame);
        
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame,
        		new MenuFragment()).commit();
	}
	
	public SlidingMenu getSlidingMenu() {
		return mMenu;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setupSlidingMenu();
		
		onTableViewDemoSelected(MultipleStylesListFragment.class);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				mMenu.toggle(); return true;
			case R.id.goto_github:
				String url = getResources().getString(R.string.github_url);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	 @Override
	 public boolean onKeyDown(int keycode, KeyEvent event ) {
		 if (keycode == KeyEvent.KEYCODE_MENU) {
			 mMenu.toggle(); return true;
		 }
		 
		 return super.onKeyDown(keycode,event);  
	 }
	 
	 @Override
	 public void onTableViewConfigurationChanged() {
		 mContentListener.onTableViewConfigurationChanged();
	 }
	 
	 @Override
	 public <T extends Fragment> void onTableViewDemoSelected(Class<T> fragmentClass) {
		T fragment = null;
		try {
			fragment = fragmentClass.newInstance();
		} catch (InstantiationException e) {
			Log.e(this.getClass().toString(), e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e(this.getClass().toString(), e.getMessage());
		}
		fragment.setHasOptionsMenu(true);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		mContentListener = (TableViewConfigurationInterface) fragment;
	 }
}
