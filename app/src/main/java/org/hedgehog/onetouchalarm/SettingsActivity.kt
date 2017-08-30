package org.hedgehog.onetouchalarm

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by hedgehog on 30.08.17.
 */

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun onResume() {
        super.onResume()
        title = getString(R.string.settings_menu_item_title)
    }

    fun setSharedPreferences(time: Long) {
        val sPref = getPreferences(Context.MODE_PRIVATE)
        val sPrefEditor = sPref.edit()
        sPrefEditor.putLong(getString(R.string.shared_preferences_time), time)
        sPrefEditor.apply()
    }



}