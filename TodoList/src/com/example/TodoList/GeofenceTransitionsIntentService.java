package com.example.TodoList;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
/**
 * Listens for geofence transition changes.
 */
public class GeofenceTransitionsIntentService extends IntentService{

	private Context mContext;
	public GeofenceTransitionsIntentService(Context c) {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
		mContext = c;
		// TODO Auto-generated constructor stub
	}

	 
	protected void onHandleIntent(Intent intent) {
		Log.v("geofence", "starting on handle");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
//            String errorMessage = GeofenceErrorMessages.getErrorString(this,
//                    geofencingEvent.getErrorCode());
        	
            Log.e("geofence", "geofence error");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    this,
//                    geofenceTransition,
//                    triggeringGeofences
//            );

            String message = (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)? "enter" : "exit";
			// Send notification and log the transition details.
            sendNotification(message);
//            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
//            Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
//                    geofenceTransition));
        }
    }

	private void sendNotification(String message) {
		Log.v("geofence", "sending notification:"+message);
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)	//builder for the notification
		.setSmallIcon(R.drawable.icon)	//the icon
		.setContentTitle("Geofence!!!!")	//the title
		.setContentText(message);	//the message
		nm.notify(0, mBuilder.build());	
		 Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		 v.vibrate(500);
		
	}
}
