package com.example.fitnessapp.data

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.example.fitnessapp.R
import com.example.fitnessapp.repository.LocationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var locationRepository: LocationRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(
            locationSwitchStateReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
            ACTION_PAUSE -> pause()
            ACTION_STARTAFTERPAUSE -> startAfterPause()
        }
        Log.d("onStop", "isCalled")
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun start() {
        createNotification()
        locationRepository.start()
        collectLocationAndUpdateNotification()
        startForeground(1, createNotification())
    }

    private fun startAfterPause() {
        locationRepository.startAfterPause()
        collectLocationAndUpdateNotification()
        startForeground(1, createNotification())
    }

    private fun collectLocationAndUpdateNotification() {
        serviceScope.launch {
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            locationRepository.latLngFlow
                .collectLatest { location ->
                    val lat = location.latitude.toString()
                    val long = location.longitude.toString()
                    val updatedNotification = createNotification("Location: ($lat, $long)")
                    notificationManager.notify(1, updatedNotification)
                }
        }
    }

    private fun stop() {
        Log.d("service", "stop")
        serviceScope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        locationRepository.stop()
        stopSelf()
    }

    private fun pause() {
        Log.d("service", "pause")
        serviceScope.cancel()
        locationRepository.pause()
        stopSelf()
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val updatedNotification = createNotification("Activity is paused")
        notificationManager.notify(1, updatedNotification)
    }

    private fun createNotification(contentText: String = "Location: Waiting for the first location"): Notification {
        return NotificationCompat.Builder(this@LocationService, "location")
            .setContentTitle("Tracking location...")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.background_image)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        Log.d("latlongFlow", "onDestroy is run")
        super.onDestroy()
        unregisterReceiver(locationSwitchStateReceiver)
        serviceScope.cancel()
    }

    private val locationSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION == intent.action) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (!isGpsEnabled) {
                    val updatedNotification =
                        createNotification(context.getString(R.string.disabled_gps_notification_text))
                    notificationManager.notify(1, updatedNotification)
                }
            }
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STARTAFTERPAUSE = "ACTION_START_AFTER_PAUSE"
    }
}
