package org.hedgehog.onetouchalarm

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Button
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context

/**
 * Created by hedgehog on 30.08.17.
 */
class AlarmActivity : AppCompatActivity() {

    private lateinit var ringtone: Ringtone
    private var kappa = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        val stopButton = findViewById<Button>(R.id.stop_button)
        stopButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        ringtone.play()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
        getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE)
                .edit()
                .putBoolean(getString(R.string.shared_preferences_alarm_active), false)
                .apply()
        getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE).edit().remove(getString(R.string.shared_preferences_alarm_time)).apply()
        ringtone.stop()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(MainActivity.NOTIFICATION_ID)
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

}