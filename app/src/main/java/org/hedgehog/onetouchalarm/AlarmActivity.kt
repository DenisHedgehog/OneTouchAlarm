package org.hedgehog.onetouchalarm

import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Button

/**
 * Created by hedgehog on 30.08.17.
 */
class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val stopButton = findViewById<Button>(R.id.stop_button)

        val ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        ringtone.play()

        stopButton.setOnClickListener {
            ringtone.stop()
            getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
                    .edit()
                    .putBoolean(getString(R.string.shared_preferences_alarm_active), false)
                    .apply()
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    }

}