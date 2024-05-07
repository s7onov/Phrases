package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val newIntent = Intent(context, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_IMMUTABLE)
        val appDatabase = (context.applicationContext as MyApplication).database
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Your phrase of the day")
            //.setContentText(Book.phrases.random())
            .setContentText(appDatabase.getPhraseDao().getAll().random().phrase)
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pIntent)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(ID, notificationBuilder.build())
    }

}
