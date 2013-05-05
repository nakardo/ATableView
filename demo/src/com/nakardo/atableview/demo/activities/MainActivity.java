package com.nakardo.atableview.demo.activities;

import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nakardo.atableview.demo.R;
import com.nakardo.atableview.demo.fragments.MainFragment;
import com.nakardo.atableview.demo.fragments.MenuFragment;
import com.nakardo.atableview.demo.interfaces.TableViewConfigurationInterface;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends SherlockFragmentActivity implements TableViewConfigurationInterface {
	private TableViewConfigurationInterface mContentListener;
	private SlidingMenu mMenu;
	
	// example default selections.
	public ATableViewStyle tableViewStyle = ATableViewStyle.Grouped;
	public ATableViewCellSeparatorStyle separatorStyle = ATableViewCellSeparatorStyle.SingleLineEtched;
	public boolean allowsSelection = true;
	public boolean allowsMultipleSelection = false;
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.content_frame);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setupSlidingMenu();
		
		MainFragment fragment = new MainFragment();
		fragment.setHasOptionsMenu(true);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		
		mContentListener = fragment;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: mMenu.toggle(); return true;
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
		 mContentListener.onTableViewConfigurationChanged(); mMenu.toggle();
	 }
}
