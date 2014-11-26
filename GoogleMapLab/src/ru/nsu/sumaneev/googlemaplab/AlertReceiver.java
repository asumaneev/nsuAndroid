package ru.nsu.sumaneev.googlemaplab;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

	private NotificationManager notificationManager = null;
	private NotificationCompat.Builder notificationBuilder = null;
	
	AlertReceiver(Context context, NotificationManager notificationManager) {

		this.notificationManager = notificationManager;
		
		this.notificationBuilder = new NotificationCompat.Builder(context)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(context.getResources().getString(R.string.app_name));
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent) {

		boolean flag = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
		
		sendNotification(flag);
		
	}

	
	void sendNotification(boolean flag) {
		
		String notificationText = null;
		
		if (flag) {
			notificationText = "you've entered in some area";
		}
		else {
			notificationText = "you've left some area";
		}
		
		Notification notification =  notificationBuilder
												.setContentText(notificationText)
												.build();
		
		notificationManager.notify(0, notification);
	}
}
