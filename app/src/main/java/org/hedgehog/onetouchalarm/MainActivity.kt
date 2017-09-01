package org.hedgehog.onetouchalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById<Button>(R.id.start_button)

        startButton.setOnClickListener {
            val notificationIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.AM_PM, Calendar.PM)
            val alarmTime = calendar.time.time + getSharedPreferencesTime()
            if (getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
                    .getBoolean(getString(R.string.shared_preferences_alarm_active), true)) {
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "Alarm is canceled", Toast.LENGTH_SHORT).show()
                startButton.text = getString(R.string.start)
                setAlarmActivityPreferences(false)
                getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE).edit().remove(getString(R.string.shared_preferences_alarm_time)).apply()
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                }
                Toast.makeText(this, "Alarm will call at ${getAlarmTime(alarmTime)}", Toast.LENGTH_SHORT).show()
                startButton.text = getString(R.string.stop_button_text)
                setAlarmActivityPreferences(true)
                setAlarmDateSharedPreferences(alarmTime)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.shared_preferences_alarm_active), true)) {
            startButton.text = getString(R.string.stop_button_text)
        } else {
            startButton.text = getString(R.string.start)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val setting = menu?.findItem(R.id.settings_menu_item)

        setting?.setOnMenuItemClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }

        return super.onCreateOptionsMenu(menu)

    }

    private fun getSharedPreferencesTime(): Long {
        val defaultValue: Long = 1000 * 60 * 60 * 8
        return getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE).getLong(getString(R.string.shared_preferences_time), defaultValue)
    }

    private fun setAlarmActivityPreferences(boolean: Boolean) {
        val sPref = getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
        val sPrefEditor = sPref.edit()
        sPrefEditor.putBoolean(getString(R.string.shared_preferences_alarm_active), boolean)
        sPrefEditor.apply()
    }

    private fun getAlarmTime(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val date = Date(time)
        return simpleDateFormat.format(date)
    }

    private fun setAlarmDateSharedPreferences(time: Long) {
        val sPref = getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
        val sPrefEditor = sPref.edit()
        sPrefEditor.putLong(getString(R.string.shared_preferences_alarm_time), time)
        sPrefEditor.apply()
    }

}
