package org.hedgehog.onetouchalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.start_button)

        

        startButton.setOnClickListener {
            val notificationIntent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val time = 1000 * 60
            alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + time, pendingIntent)
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

    fun getSharedPreferences() : Long {
        val defaultValue: Long = 1000 * 60 * 60 * 8
        return getPreferences(MODE_PRIVATE).getLong(getString(R.string.shared_preferences_time), defaultValue)
    }

}
