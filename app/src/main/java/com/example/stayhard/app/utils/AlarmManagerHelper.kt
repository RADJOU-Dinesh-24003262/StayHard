package com.example.stayhard.app.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.stayhard.app.controller.WallpaperUpdateReceiver
import java.util.*

object AlarmManagerHelper {

    private const val PREFS_NAME = "stayhard_prefs"
    private const val KEY_ALARMS_SCHEDULED = "alarms_scheduled"

    // Horaires stratégiques: matin, midi, après-midi, soir
    private val WALLPAPER_UPDATE_HOURS = listOf(6, 10, 12, 15, 18, 21)

    private const val BASE_REQUEST_CODE = 1000

    /**
     * Configure les alarmes pour le changement automatique de fond d'écran
     * Seulement 6 alarmes au lieu de 15
     */
    fun setupWallpaperAlarms(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Vérifier si alarmes déjà programmées
        if (prefs.getBoolean(KEY_ALARMS_SCHEDULED, false)) {
            Log.d(Constants.LogTags.ALARM_MANAGER, "Alarmes déjà programmées, skip")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Annuler toutes les anciennes alarmes au cas où
        cancelAllAlarms(context)

        // Programmer les nouvelles alarmes
        WALLPAPER_UPDATE_HOURS.forEachIndexed { index, hour ->
            scheduleAlarmForHour(context, alarmManager, hour, index)
        }

        // Marquer comme programmées
        prefs.edit().putBoolean(KEY_ALARMS_SCHEDULED, true).apply()

        Log.d(Constants.LogTags.ALARM_MANAGER, "${WALLPAPER_UPDATE_HOURS.size} alarmes programmées avec succès")
    }

    /**
     * Programme une alarme pour une heure donnée
     */
    private fun scheduleAlarmForHour(
        context: Context,
        alarmManager: AlarmManager,
        hour: Int,
        index: Int
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si l'heure est déjà passée aujourd'hui, programmer pour demain
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(context, WallpaperUpdateReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            BASE_REQUEST_CODE + index, // ID unique pour chaque alarme
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Alarme exacte qui fonctionne même en Doze Mode
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            Log.d(Constants.LogTags.ALARM_MANAGER, "Alarme programmée pour ${hour}h00 (Request code: ${BASE_REQUEST_CODE + index})")
        } catch (e: SecurityException) {
            Log.e(Constants.LogTags.ALARM_MANAGER, "Permission SCHEDULE_EXACT_ALARM refusée: ${e.message}", e)
        }
    }

    /**
     * Annule toutes les alarmes programmées
     */
    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Annuler TOUTES les alarmes possibles (anciennes + nouvelles)
        val allPossibleHours = listOf(3, 6, 7, 8, 9, 10, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22)

        allPossibleHours.forEachIndexed { index, _ ->
            val intent = Intent(context, WallpaperUpdateReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BASE_REQUEST_CODE + index,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )

            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }

        // Reset le flag
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ALARMS_SCHEDULED, false).apply()

        Log.d(Constants.LogTags.ALARM_MANAGER, "Toutes les alarmes annulées")
    }

    /**
     * Reprogramme toutes les alarmes (utile après reboot)
     */
    fun rescheduleAlarms(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ALARMS_SCHEDULED, false).apply()
        setupWallpaperAlarms(context)
    }
}