package com.example.TodoList;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class map extends FragmentActivity {
	private String latitude;
	private String longitude;
	private LatLng LOCATION;
	private GoogleMap map;
	private RadioButton radio0,radio1;

	@Override
	protected void onCreate(Bundle MapActivity) {
		// TODO Auto-generated method stub
		super.onCreate(MapActivity);
		setContentView(R.layout.map);

		latitude = getIntent().getExtras().getString("Latitude");
		longitude = getIntent().getExtras().getString("Longitude");
		double Llatitude = Double.parseDouble(latitude);	//left to right
		double Llongitude = Double.parseDouble(longitude);	//top to down

		radio0 = (RadioButton) findViewById(R.id.radio0);
		radio1  = (RadioButton) findViewById(R.id.radio1);

		//get dynamic area
		LOCATION = new LatLng(Llatitude, Llongitude);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.addMarker(new MarkerOptions().position(LOCATION).title("Im Here!!!"));
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		//points on the map by google maps of SHENKAR COLLEGE
		//my home
		if (Llatitude < 31.891986 && Llatitude > 31.891238 && Llongitude > 34.808573 && Llongitude < 34.809775 ){
			//shenkar
			//if (Llatitude > 32.089408 && Llatitude < 32.090784 && Llongitude > 34.802017 && Llongitude < 34.803736 ){
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.map)
			.setContentTitle("GEO-POSITION: SHENKAR!")
			.setContentText("LAT:" + Llatitude + " ,LONG: " + Llongitude);
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			nm.notify(1, mBuilder.build());	

			 // Vibrate for 500 milliseconds
			 Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
			 v.vibrate(500);
		}
		else	//im not in shenkar
		{
			Log.v("Llatitude1",latitude);
			Log.v("Llongitude",longitude);
		}
	}

	public void onClick_findMyAndroid(View v){
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION, 16);
		map.animateCamera(update);
	}

	public void onClick_satteliteView(View v){
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		radio0.setChecked(true);
		radio1.setChecked(false);
	}
	public void onClick_regularView(View v){
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		radio1.setChecked(true);
		radio0.setChecked(false);
	}
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}