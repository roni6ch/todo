package roni.shlomi.TodoList;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.TodoList.R;

public class page2task extends Activity {
	TextView tv;
	TimePicker tp = null;
	AlarmManager am = null;
	String Latitude, Longitude;
	private String userChosenLat;
	private String userChosenLong;
	private static boolean userChosenMapPosition = false;
	Cursor cursor2;
	String stringRecd;
	Boolean editORadd = false;
	int intRecd;
	private boolean checker = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page2);
		tv = (TextView) findViewById(R.id.displayText1);
		tp = (TimePicker) findViewById(R.id.timePicker1);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Latitude = getIntent().getExtras().getString("Latitude");
		Longitude = getIntent().getExtras().getString("Longitude");
		intRecd = getIntent().getExtras().getInt("position");
		if (getIntent().getExtras().getString("userChosenLat") != null) {
			userChosenLat = getIntent().getExtras().getString("userChosenLat");
			userChosenLong = getIntent().getExtras()
					.getString("userChosenLong");
		}
		// we came back from map layout
		if (getIntent().getExtras().getString("realLatitude") != null) {
			Latitude = getIntent().getExtras().getString("realLatitude");
			Longitude = getIntent().getExtras().getString("realLongitude");

		}

	}

	protected void onResume() {
		super.onResume();

		editORadd = getIntent().getExtras().getBoolean("Edit");
		stringRecd = getIntent().getExtras().getString("task");
		intRecd = getIntent().getExtras().getInt("position");
		// i have back from the map the location
		if (isUserChosenMapPosition() == true) {
			checker =true;
			userChosenLat = getIntent().getExtras().getString("userChosenLat");
			userChosenLong = getIntent().getExtras()
					.getString("userChosenLong");
		}

		if (editORadd == true) // true = edit, false = add
		{
			tv.setText(stringRecd);
		} else {
			Toast.makeText(getApplicationContext(), "Please write a new task",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void onClickAddnewTask(View v) {
		int hour = tp.getCurrentHour();
		int minute = tp.getCurrentMinute();
		Calendar calendar = Calendar.getInstance(); // the creation of the
													// Clock!
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.putExtra("messege", tv.getText().toString());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pendingIntent);

		String task = tv.getText().toString();
		if (editORadd == false) // if i want ADD
		{
			if (isUserChosenMapPosition() == true)
				MainActivity.newTask = MainActivity.myDb.insertRow(task,
						userChosenLat, userChosenLong, 0);
			else
				// the user did not enter location on map
				MainActivity.newTask = MainActivity.myDb.insertRow(task, "0.0",
						"0.0", 0);
			setUserChosenMapPosition(false);
		} else // if i want EDIT
		{
			MainActivity.newTask = intRecd;
			MainActivity.myItems.set(intRecd, task);

			cursor2 = MainActivity.myDb.getAllRows();
			if (isUserChosenMapPosition() == true || checker==true) {

				MainActivity.myDb.updateRow(
						MainActivity.myDb.getIthRowId(intRecd), task,
						userChosenLat, userChosenLong, 0);
			} else
				// the user did not enter location on map
				MainActivity.myDb.updateRow(
						MainActivity.myDb.getIthRowId(intRecd), task, "0", "0",
						0);
		}
		
		finish();
	}

	public void onClick_Map(View v) {
		finish();
		Intent i = new Intent(this, map.class);
		i.putExtra("Latitude", Latitude.substring(0, 8));
		i.putExtra("Longitude", Longitude.substring(0, 8));
		i.putExtra("task", tv.getText().toString());
		i.putExtra("editOrAdd", editORadd);
		i.putExtra("position", intRecd);

		if (editORadd == true) {
			i.putExtra("userChosenLat", userChosenLat);
			i.putExtra("userChosenLong", userChosenLong);
		}
		startActivity(i);

	}

	public boolean isUserChosenMapPosition() {
		return userChosenMapPosition;
	}

	public static void setUserChosenMapPosition(boolean userChosenMapPosition1) {
		userChosenMapPosition = userChosenMapPosition1;
	}

}
