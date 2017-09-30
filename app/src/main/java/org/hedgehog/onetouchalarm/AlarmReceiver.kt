package org.hedgehog.onetouchalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import android.widget.Toast

/**
 * Created by hedgehog on 30.08.17.
 */

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("RECIEVER MF", "RECIEVER WAS STARTED")
        context.startActivity(Intent(context, AlarmActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}