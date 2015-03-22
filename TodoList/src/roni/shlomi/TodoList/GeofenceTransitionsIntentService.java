package roni.shlomi.TodoList;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.TodoList.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Listens for geofence transition changes.
 */

public class GeofenceTransitionsIntentService extends IntentService {

	private Context mContext;
	private static final String TAG = "geofence-transitions-service";
	private GoogleApiClient mGoogleApiClient;

	public GeofenceTransitionsIntentService(Context c) {
		super(TAG);
		Log.v("2", "2123");
		mContext = c;
		// TODO Auto-generated constructor stub
	}

	public GeofenceTransitionsIntentService() {
		// Use the TAG to name the worker thread.
		super(TAG);
		Log.v("22", "222");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("11", "111");

	}

	public void onStartCommand(Intent intent, int startId) {
		Log.v("333", "333");
		onHandleIntent(intent);

	}

		
		  @Override
		    protected void onHandleIntent(Intent intent) {
			  Log.v(TAG, "starting on handle");
		        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
		        if (geofencingEvent.hasError()) {
		            String errorMessage = "errorrr";
		            Log.e(TAG, errorMessage);
		            return;
		        }
		        
		        

		// Get the transition type.
		int geofenceTransition = geofencingEvent.getGeofenceTransition();

		// Test that the reported transition was of interest.
		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {


			// Get the geofences that were triggered. A single event can trigger
			// multiple geofences.
			 List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

			// Get the transition details as a String.
			 String geofenceTransitionDetails = getGeofenceTransitionDetails(
	                    this,
	                    geofenceTransition,
	                    triggeringGeofences
	            );

				// Send notification and log the transition details.
			  sendNotification(geofenceTransitionDetails);
	            Log.i(geofenceTransitionDetails, geofenceTransitionDetails);

		} else {
			 Log.e(TAG, "eerrrorrrrR");
		}
	}

		  private String getGeofenceTransitionDetails(
		            Context context,
		            int geofenceTransition,
		            List<Geofence> triggeringGeofences) {

	    String geofenceTransitionString = getTransitionString(geofenceTransition);

		// Get the Ids of each geofence that was triggered.
	    ArrayList triggeringGeofencesIdsList = new ArrayList();
	    for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
	    String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

	    return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
	}

	private void sendNotification(String message) {
		Log.v("geofence", "sending notification:" + message);
		NotificationManager nm = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext) // builder for the notification
				.setSmallIcon(R.drawable.icon) // the icon
				.setContentTitle("Geofence!!!!") // the title
				.setContentText(message); // the message
		nm.notify(0, mBuilder.build());
		Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(500);

	}

	private String getTransitionString(int transitionType) {
		switch (transitionType) {
		case Geofence.GEOFENCE_TRANSITION_ENTER:
			return "enter";
		case Geofence.GEOFENCE_TRANSITION_EXIT:
			return "exit";
		default:
			return "unkonwn";
		}
	}
}
