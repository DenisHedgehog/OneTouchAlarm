package org.hedgehog.onetouchalarm

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

/**
 * Created by hedgehog on 30.08.17.
 */

class SettingsActivity : AppCompatActivity() {

    private val m: Long = 1000 * 60
    private val h = m * 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val hours = findViewById<EditText>(R.id.hours_edit_text)
        val minutes = findViewById<EditText>(R.id.minutes_edit_text)
        val okButton = findViewById<Button>(R.id.ok_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        hours.setText(getHours().toString(), TextView.BufferType.EDITABLE)
        minutes.setText(getMinutes().toString(), TextView.BufferType.EDITABLE)

        okButton.setOnClickListener {
            if (hours.text.toString() != "") {
                if (minutes.text.toString() != "") {
                    if (minutes.text.toString().toInt() <= 60) {
                        setSharedPreferences(getMilliseconds(hours.text.toString().toInt(), minutes.text.toString().toInt()))
                        Toast.makeText(this, "Time was changed at ${getHours()}:${getMinutes()}", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    } else {
                        Toast.makeText(this, "Minutes can't be more than 60", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "You must set minutes at least 0", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "You must set hours at least 0", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        title = getString(R.string.settings_menu_item_title)
    }

    private fun setSharedPreferences(time: Long) {
        val sPref = getSharedPreferences(getString(R.string.my_shared_preferences), Context.MODE_PRIVATE)
        val sPrefEditor = sPref.edit()
        sPrefEditor.putLong(getString(R.string.shared_preferences_time), time)
        sPrefEditor.apply()
    }

    private fun getMilliseconds(hours: Int, minutes: Int): Long {
        return h * hours + m * minutes
    }

    private fun getSharedPreferences(): Long {
        val defaultValue: Long = 1000 * 60 * 60 * 8
        return getSharedPreferences(getString(R.string.my_shared_preferences), MODE_PRIVATE).getLong(getString(R.string.shared_preferences_time), defaultValue)
    }

    private fun getHours(): Long {
        return getSharedPreferences() / h
    }

    private fun getMinutes(): Long {
        return (getSharedPreferences() % h) / m
    }

}