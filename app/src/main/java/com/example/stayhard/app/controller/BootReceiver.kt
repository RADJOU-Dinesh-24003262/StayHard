package com.example.stayhard.app.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.stayhard.app.utils.AlarmManagerHelper
import com.example.stayhard.app.utils.Constants

/**
 * ✅ Receiver pour reprogrammer les alarmes après un reboot du téléphone
 * Les alarmes AlarmManager sont perdues au redémarrage
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(Constants.LogTags.ALARM_MANAGER, "Appareil redémarré - Reprogrammation des alarmes")
            
            // ✅ Reprogrammer toutes les alarmes
            AlarmManagerHelper.rescheduleAlarms(context)
        }
    }
}