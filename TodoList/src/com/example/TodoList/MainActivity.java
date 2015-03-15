package com.example.TodoList;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends Activity implements android.widget.AdapterView.OnItemClickListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ResultCallback<Status>
{
	protected Cursor cursor;
	static List<String> myItems = new ArrayList<String>();
	static DBAdapter myDb;
	protected ListView list;
	static long newTask = 0 ;
	protected ArrayAdapter<String> adapter ;
	protected AlarmManager am = null;
	int selectedItemPosition = -1;
	boolean alreadyPressed = false,canEdit = false;
	public String buttonText, mLastUpdateTime;
	private GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation,mCurrentLocation;
	protected TextView mLatitudeText,mLongitudeText,mLastUpdateTimeTextView;
	private boolean mRequestingLocationUpdates = true;
	protected LocationRequest mLocationRequest;
	
	PendingIntent mGeofencePendingIntent = null;

	List<Geofence> mGeofenceList = new ArrayList<Geofence>();
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myDb = new DBAdapter(this);
		myDb.open();
		cursor = myDb.getAllRows();
		populateListView();
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		buildGoogleApiClient(); //connect to the google API services
		mLatitudeText  = (TextView) findViewById(R.id.mLatitude);
		mLongitudeText  = (TextView) findViewById(R.id.mLongitude);
		mLastUpdateTimeTextView  = (TextView) findViewById(R.id.mLastUpdateTimeTextView);
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
		mLastUpdateTimeTextView.setText(mLastUpdateTime);
		mGoogleApiClient.connect(); //connect the google API services
		createLocationRequest();	//make the locations attitudes
		
		
		
	}

	/**
	 * @param locationEntry **************************************************************************************/
	private void geoFence(Location locationEntry) {
		
		mGeofenceList.add(new Geofence.Builder()
	    // Set the request ID of the geofence. This is a string to identify this
	    // geofence.
	    .setRequestId("myLocation")

	    .setCircularRegion(
	    		locationEntry.getLatitude(),
	    		locationEntry.getLongitude(),
	            Constants.GEOFENCE_RADIUS_IN_METERS
	    )
	    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
	    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
	            Geofence.GEOFENCE_TRANSITION_EXIT)
	    .build());
		
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
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
	
	/****************************************************************************************/

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();
	}

	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(2000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	@Override
	public void onConnected(Bundle connectionHint) {	//Get the Last Known Location
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		Log.v("latitude",String.valueOf(mLastLocation.getLatitude()));
		Log.v("lonitude",String.valueOf(mLastLocation.getLongitude()));
		Log.v("accurecy",String.valueOf(mLastLocation.getAccuracy()));
		geoFence(mLastLocation);
		
////////////////
		if (mLastLocation != null) {
			mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()).substring(0, 8));
			mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()).substring(0, 8));


			// Determine whether a Geocoder is available.
			if (!Geocoder.isPresent()) {
				Toast.makeText(this, "ddd",
						Toast.LENGTH_LONG).show();
				return;
			}

		}
		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
		
		LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);
	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mCurrentLocation = location;
		mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
		updateUI();
	}

	private void updateUI() {
		mLatitudeText.setText(String.valueOf(mCurrentLocation.getLatitude()));
		mLongitudeText.setText(String.valueOf(mCurrentLocation.getLongitude()));
		mLastUpdateTimeTextView.setText(mLastUpdateTime);
	}

	/********************************************************************************/
	/*			from here the code of the app 		*/
	public void populateListView()	//display the list on the ListView GUI
	{
		//put all tasks in myItems Array List
		displayRecord(cursor);
		// Build Adapter
		adapter = new ArrayAdapter<String>( this, R.layout.da_item,myItems); 
		// Configure the list view.
		list = (ListView) findViewById(R.id.listViewMain1);
		// Items to be displayed (connect between the adapter (that have the "myItems" ArrayList) and the listView)
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);		//listener for touching each row to delete

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) //listener in background
	{
		buttonText = (list.getItemAtPosition(position).toString());	//for the onClickEditButton() function
		if(selectedItemPosition == position)	//in case of selecting the same row
		{
			alreadyPressed=!alreadyPressed;		//it is already pressed
			if (alreadyPressed == true)
			{
				view.setBackgroundColor(Color.TRANSPARENT);	//for the second time pressed on the row
				canEdit = false;
			}
			else
			{
				view.setBackgroundColor(Color.LTGRAY);	//for the third time
				canEdit = true;
			}
		}
		else	//in case of selecting different row
		{
			alreadyPressed = false;	//its not pressed yet
			view.setBackgroundColor(Color.LTGRAY);
			canEdit = true;
			if(selectedItemPosition!=-1)
				getViewByPosition(selectedItemPosition,list).setBackgroundColor(Color.TRANSPARENT);
		}
		selectedItemPosition  = position;
	}

	public View getViewByPosition(int pos, ListView listView) 
	{
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition ) 
		{
			return listView.getAdapter().getView(pos, null, listView);
		} 
		else 
		{
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	public void onClickEditButton(View v)	//Edit!!
	{
		if(canEdit == true)
		{
			canEdit=false;
			Intent i = new Intent(this, page2task.class);
			i.putExtra("Edit", true);
			i.putExtra("EditText", buttonText);
			i.putExtra("position", selectedItemPosition);
			startActivity(i);
		}
		else return;
	}

	public void onClickOneDone(View v)	//Done!!
	{
		if (!myItems.get(selectedItemPosition).contains("Done!"))
		{
			myDb.updateRow(selectedItemPosition, myItems.get(selectedItemPosition) + " - Done!");
			myItems.set(selectedItemPosition, myItems.get(selectedItemPosition)+ " - Done!");
			adapter.notifyDataSetChanged();	//Refresh the GUI
		}
		else if (myItems.get(selectedItemPosition).contains("Done!"))	//the item that we pressed not done.
		{
			myDb.updateRow(selectedItemPosition, myItems.get(selectedItemPosition).substring(0, myItems.get(selectedItemPosition).length()-8));
			myItems.set(selectedItemPosition, myItems.get(selectedItemPosition).substring(0, myItems.get(selectedItemPosition).length()-8));
			adapter.notifyDataSetChanged();	//Refresh the GUI
		}
	}

	public void onClickOneDelete(View v) 	// Delete Button
	{
		if(selectedItemPosition!=-1 && alreadyPressed == false)
		{
			adapter.remove(adapter.getItem(selectedItemPosition));	//delete the line from the adapter that show on the GUI
			int id = myDb.getIthRowId(selectedItemPosition);
			myDb.deleteRow(id);
			adapter.notifyDataSetChanged();	//Refresh the GUI
			getViewByPosition(selectedItemPosition,list).setBackgroundColor(Color.TRANSPARENT);	//after clicking delete its transparent
		}
		selectedItemPosition = -1;
	}
	public void onClick_Del(View v)	//Clear Button
	{
		//v.setBackgroundResource(R.drawable.clear2);
		myDb.deleteAll();
		myItems.clear();
		cursor = myDb.getAllRows();
		populateListView();
	}	
	public void displayRecord(Cursor cursor) //Display All Records
	{
		if (cursor.moveToFirst()) //if it can move to start then we have a record to print
		{
			do 
			{
				String task = cursor.getString(DBAdapter.COL_TASK);
				myItems.add(task);
			} 
			while(cursor.moveToNext());
		}
		cursor.close();
	}
	public void onClicknewTask(View v) //Add Button
	{
		Intent i = new Intent(this, page2task.class);
		i.putExtra("Edit", false);
		startActivity(i);
	}
	@Override
	protected void onRestart() 
	{
		super.onRestart(); 
		cursor = myDb.getRow(newTask);
		populateListView();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();	
		cursor.close();
		myDb.close();
	}


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}
	public void onClick_Map(View v){
		Intent i = new Intent(this, map.class);
		i.putExtra("Latitude", String.valueOf(mLastLocation.getLatitude()).substring(0, 8));
		i.putExtra("Longitude", String.valueOf(mLastLocation.getLongitude()).substring(0, 8));
		startActivity(i);

	}

	@Override
	public void onResult(Status arg0) {
		Log.v("geofence","in on result arg0:"+arg0);
		
	}
}


//1.save update - check updateRow() in db
//2.appstore
//3.broadcast all the time
