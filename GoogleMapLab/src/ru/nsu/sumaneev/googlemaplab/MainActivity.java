package ru.nsu.sumaneev.googlemaplab;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity	extends Activity
							implements OnMyLocationButtonClickListener
										, ConnectionCallbacks
										, LocationListener
										, OnConnectionFailedListener
										, OnMapLongClickListener {
	
	//	sent extras keys
	public static final String EXTRA_MAP_TYPE = "MAP_TYPE";
	public static final String EXTRA_CLEAR_CIRCLES = "CLEAR_CIRCLES";
	public static final String EXTRA_CHANGE_RADIUS = "CIRCLE_RADIUS";
	
	//	started activities' IDs
	private static final int ACTIVITY_SETTINGS_ID = 1;
	
	private GoogleMap map = null;
	
	//	user's location fields
	private LocationClient mLocationClient = null;
	private LocationManager mLocationManager = null;
	
	private static final int LOCATION_REQUES_TIME = 5000;
	private static final int LOCATION_REQUES_FASTEST_TIME = 16;
	
	private static final LocationRequest LOCATION_REQUEST = LocationRequest.create()
			.setInterval(LOCATION_REQUES_TIME)
			.setFastestInterval(LOCATION_REQUES_FASTEST_TIME)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	

	//	notification
	private static final String NOTIFICATION_ACTION = "ru.nsu.sumaneev.googlemaplab.ALERT";
	private BroadcastReceiver receiver = null;
	private static List<PendingIntent> notificationPIntents = new LinkedList<PendingIntent >();

	//	circle
	private static int circleRadius = 500;
	private static float circleStrokeWidth = 1;
	private static int circleStrokeColor = Color.BLACK;
	private static int circleFillColor = Color.LTGRAY;
	
	private List<Circle> circleArray = new LinkedList<Circle>();
	
	//	camera
	private static float cameraZoom = 15f;
	private static boolean zoomChanged = false;
	
	//	speed
	private TextView speedView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		setUpMapIfNeeded();
	    setUpLocationParametersIfNeeded();
	    
	    speedView = (TextView) findViewById(R.id.main_speed_value);
	    
	    receiver = new AlertReceiver(this, (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
	    registerReceiver(receiver, new IntentFilter(NOTIFICATION_ACTION));

	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    setUpMapIfNeeded();
	    setUpLocationParametersIfNeeded();
	    mLocationClient.connect();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    if (null != mLocationClient) {
	        mLocationClient.disconnect();
	    }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	    
		unregisterReceiver(receiver);
	}

	
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (null ==  map) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (null != map) {
            	map.setMyLocationEnabled(true);
            	map.setOnMyLocationButtonClickListener(this);
            	map.setOnMapLongClickListener(this);
            }
        }
    }

	private void setUpLocationParametersIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
        if (null == mLocationManager) {
        	
        	mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        	
        }
    }
	
	/*
	 **************************************************
	 * 
	 * 				START OPTIONS METHODS 
	 * 
	 ************************************************** 
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case R.id.menu_settings:
			
			Intent intent = new Intent(this, SettingsActivity.class);
			
			intent.putExtra(EXTRA_CHANGE_RADIUS, circleRadius);
			
			startActivityForResult(intent, ACTIVITY_SETTINGS_ID);
			
			break;
			
		case R.id.menu_exit:
			
			finish();
			
			break;
		
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 **************************************************
	 * 
	 * 				END OPTIONS METHODS 
	 * 
	 ************************************************** 
	 */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (null == data) {
			return;
		}
		
		switch (requestCode) {
		
		case ACTIVITY_SETTINGS_ID:
			
			{
				map.setMapType(data.getIntExtra(EXTRA_MAP_TYPE, map.getMapType()));
			}
			
			{
				if ( data.getBooleanExtra(EXTRA_CLEAR_CIRCLES, false) ) {
					
					for (Circle circle : circleArray) {
						circle.remove();
					}
					
					for (PendingIntent pIntent: notificationPIntents) {
						
						mLocationManager.removeProximityAlert(pIntent);
						
					}
					
				}
			}
			
			{
				int newRadius = data.getIntExtra(EXTRA_CHANGE_RADIUS, 0);
				
				if ( newRadius > 0 ) {
					
					circleRadius = newRadius;
				}
			
				
			}
			
			return;
		
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 **************************************************
	 * 
	 * 				START LOCATIONS METHODS 
	 * 
	 ************************************************** 
	 */
	
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(LOCATION_REQUEST, this);
		
	}

	@Override
	public void onDisconnected() {
				
	}

	@Override
	public boolean onMyLocationButtonClick() {
		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(this, "Connection faild", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		
		speedView.setText( Integer.toString(Math.round(location.getSpeed())) );
		
		if (zoomChanged) {
			cameraZoom = map.getCameraPosition().zoom;
		}
		else {
			zoomChanged = true;
		}
		
		CameraPosition position = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(cameraZoom)
                .build();
		
		changeCamera(CameraUpdateFactory.newCameraPosition(position));
		
	}
	
	/*
	 **************************************************
	 * 
	 * 				END LOCATIONS METHODS 
	 * 
	 ************************************************** 
	 */

	/*
	 **************************************************
	 * 
	 * 				START CIRCLE METHODS 
	 * 
	 ************************************************** 
	 */
	

	@Override
	public void onMapLongClick(LatLng point) {
		
		Circle circle = map.addCircle(new CircleOptions()
        .center(point)
        .radius(circleRadius)
        .strokeWidth(circleStrokeWidth)
        .strokeColor(circleStrokeColor)
        .fillColor(circleFillColor));
		
		circleArray.add(circle);
		
		Intent notificationIntent = new Intent(NOTIFICATION_ACTION);
		PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);
		notificationPIntents.add(pIntent);
		
		mLocationManager.addProximityAlert(point.latitude, point.longitude, circleRadius, -1, pIntent);
	}
	
	/*
	 **************************************************
	 * 
	 * 				END CIRCLE METHODS 
	 * 
	 ************************************************** 
	 */
	
	/*
	 **************************************************
	 * 
	 * 				START CAMERA METHODS 
	 * 
	 ************************************************** 
	 */

	
	private void changeCamera(CameraUpdate update) {
                
		map.animateCamera(update, null);
    }
}
