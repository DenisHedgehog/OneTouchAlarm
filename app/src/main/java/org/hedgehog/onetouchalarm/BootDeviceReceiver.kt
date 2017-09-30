package org.hedgehog.onetouchalarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by hedgehog on 01.09.17.
 */

class BootDeviceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("REBOOT RECEIVER", "IT RECEIVED")
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.i("REBOOT RECEIVER", "BOOT COMPLETED")
            val alarmIntent = Intent(context, AlarmReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (getAlarmDateSharedPreferences(context) != 0L) {
                if (Build.VERSION.SDK_INT >= 23) {
                    manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getAlarmDateSharedPreferences(context), pendingIntent)
                    Log.i("REBOOT RECEIVER", "ALARM WAS RESET")
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, getAlarmDateSharedPreferences(context), pendingIntent)
                    Log.i("REBOOT RECEIVER", "ALARM WAS RESET")
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(MainActivity.NOTIFICATION_ID, MainActivity.notificationAboutAlarm(context))
            }
        }
    }

    private fun getAlarmDateSharedPreferences(context: Context): Long {
        return context.getSharedPreferences(context.getString(R.string.my_shared_preferences), AppCompatActivity.MODE_PRIVATE).getLong(context.getString(R.string.shared_preferences_alarm_time), 0L)
    }

}