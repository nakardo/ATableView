package com.nakardo.atableview.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.protocol.ATableViewDataSourceExt;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

public class MainActivity extends Activity {
	private List<List<String>> mCapitals;
	private List<List<String>> mProvinces;
	private String[] mRegions = {
		"Northwest", "Gran Chaco", "Mesopotamia", "Cuyo", /* "Pampas", "Patagonia" */
	};
	
	private static List<List<String>> createProvincesList() {
		List<List<String>> provinces = new ArrayList<List<String>>();
		
		provinces.add(Arrays.asList(new String[] { "Jujuy", "Salta", "Tucumán", "Catamarca" }));
		provinces.add(Arrays.asList(new String[] { "Formosa", "Chaco", "Santiago del Estero" }));
		provinces.add(Arrays.asList(new String[] { "Misiones", "Entre Ríos", "Corrientes" }));
		provinces.add(Arrays.asList(new String[] { "San Juan", "La Rioja", "Mendoza", "San Luis" }));
		provinces.add(Arrays.asList(new String[] { "Córdoba", "Santa Fe", "La Pampa", "Buenos Aires" }));
		provinces.add(Arrays.asList(new String[] { "Rio Negro", "Neuquén", "Chubut", "Santa Cruz", "Tierra del Fuego" }));
		
		return provinces;
	}
	
	private static List<List<String>> createCapitalsList() {
		List<List<String>> capitals = new ArrayList<List<String>>();
		
		capitals.add(Arrays.asList(new String[] { "San Salvador de Jujuy", "Salta", "San Miguel de Tucuman", "S.F.V. de Catamarca" }));
		capitals.add(Arrays.asList(new String[] { "Formosa", "Resistencia", "Santiago del Estero" }));
		capitals.add(Arrays.asList(new String[] { "Posadas", "Parana", "Corrientes" }));
		capitals.add(Arrays.asList(new String[] { "San Juan", "La Rioja", "Mendoza", "San Luis" }));
		capitals.add(Arrays.asList(new String[] { "Cordoba", "Santa Fe", "Santa Rosa", "Capital Federal" }));
		capitals.add(Arrays.asList(new String[] { "Viedma", "Neuquén", "Rawson", "Rio Gallegos", "Ushuaia" }));
		
		return capitals;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mCapitals = createCapitalsList();
        mProvinces = createProvincesList();
        
        ATableView tableView = new ATableView(ATableViewStyle.Grouped, this);
        tableView.setDataSource(new SampleATableViewDataSource());
        tableView.setDelegate(new SampleATableViewDelegate());
        
        FrameLayout container = (FrameLayout)findViewById(android.R.id.content);
        container.addView(tableView);
    }

	private class SampleATableViewDataSource extends ATableViewDataSourceExt {
		
		@Override
		public ATableViewCell cellForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			String cellIdentifier = "CellIdentifier0";
			ATableViewCellStyle style = ATableViewCellStyle.Default;
			
			int section = indexPath.getSection();
			if (section == 0) {
				cellIdentifier = "CellIdentifier1";
				style = ATableViewCellStyle.Subtitle;
			} else if (section == 1) {
				cellIdentifier = "CellIdentifier2";
				style = ATableViewCellStyle.Value1;
			} else if (section == 2) {
				cellIdentifier = "CellIdentifier3";
				style = ATableViewCellStyle.Value2;
			} 
			
			ATableViewCell cell = dequeueReusableCellWithIdentifier(cellIdentifier);
			if (cell == null) {
				cell = new ATableViewCell(style, cellIdentifier, MainActivity.this);
				cell.setSelectionStyle(ATableViewCellSelectionStyle.Blue);
			}
			
			int row = indexPath.getRow();
			
			TextView textLabel = cell.getTextLabel();
			if (textLabel != null) {
				String province = mProvinces.get(section).get(row);
				textLabel.setText(province);
			}
			
			TextView detailTextLabel = cell.getDetailTextLabel();
			if (detailTextLabel != null) {
				String capital = mCapitals.get(section).get(row);
				detailTextLabel.setText(capital);
			}
			
			return cell;
		}

		@Override
		public int numberOfRowsInSection(ATableView tableView, int section) {
//			return mCapitals.get(section).size();
			return 3;
		}
		
		@Override
		public int numberOfSectionsInTableView(ATableView tableView) {
			return mRegions.length;
		}

		@Override
		public int numberOfRowStyles() {
			return 4;
		}

		@Override
		public int styleForRowAtIndexPath(NSIndexPath indexPath) {
			return indexPath.getSection();
		}
	}
	
	private class SampleATableViewDelegate extends ATableViewDelegate {
		
		@Override
		public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			CharSequence text = String.format("Selected IndexPath [%d, %d]", indexPath.getSection(), indexPath.getRow());
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
			toast.show();
		}
		
		@Override
		public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
			return 44;
		}
	}
}
