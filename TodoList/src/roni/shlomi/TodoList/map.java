package roni.shlomi.TodoList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.TodoList.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class map extends FragmentActivity implements OnMapClickListener,
		OnMapLongClickListener {
	private String latitude;
	private String longitude;
	private LatLng LOCATION, LOCATION2;
	private GoogleMap map;
	private RadioButton radio0, radio1;
	private LatLng newPoint = null;
	public CircleOptions circleOptions, circleOptions2;
	private MarkerOptions realPositionMarker, realPositionMarker2;
	private MarkerOptions userPositionMarker;
	public Marker userMarker = null, userMarker2 = null;
	public Circle userChosenCircle = null, userChosenCircle2 = null;
	public String task;
	private Boolean editOrAdd;
	int intRecd;
	
	@Override
	protected void onCreate(Bundle MapActivity) {
		// TODO Auto-generated method stub
		super.onCreate(MapActivity);
		setContentView(R.layout.map);
		
		intRecd = getIntent().getExtras().getInt("position");
		latitude = getIntent().getExtras().getString("Latitude");
		longitude = getIntent().getExtras().getString("Longitude");
		if(getIntent().getExtras().getString("task")!=null)
			task = getIntent().getExtras().getString("task");
		editOrAdd = getIntent().getExtras().getBoolean("editOrAdd");

		double Llatitude = Double.parseDouble(latitude); // left to right
		double Llongitude = Double.parseDouble(longitude); // top to down

		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1 = (RadioButton) findViewById(R.id.radio1);

		// get dynamic area
		LOCATION = new LatLng(Llatitude, Llongitude);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		realPositionMarker = new MarkerOptions().position(LOCATION).title(
				"You Are Here!");
		map.addMarker(realPositionMarker);
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);

		
		// Instantiates a new CircleOptions object and defines the center and
		// radius
		circleOptions = new CircleOptions().center(LOCATION).radius(100); // In
																			// meters

		// Get back the mutable Circle
		map.addCircle(circleOptions);

		// we came back to map layout from page2task
		//Log.v("a",getIntent().getExtras().getString("userChosenLat"));
		if (getIntent().getExtras().getString("userChosenLat") != null && Double.parseDouble(getIntent().getExtras().getString("userChosenLat"))!=0.0 ) {
			double Llatitude2 = Double.parseDouble(getIntent().getExtras()
					.getString("userChosenLat")); // left to right
			double Llongitude2 = Double.parseDouble(getIntent().getExtras()
					.getString("userChosenLong")); // top to down
			LOCATION2 = new LatLng(Llatitude2, Llongitude2);
			circleOptions2 = new CircleOptions().center(LOCATION2).radius(100); // In
																				// meters

			userChosenCircle2 = map.addCircle(circleOptions2);

			realPositionMarker2 = new MarkerOptions().position(LOCATION2)
					.title("You Are Here!");
			userMarker2 = map.addMarker(realPositionMarker2);
		}
		// points on the map by google maps of SHENKAR COLLEGE
		// shenkar
		if (Llatitude > 32.089408 && Llatitude < 32.090784
				&& Llongitude > 34.802017 && Llongitude < 34.803736) {
			Toast.makeText(getBaseContext(), "You are in Shenkar!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void onClick_findMyAndroid(View v) {
		map.addMarker(new MarkerOptions().position(LOCATION).title(
				LOCATION.toString()));

		map.addCircle(circleOptions);

		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION, 16);
		map.animateCamera(update);
	}

	public void onClick_satteliteView(View v) {
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		radio0.setChecked(true);
		radio1.setChecked(false);
	}

	public void onClick_regularView(View v) {
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		radio1.setChecked(true);
		radio0.setChecked(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onMapClick(LatLng point) {
		map.animateCamera(CameraUpdateFactory.newLatLng(point));
	}

	@Override
	public void onMapLongClick(LatLng point) {
		newPoint = point;

		// if there is a marker already
		if (userChosenCircle != null) {
			userChosenCircle.remove();
			userMarker.remove();

		}
		if (userChosenCircle2 != null) {
			userChosenCircle2.remove();
			userMarker2.remove();

		}
		userPositionMarker = new MarkerOptions().position(point)
				.title("Your chosen marker").draggable(true);
		// map.addMarker(userPositionMarker);

		userMarker = map.addMarker(userPositionMarker);

		circleOptions = new CircleOptions().center(newPoint).radius(100); // In
																			// meters

		userChosenCircle = map.addCircle(circleOptions);
		page2task.setUserChosenMapPosition(true);

	}

	public void onClick_OkMap(View v) {
		// user Chosen Position
		if (newPoint != null) {
			Intent iService = new Intent(this, IntentMapService.class);
			IntentMapService.setVisited(false);
			startService(iService.putExtra("circleOptions", circleOptions));
			//startService(iService);
			
			Intent i = new Intent(this, page2task.class);
			// real position
			i.putExtra("realLatitude", latitude);
			i.putExtra("realLongitude", longitude);
			i.putExtra("task", task);
			i.putExtra("Edit", editOrAdd);
			i.putExtra("position", intRecd);

			i.putExtra("userChosenLat", String.valueOf(newPoint.latitude));
			i.putExtra("userChosenLong", String.valueOf(newPoint.longitude));
			page2task.setUserChosenMapPosition(true);
			startActivity(i);
		} else {
			Toast.makeText(getBaseContext(), "Please choose location to task",
					Toast.LENGTH_SHORT).show();
		}
	}
}