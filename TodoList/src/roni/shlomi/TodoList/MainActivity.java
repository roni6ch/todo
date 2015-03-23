package roni.shlomi.TodoList;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity implements
		android.widget.AdapterView.OnItemClickListener, ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener, ResultCallback<Status> {
	protected Cursor cursor;
	static List<String> myItems = new ArrayList<String>();
	static DBAdapter myDb;
	protected ListView list;
	static long newTask = 0;
	protected ArrayAdapter<String> adapter;
	protected AlarmManager am = null;
	int selectedItemPosition = -1;
	boolean alreadyPressed = false, canEdit = false;
	public String buttonText, mLastUpdateTime;
	private GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation, mCurrentLocation;
	protected TextView mLatitudeText, mLongitudeText, mLastUpdateTimeTextView;
	private boolean mRequestingLocationUpdates = true;
	protected LocationRequest mLocationRequest;
	PendingIntent mGeofencePendingIntent = null;
	List<Geofence> mGeofenceList = new ArrayList<Geofence>();
	private LatLng LOCATION;
	private EasyTracker easyTracker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        easyTracker = EasyTracker.getInstance(MainActivity.this);
        easyTracker.send(MapBuilder.createEvent("TrackEventTest", "Welcome To The APP", "track_event", null).build());
        
		myDb = new DBAdapter(this);
		myDb.open();
		cursor = myDb.getAllRows();

		/** Check rows DB locations */
		if (cursor.moveToFirst()) {
			do {
				String loca = cursor.getString(DBAdapter.COL_LAT);
				Log.w("DB", loca);
			} while (cursor.moveToNext());
			Log.w("-----","--------------------");
		}
		cursor.close();
		/** till here */

		cursor = myDb.getAllRows();

		populateListView();// get the list of todo's
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		buildGoogleApiClient(); // connect to the google API services
		mGoogleApiClient.connect(); // connect the google API services
		createLocationRequest(); // make the locations attitudes

	}

	@Override
	protected void onStart() {
		super.onStart();
		
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		EasyTracker.getInstance(this).activityStop(this);
	}
	/************************************ GOOGLE API FOR LOCATION ****************************************/

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(2000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	// Get the Last Known Location
	@Override
	public void onConnected(Bundle connectionHint) {
		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);

		geoFence(mLastLocation);

		if (mLastLocation != null) {

			// Determine whether a Geocoder is available.
			if (!Geocoder.isPresent()) {
				Toast.makeText(this, "ddd", Toast.LENGTH_LONG).show();
				return;
			}

		}
		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}

		LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
				getGeofencingRequest(), getGeofencePendingIntent())
				.setResultCallback(this);
	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// send to the service the last location to update my position
		mCurrentLocation = location;
		Intent intent1 = new Intent(this, IntentMapService.class);
		startService(intent1.putExtra("location", mCurrentLocation));

	}

	/********************************************************************************/
	private void geoFence(Location locationEntry) {
		mGeofenceList.add(new Geofence.Builder()
				// Set the request ID of the geofence. This is a string to
				// identify this
				// geofence.
				.setRequestId("myLocation")

				.setCircularRegion(locationEntry.getLatitude(),
						locationEntry.getLongitude(),
						Constants.GEOFENCE_RADIUS_IN_METERS)
				.setExpirationDuration(
						Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
				.setTransitionTypes(
						Geofence.GEOFENCE_TRANSITION_ENTER
								| Geofence.GEOFENCE_TRANSITION_EXIT).build());

	}

	private GeofencingRequest getGeofencingRequest() {
		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
		builder.addGeofences(mGeofenceList);
		return builder.build();
	}

	private PendingIntent getGeofencePendingIntent() {
		// Reuse the PendingIntent if we already have it.

		if (mGeofencePendingIntent != null) {
			return mGeofencePendingIntent;
		}
		Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent
		// back when
		// calling addGeofences() and removeGeofences().
		return PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	/******************************************************************************************/
	/* from here the code of the app */
	public void populateListView() // display the list on the ListView GUI
	{
		// put all tasks in myItems Array List
		displayRecord(cursor);
		// Build Adapter
		adapter = new ArrayAdapter<String>(this, R.layout.da_item, myItems);
		// Configure the list view.
		list = (ListView) findViewById(R.id.listViewMain1);
		// Items to be displayed (connect between the adapter (that have the
		// "myItems" ArrayList) and the listView)
		list.setAdapter(adapter);
		list.setOnItemClickListener(this); // listener for touching each row to
											// delete

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) // listener in background
	{
		buttonText = (list.getItemAtPosition(position).toString()); // for the
																	// onClickEditButton()
																	// function
		if (selectedItemPosition == position) // in case of selecting the same
												// row
		{
			alreadyPressed = !alreadyPressed; // it is already pressed
			if (alreadyPressed == true) {
				view.setBackgroundColor(Color.TRANSPARENT); // for the second
															// time pressed on
															// the row
				canEdit = false;
			} else {
				view.setBackgroundColor(Color.LTGRAY); // for the third time
				canEdit = true;
			}
		} else // in case of selecting different row
		{
			alreadyPressed = false; // its not pressed yet
			view.setBackgroundColor(Color.LTGRAY);
			canEdit = true;
			if (selectedItemPosition != -1)
				getViewByPosition(selectedItemPosition, list)
						.setBackgroundColor(Color.TRANSPARENT);
		}
		selectedItemPosition = position;
	}

	public View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition
				+ listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	public void onClickEditButton(View v) // Edit!!
	{
		if (canEdit == true) {
			canEdit = false;
			Intent i = new Intent(this, page2task.class);
			i.putExtra("Edit", true);
			i.putExtra("task", buttonText);
			i.putExtra("position", selectedItemPosition);
			i.putExtra("Latitude", String.valueOf(mLastLocation.getLatitude()));
			i.putExtra("Longitude",
					String.valueOf(mLastLocation.getLongitude()));
			
			double LA = myDb.getLAT(selectedItemPosition);
			double LO = myDb.getLONG(selectedItemPosition);
			String lati = String.valueOf(LA);
			String longi = String.valueOf(LO);
			i.putExtra("userChosenLat", lati);
			i.putExtra("userChosenLong", longi);
			
			startActivity(i);
		} else
			return;
	}

	public void onClickOneDone(View v) // Done!!
	{
		double LA = myDb.getLAT(selectedItemPosition);
		double LO = myDb.getLONG(selectedItemPosition);
		String lati = String.valueOf(LA);
		String longi = String.valueOf(LO);
		if (!myItems.get(selectedItemPosition).contains("Done!")) {
			myDb.updateRow(myDb.getIthRowId(selectedItemPosition),
					myItems.get(selectedItemPosition) + " - Done!", lati, longi,0);
			myItems.set(selectedItemPosition, myItems.get(selectedItemPosition)
					+ " - Done!");
			adapter.notifyDataSetChanged(); // Refresh the GUI
			// the item that we pressed not done.
		} else if (myItems.get(selectedItemPosition).contains("Done!")) {
			myDb.updateRow(
					myDb.getIthRowId(selectedItemPosition),
					myItems.get(selectedItemPosition).substring(0,
							myItems.get(selectedItemPosition).length() - 8),
					lati, longi,0);
			myItems.set(
					selectedItemPosition,
					myItems.get(selectedItemPosition).substring(0,
							myItems.get(selectedItemPosition).length() - 8));
			adapter.notifyDataSetChanged(); // Refresh the GUI
		}
	}

	public void onClickOneDelete(View v) // Delete Button
	{
		if (selectedItemPosition != -1 && alreadyPressed == false) {
			// delete the line from the adapter that show on the GUI
			adapter.remove(adapter.getItem(selectedItemPosition));
			int id = myDb.getIthRowId(selectedItemPosition);
			myDb.deleteRow(id);
			adapter.notifyDataSetChanged(); // Refresh the GUI
			getViewByPosition(selectedItemPosition, list).setBackgroundColor(
					Color.TRANSPARENT); // after clicking delete its transparent
		}
		selectedItemPosition = -1;
	}

	public void onClick_Del(View v) // Clear Button
	{
		myDb.deleteAll();
		myItems.clear();
		cursor = myDb.getAllRows();
		populateListView();
	}

	public void displayRecord(Cursor cursor) // Display All Records
	{
		// if it can move to start then we have a record to print
		if (cursor.moveToFirst()) {
			do {
				String task = cursor.getString(DBAdapter.COL_TASK);
				myItems.add(task);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	public void onClicknewTask(View v) // Add Button
	{
		if (canEdit==true)
		{
			Toast.makeText(getApplicationContext(), "Please cancel selection first!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i = new Intent(this, page2task.class);
		i.putExtra("Edit", false);
		//my real position
		i.putExtra("Latitude", String.valueOf(mLastLocation.getLatitude()));
		i.putExtra("Longitude", String.valueOf(mLastLocation.getLongitude()));
		startActivity(i);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		cursor = myDb.getRow(newTask);
		populateListView();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myItems.clear();
		cursor.close();
		myDb.close();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@Override
	public void onConnectionSuspended(int arg0) {
	}


	@Override
	public void onResult(Status arg0) {

	}

}

// todo: 1. list copy itself when entering