package roni.shlomi.TodoList;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.TodoList.R;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class IntentMapService extends Service {

	public DBAdapter myDb;
	public static boolean visited = false;
	private LatLng LOCATION;
	public CircleOptions circleOptions = null;
	Location location;
	float[] distance = new float[2];
	float[] distance1 = new float[2];
	Cursor cursor,cursor2;
	int i = 0,j=1;
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getExtras().getParcelable("location") != null) {
			 
			location = intent.getExtras().getParcelable("location");
		}
		cursor = myDb.getAllRows();

		/** Check rows DB locations */
		if (cursor.moveToFirst()) {
			do {
				String loca = cursor.getString(DBAdapter.COL_LAT);
				Log.w("DB"+ i, loca);
			} while (cursor.moveToNext());
			i++;
			Log.w("-----","--------------------");
		}
		cursor.close();
		/** till here */
	
		cursor2 = myDb.getAllRows();

		if (cursor2.moveToFirst()) {
			do {
				String latetud = cursor2.getString(DBAdapter.COL_LAT);
				String longa = cursor2.getString(DBAdapter.COL_LONG);
				Double latD = Double.parseDouble(latetud);
				Double longD = Double.parseDouble(longa);
				
				Location.distanceBetween(latD, longD, location.getLatitude(),
						location.getLongitude(), distance1);

				// Integer.parseInt(DBAdapter.KEY_VISITED) == 0 is not visited
				
				if (distance1[0] < 200.0
						&& cursor2.getInt(DBAdapter.COL_VISITED) == 0) {
					//Log.v("close", cursor2.getString(DBAdapter.COL_TASK));
					Log.w("-close-","--------------------");
					
					NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)	//builder for the notification
					.setSmallIcon(R.drawable.map)	//the icon
					.setContentTitle(cursor2.getString(DBAdapter.COL_TASK))	//the title
					.setContentText("TODO GeoLocation!");	//the message
					nm.notify(j++, mBuilder.build());	
					 Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
					 v.vibrate(500);
					 
					myDb.updateRow(cursor2.getLong(DBAdapter.COL_ROWID),
							cursor2.getString(DBAdapter.COL_TASK),
							latetud,
							longa,
							1);

				}
			} while (cursor2.moveToNext());
		}
		cursor2.close();

		if (intent.getExtras().getParcelable("circleOptions") != null) {
			circleOptions = intent.getExtras().getParcelable("circleOptions");

			setVisited(true);

		}

		return Log.e("Service", "onStartCommand - LOOP " + i);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public IntentMapService() {
		super();
	}

	@Override
	public void onCreate() {
		myDb = new DBAdapter(this);
		myDb.open();
	}

	public LatLng getLOCATION() {
		return LOCATION;
	}

	public void setLOCATION(LatLng lOCATION) {
		LOCATION = lOCATION;
	}

	public boolean isVisited() {
		return visited;
	}

	static void setVisited(boolean b) {
		visited = b;
	}
	
	public void onDestroy() {
		super.onDestroy();
		myDb.close();
	}
}