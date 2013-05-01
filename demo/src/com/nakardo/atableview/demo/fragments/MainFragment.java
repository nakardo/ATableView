package com.nakardo.atableview.demo.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.nakardo.atableview.demo.R;
import com.nakardo.atableview.demo.cells.MyCustomCell;
import com.nakardo.atableview.demo.interfaces.OnSlidingMenuItemClickedListener;
import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView.ATableViewCellAccessoryType;
import com.nakardo.atableview.protocol.ATableViewDataSourceExt;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

public class MainFragment extends SherlockFragment implements OnSlidingMenuItemClickedListener {
	private List<List<String>> mCapitals;
	private List<List<String>> mProvinces;
	private String[] mRegions = {
		"Northwest", "Gran Chaco", "Mesopotamia", "Pampas", "Cuyo", "Patagonia", "Capital City", "About"
	};
	private String[] mNotes = {
		null, "Southwestern Santiago del Estero is sometimes considered part of the Sierras area.",
		null, "Southern part of La Pampa is sometimes called Dry Pampa and included in Patagonia.",
		"La Rioja is sometimes considered part of Cuyo region instead of the Northwest.", null,
		null, null
	};
	
	private ATableView mTableView;
	private ATableViewStyle mTableViewStyle = ATableViewStyle.Grouped;
	private ATableViewCellSeparatorStyle mTableViewSeparatorStyle = ATableViewCellSeparatorStyle.SingleLineEtched;
	
	private static List<List<String>> createProvincesList() {
		List<List<String>> provinces = new ArrayList<List<String>>();
		
		provinces.add(Arrays.asList(new String[] { "Jujuy", "Salta", "Tucumán", "Catamarca" }));
		provinces.add(Arrays.asList(new String[] { "Formosa", "Chaco", "Santiago del Estero" }));
		provinces.add(Arrays.asList(new String[] { "Misiones", "Entre Ríos", "Corrientes" }));
		provinces.add(Arrays.asList(new String[] { "Córdoba", "Santa Fe", "La Pampa", "Buenos Aires" }));
		provinces.add(Arrays.asList(new String[] { "San Juan", "La Rioja", "Mendoza", "San Luis" }));
		provinces.add(Arrays.asList(new String[] { "Neuquén", "Chubut", "Santa Cruz", "Tierra del Fuego" }));
		provinces.add(Arrays.asList(new String[] { "Autonomous City of Buenos Aires" }));
		provinces.add(Arrays.asList(new String[] { "ATableView intends to imitate same object model proposed on UIKit for building tables, " +
				"so it's not only limited on theming Android ListView.\n\nCopyright 2012 Diego Acosta\n\nContact me at diegonake@gmail.com / @nakardo"}));
		
		return provinces;
	}
	
	private static List<List<String>> createCapitalsList() {
		List<List<String>> capitals = new ArrayList<List<String>>();
		
		capitals.add(Arrays.asList(new String[] { "San Salvador de Jujuy", "Salta", "San Miguel de Tucuman", "S.F.V. de Catamarca" }));
		capitals.add(Arrays.asList(new String[] { "Formosa", "Resistencia", "Santiago del Estero" }));
		capitals.add(Arrays.asList(new String[] { "Posadas", "Parana", "Corrientes" }));
		capitals.add(Arrays.asList(new String[] { "Cordoba", "Santa Fe", "Santa Rosa", "Capital Federal" }));
		capitals.add(Arrays.asList(new String[] { "San Juan", "La Rioja", "Mendoza", "San Luis" }));
		capitals.add(Arrays.asList(new String[] { "Viedma", "Neuquén", "Rawson", "Rio Gallegos", "Ushuaia" }));
		
		return capitals;
	}
	
	private void createTableView() {
		mTableView = new ATableView(mTableViewStyle, getActivity());
		mTableView.setSeparatorStyle(mTableViewSeparatorStyle);
        mTableView.setDataSource(new SampleATableViewDataSource());
        mTableView.setDelegate(new SampleATableViewDelegate());
        
        ViewGroup container = (ViewGroup) getView();
        container.addView(mTableView);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	FrameLayout view = new FrameLayout(getActivity());
    	return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	mCapitals = createCapitalsList();
        mProvinces = createProvincesList();
    	
    	if (savedInstanceState == null) {
    		createTableView();
		}
    }

	private class SampleATableViewDataSource extends ATableViewDataSourceExt {
		
		private Drawable getDrawableForRowAtIndexPath(NSIndexPath indexPath) {
	    	Drawable drawable = null;
			switch (indexPath.getRow()) {
				case 0: drawable = getResources().getDrawable(R.drawable.san_juan); break;
				case 1: drawable = getResources().getDrawable(R.drawable.la_rioja); break;
				case 2: drawable = getResources().getDrawable(R.drawable.mendoza); break;
				default:drawable = getResources().getDrawable(R.drawable.san_luis);
			}
			
			return drawable;
	    }
		
		private void setupImageView(ATableViewCell cell, NSIndexPath indexPath) {
			ImageView imageView = cell.getImageView();
			if (indexPath.getSection() == 4) {
				int paddingLeft = (int) Math.ceil(8 * getResources().getDisplayMetrics().density);
				imageView.setPadding(paddingLeft, 0, 0, 0);
				imageView.setImageDrawable(getDrawableForRowAtIndexPath(indexPath));
			} else {
				imageView.setPadding(0, 0, 0, 0);
				imageView.setImageDrawable(null);
			}
		}
		
