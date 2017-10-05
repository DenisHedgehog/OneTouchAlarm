package org.hedgehog.onetouchalarm

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import java.util.*

/**
 * Created by hedgehog on 30.08.17.
 */
class AlarmActivity : AppCompatActivity() {

    private lateinit var ringtone: Ringtone
    private var kappa = 0
    private lateinit var player: MediaPlayer
    private var stopped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        Log.i("alarm activity", "was started")
        val stopButton = findViewById<Button>(R.id.stop_button)
        stopButton.setOnClickListener {
            stopped = true
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

        player = MediaPlayer()//.create(this, R.raw.my_alarm)
        if (Build.VERSION.SDK_INT >= 21) {
            val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
            player.setAudioAttributes(attribute)
        } else {
            player.setAudioStreamType(AudioManager.STREAM_ALARM)
        }

        player.setDataSource(this, Uri.parse("android.resource://$packageName/${R.raw.my_alarm}"))

        player.prepare()
        player.start()
        player.setOnCompletionListener {
            if (!stopped) {
                finish()
            }
        }

    }

    // TODO: FIX START TIME IN TIME PICKER

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
        getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
                .edit()
                .putBoolean(getString(R.string.shared_preferences_alarm_active), false)
                .apply()

        getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE).edit().remove(getString(R.string.shared_preferences_alarm_time)).apply()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(MainActivity.NOTIFICATION_ID)
        if (!stopped) {
            count++
            Log.i("alarm activity", "alarm isn't stopped, count = $count")
            if (count < 3) {
                val notificationIntent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                val alarmTime: Long = Date().time + 1000 * 60 * 10
                if (Build.VERSION.SDK_INT >= 23) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                    setAlarmDateSharedPreferences(alarmTime)
                    setAlarmActivityPreferences(true)
                    Log.i("Alarm was set", "sdk 23+ ${MainActivity.getAlarmTime(this, MainActivity.getSharedPreferencesTime(this))}, alarmTime = ${MainActivity.getAlarmTime(this, alarmTime)}")
                } else {
                    if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                        setAlarmDateSharedPreferences(alarmTime)
                        setAlarmActivityPreferences(true)
                        Log.i("Alarm was set", "19 <= sdk < 23 ${MainActivity.getAlarmTime(this, MainActivity.getSharedPreferencesTime(this))}, alarmTime = ${MainActivity.getAlarmTime(this, alarmTime)}")
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                        setAlarmDateSharedPreferences(alarmTime)
                        setAlarmActivityPreferences(true)
                        Log.i("Alarm was set", "sdk < 19 ${MainActivity.getAlarmTime(this, MainActivity.getSharedPreferencesTime(this))}, alarmTime = ${MainActivity.getAlarmTime(this, alarmTime)}")
                    }
                }
            }
        } else {
            player.stop()
        }

    }

    override fun onStop() {
        super.onStop()
        val myKM = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (myKM.inKeyguardRestrictedInputMode()) {
            if (kappa > 0) {
                finish()
            }
            kappa++
        } else {
            finish()
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
        private var count = 0
    }

}