package org.hedgehog.onetouchalarm

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var timePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        startButton = findViewById(R.id.start_button)
        timePicker = findViewById(R.id.time_picker)

        if (DateFormat.is24HourFormat(this)) {
            timePicker.setIs24HourView(true)
        } else {
            timePicker.setIs24HourView(false)
        }

        if (getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.shared_preferences_alarm_active), false)) {
            val time = Date(getSharedPreferencesTime(this))
            timePicker.currentHour = time.hours
            timePicker.currentMinute = time.minutes
        }

        startButton.setOnClickListener {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) == 0) {
                Toast.makeText(this, "WARNING: Alarm volume is null", Toast.LENGTH_SHORT).show()
            }

            val notificationIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            if (getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
                    .getBoolean(getString(R.string.shared_preferences_alarm_active), false)) {
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, getString(R.string.alarm_is_canceled), Toast.LENGTH_SHORT).show()
                startButton.text = getString(R.string.start)
                setAlarmActivityPreferences(false)
                getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE).edit().remove(getString(R.string.shared_preferences_alarm_time)).apply()
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(NOTIFICATION_ID)
            } else {
                var year = Calendar.getInstance().get(Calendar.YEAR) - 1900
                var month = Calendar.getInstance().get(Calendar.MONTH)
                var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                val hour = timePicker.currentHour
                val minute = timePicker.currentMinute

                if (hour < Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ||
                        ((hour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) &&
                                (minute < Calendar.getInstance().get(Calendar.MINUTE)))) {
                    Log.i("CHECK", "year end")
                    if (day == Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        if (month == Calendar.getInstance().getActualMaximum(Calendar.MONTH)) {
                            year++
                            month = 0
                            day = 1
                            Log.i("Month end", "year end")
                        } else {
                            month++
                            day = 1
                            Log.i("Month end", "year is not end")
                        }
                    } else {
                        day++
                    }
                }

                val alarmTime = Date(year, month, day, hour, minute).time

                if (Build.VERSION.SDK_INT >= 23) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                    setAlarmDateSharedPreferences(alarmTime)
                    setAlarmActivityPreferences(true)
                    Log.i("Alarm was set", "sdk 23+ ${getAlarmTime(this, getSharedPreferencesTime(this))}, alarmTime = ${getAlarmTime(this, alarmTime)}")
                } else {
                    if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                        setAlarmDateSharedPreferences(alarmTime)
                        setAlarmActivityPreferences(true)
                        Log.i("Alarm was set", "19 <= sdk < 23 ${getAlarmTime(this, getSharedPreferencesTime(this))}, alarmTime = ${getAlarmTime(this, alarmTime)}")
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                        setAlarmDateSharedPreferences(alarmTime)
                        setAlarmActivityPreferences(true)
                        Log.i("Alarm was set", "sdk < 19 ${getAlarmTime(this, getSharedPreferencesTime(this))}, alarmTime = ${getAlarmTime(this, alarmTime)}")
                    }
                }
                if (getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
                        .getBoolean(getString(R.string.shared_preferences_alarm_active), false)) {
                    startButton.text = getString(R.string.stop_button_text)

                    // TODO: 12/24 time description in toast

                    Toast.makeText(this, "${getString(R.string.alarm_will_call_at)} ${getAlarmTime(this, alarmTime)}", Toast.LENGTH_SHORT).show()
                } else {
                    startButton.text = getString(R.string.start)
                }
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notificationAboutAlarm(this))
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.shared_preferences_alarm_active), false)) {
            startButton.text = getString(R.string.stop_button_text)
        } else {
            startButton.text = getString(R.string.start)
        }
    }

    private fun setAlarmActivityPreferences(boolean: Boolean) {
        val sPref = getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
        val sPrefEditor = sPref.edit()
        sPrefEditor.putBoolean(getString(R.string.shared_preferences_alarm_active), boolean)
        sPrefEditor.apply()
    }

    private fun setAlarmDateSharedPreferences(time: Long) {
        val sPref = getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
        val sPrefEditor = sPref.edit()
        sPrefEditor.putLong(getString(R.string.shared_preferences_alarm_time), time)
        sPrefEditor.apply()
    }

    companion object {
        val NOTIFICATION_ID: Int = 42

        fun notificationAboutAlarm(context: Context): Notification {
            return Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("${context.getString(R.string.alarm_notification)} ${getAlarmTime(context, getSharedPreferencesTime(context))}")
                    .setOngoing(true)
                    .build()
        }

        fun getAlarmTime(context: Context, time: Long): String {
            var simpleDateFormat: SimpleDateFormat
            if (DateFormat.is24HourFormat(context)) {
                simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
            } else {
                simpleDateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm aa")
            }
            val date = Date(time)
            return simpleDateFormat.format(date)
        }

        fun getSharedPreferencesTime(context: Context): Long {
            return context.getSharedPreferences(context.getString(R.string.my_shared_preferences), MODE_PRIVATE).getLong(context.getString(R.string.shared_preferences_alarm_time), Calendar.getInstance().timeInMillis)
        }
    }

}
