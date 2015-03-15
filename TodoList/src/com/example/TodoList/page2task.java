package com.example.TodoList;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class page2task extends Activity 
{
	TextView tv;
	TimePicker tp = null;
	AlarmManager am = null;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page2);
		tv = (TextView) findViewById(R.id.displayText1);
		tp = (TimePicker) findViewById(R.id.timePicker1);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	}
	String stringRecd;
	Boolean editORadd;
	long intRecd;
	
	protected void onResume()
	{
		super.onResume();
		editORadd = getIntent().getExtras().getBoolean("Edit");
		stringRecd = getIntent().getExtras().getString("EditText");
		intRecd = getIntent().getExtras().getInt("position");
		if (editORadd == true)	//true = edit, false = add
		{
			tv.setText(stringRecd);
		}
		else 
		{
			Toast.makeText(getApplicationContext(), "Please write a new task", Toast.LENGTH_SHORT).show();
		}
	}

	public void onClickAddnewTask(View v)
	{
		int hour = tp.getCurrentHour();
		int minute = tp.getCurrentMinute();
		Calendar calendar = Calendar.getInstance();	//the creation of the Clock!
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM,Calendar.PM);
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.putExtra("messege", tv.getText().toString());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_ONE_SHOT);
		am.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);

		String task = tv.getText().toString();
		if (editORadd == false)	//if i want ADD
		{
			MainActivity.newTask = MainActivity.myDb.insertRow(task);
			finish();
		}
		else	//if i want EDIT
		{
			MainActivity.newTask = intRecd;
			MainActivity.myItems.set((int) intRecd, task);
			MainActivity.myDb.updateRow(intRecd, task);
			finish();
		}
	}
}