		private void setupLayout(ATableViewCell cell, NSIndexPath indexPath) {
			TextView textLabel = (TextView) cell.findViewById(R.id.textLabel);
			
			int maxLines = 1;
			TruncateAt truncateAt = TruncateAt.END;
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textLabel.getLayoutParams();
			params.topMargin = params.bottomMargin = 0;
			
			// add multiline to textView apart from WRAP_CONTENT to make height dynamic.
			if (indexPath.getSection() == 7) {
				maxLines = Integer.MAX_VALUE;
				params.topMargin = (int) getResources().getDimension(R.dimen.atv_cell_content_margin);
				params.bottomMargin = (int) getResources().getDimension(R.dimen.atv_cell_content_margin);
				truncateAt = null;
			}
			
			textLabel.setLayoutParams(params);
			textLabel.setMaxLines(maxLines);
			textLabel.setEllipsize(truncateAt);
		}
		
		@Override
		public ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			String cellIdentifier = "CellIdentifier0";
			ATableViewCellStyle cellStyle = ATableViewCellStyle.Default;
			ATableViewCellAccessoryType accessoryType = ATableViewCellAccessoryType.None;
			ATableViewCellSelectionStyle selectionStyle = ATableViewCellSelectionStyle.Blue;
			
			// set proper style and identifier for cells on each section.
			int section = indexPath.getSection();
			if (section == 0) {
				cellIdentifier = "CellIdentifier1";
				cellStyle = ATableViewCellStyle.Subtitle;
				accessoryType = ATableViewCellAccessoryType.DisclosureIndicator;
			} else if (section == 1) {
				cellIdentifier = "CellIdentifier2";
				cellStyle = ATableViewCellStyle.Value1;
				accessoryType = ATableViewCellAccessoryType.DisclosureButton;
			} else if (section == 2) {
				cellIdentifier = "CellIdentifier3";
				cellStyle = ATableViewCellStyle.Value2;
				accessoryType = ATableViewCellAccessoryType.Checkmark;
			} else if (section == 5) {
				cellIdentifier = "CustomCellIdentifier";
				selectionStyle = ATableViewCellSelectionStyle.Gray;
			} else if (section == 7) {
				selectionStyle = ATableViewCellSelectionStyle.None;
			}
			
			// get row data.
			int row = indexPath.getRow();
			String province = mProvinces.get(section).get(row);
			
			ATableViewCell cell = null;
			if (section != 5) {
				cell = dequeueReusableCellWithIdentifier(cellIdentifier);
				if (cell == null) {
					cell = new ATableViewCell(cellStyle, cellIdentifier, getActivity());
				}
				
				cell.setSelectionStyle(selectionStyle);
				cell.setAccessoryType(accessoryType);
				
				// imageView
				setupImageView(cell, indexPath);
				
				// textLabel
				cell.getTextLabel().setText(province);
				
				// detailTextLabel
				TextView detailTextLabel = cell.getDetailTextLabel();
				if (detailTextLabel != null) {
					String capital = mCapitals.get(section).get(row);
					detailTextLabel.setText(capital);
				}
				
				setupLayout(cell, indexPath);
			} else {
				MyCustomCell customCell = (MyCustomCell)dequeueReusableCellWithIdentifier(cellIdentifier);
				if (cell == null) {
					customCell = new MyCustomCell(ATableViewCellStyle.Default, cellIdentifier, getActivity());
					customCell.setSelectionStyle(selectionStyle);
					customCell.setAccessoryType(accessoryType);
				}
				
				// customLabel
				customCell.getTextLabel().setText(province);
				
				cell = customCell;
			}
			
			return cell;
		}

		@Override
		public int numberOfRowsInSection(ATableView tableView, int section) {
			return mProvinces.get(section).size();
		}
		
		@Override
		public int numberOfSectionsInTableView(ATableView tableView) {
			return mRegions.length;
		}
		
		@Override
		public String titleForHeaderInSection(ATableView tableView, int section) {
			return mRegions[section];
		}
		
		@Override
		public String titleForFooterInSection(ATableView tableView, int section) {
			return mNotes[section];
		}
		
		@Override
		public int numberOfRowStyles() {
			return 5;
		}

		@Override
		public int styleForRowAtIndexPath(NSIndexPath indexPath) {
			int style = indexPath.getRow();
			
			switch (indexPath.getSection()) {
				case 3: case 4: case 6: case 7: style = 3; break; // default
				case 5: style = 4; break; // custom
				default: break;
			}
			
			return style;
		}
	}
	
	private class SampleATableViewDelegate extends ATableViewDelegate {
		
		@Override
		public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			if (indexPath.getSection() == 7) {
				return ListView.LayoutParams.WRAP_CONTENT;
			}
			
			return super.heightForRowAtIndexPath(tableView, indexPath);
		}
		
		@Override
		public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			String text ="Selected IndexPath [" + indexPath.getSection() + "," + indexPath.getRow() + "]";
			Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
			toast.show();
		}
		
		@Override
		public void accessoryButtonTappedForRowWithIndexPath(ATableView tableView, NSIndexPath indexPath) {
			String text = "Tapped DisclosureButton at indexPath [" + indexPath.getSection() + "," + indexPath.getRow() + "]";
			Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public void onStyleATableViewStyleSelected(ATableViewStyle tableViewStyle,
			ATableViewCellSeparatorStyle separatorStyle) {
		
		mTableViewStyle = tableViewStyle;
		mTableViewSeparatorStyle = separatorStyle;
		
		((ViewGroup) getView()).removeView(mTableView);
		createTableView();
	}
}
