package com.example.TodoList;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver 
{
	String messege;
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		messege = intent.getExtras().getString("messege");
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)	//builder for the notification
		.setSmallIcon(R.drawable.icon)	//the icon
		.setContentTitle("Todo Notification! :)")	//the title
		.setContentText(messege);	//the message
		nm.notify(0, mBuilder.build());	
		 Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		 v.vibrate(500);
	}
}

