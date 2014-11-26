package ru.nsu.sumaneev.googlemaplab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

public class SettingsActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {

	//TODO: add setting circle radius
	
	private String[] mapTypes = null;
	
	private ListView mapTypeSettingsListView = null;
	private ArrayAdapter<CharSequence> mapTypeSettingsAdapter = null;
	private Button mapTypeButton = null;
	
	private Button clearCirclesButton = null;
	private SeekBar circleRadiusSeekBar = null;
	private TextView circleRadiusValue = null;
	private int circleRadius = 0;
	private int circleMinRadius = 0;
	
	private Intent resultIntent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mapTypes = getResources().getStringArray(R.array.map_types);
			
		mapTypeSettingsListView = (ListView) findViewById(R.id.settings_map_types_list);
		mapTypeSettingsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mapTypeSettingsAdapter = ArrayAdapter.createFromResource(this,R.array.map_types, android.R.layout.simple_list_item_single_choice);
		mapTypeSettingsListView.setAdapter(mapTypeSettingsAdapter);
		
		mapTypeButton = (Button) findViewById(R.id.settings_map_type_button);
		mapTypeButton.setOnClickListener(this);
		
		clearCirclesButton = (Button) findViewById(R.id.settings_circles_clear_button);
		clearCirclesButton.setOnClickListener(this);
		
		int currentCircleRadius = getIntent().getIntExtra(MainActivity.EXTRA_CHANGE_RADIUS, 0);
		circleMinRadius = getResources().getInteger(R.integer.settings_circle_radius_min);
		
		circleRadiusSeekBar = (SeekBar) findViewById(R.id.settings_circle_radius_seek_bar);
		circleRadiusSeekBar.setProgress(currentCircleRadius - circleMinRadius);
		circleRadiusSeekBar.setOnSeekBarChangeListener(this);
		
		
		circleRadiusValue = (TextView) findViewById(R.id.settings_circle_radius_value);
		circleRadiusValue.setText(Integer.toString(currentCircleRadius));
		
		resultIntent = new Intent();
	}
	
	private void onFinish() {
		
		if (0 != circleRadius) {
			resultIntent.putExtra(MainActivity.EXTRA_CHANGE_RADIUS, circleRadius);
		}
		
		setResult(RESULT_OK, resultIntent);
		
		finish();
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
	
		case R.id.settings_map_type_button:
			
			int pos = mapTypeSettingsListView.getCheckedItemPosition();
			
			if (pos < 0) {
				
				Toast.makeText(this, "nothing choosed", Toast.LENGTH_SHORT).show();
				
				break;
			}
			
			String type = mapTypes[pos];
			
			int result = 0;
			
			if (0 == type.compareToIgnoreCase("Normal")) {
				
				result = GoogleMap.MAP_TYPE_NORMAL;
				
			} else if (0 == type.compareToIgnoreCase("Satellite")) {
				
				result = GoogleMap.MAP_TYPE_SATELLITE;
				
			} else if (0 == type.compareToIgnoreCase("Hybrid")) {

				result = GoogleMap.MAP_TYPE_HYBRID;
				
			} else if (0 == type.compareToIgnoreCase("Terrain")) {

				result = GoogleMap.MAP_TYPE_TERRAIN;
				
			} else if (0 == type.compareToIgnoreCase("None")) {

				result = GoogleMap.MAP_TYPE_NONE;
				
			}
			
			resultIntent.putExtra(MainActivity.EXTRA_MAP_TYPE, result);
			
			break;
		case R.id.settings_circles_clear_button:
			
			resultIntent.putExtra(MainActivity.EXTRA_CLEAR_CIRCLES, true);
			
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		onFinish();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		circleRadiusValue.setText(Integer.toString(progress + circleMinRadius));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		circleRadius = seekBar.getProgress() + circleMinRadius;
	}

	
}
