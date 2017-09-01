package org.hedgehog.onetouchalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.support.v7.app.AppCompatActivity

/**
 * Created by hedgehog on 01.09.17.
 */

class BootDeviceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            val alarmIntent = Intent(context, AlarmReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (getAlarmDateSharedPreferences(context) != 0L) {
                manager.set(AlarmManager.RTC_WAKEUP, getAlarmDateSharedPreferences(context), pendingIntent)
            }
        }
    }

    private fun getAlarmDateSharedPreferences(context: Context): Long {
        return context.getSharedPreferences(context.getString(R.string.my_shared_preferences), AppCompatActivity.MODE_PRIVATE).getLong(context.getString(R.string.shared_preferences_alarm_time), 0)
    }

}