package com.appilary.radar.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.appilary.radar.R
import com.appilary.radar.activities.HomeActivity
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.toDisplay
import com.appilary.radar.utils.toText
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent


class ForegroundOnlyLocationService : Service() {


    private var configurationChange = false
    private var serviceRunningInForeground = false
    private val localBinder = LocalBinder()
    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val intervalInMilliSeconds = 10000
    private val fastestIntervalInMilliSeconds = 10000
    private var appPreference: AppPreference = AppPreference.instance
    private var currentLocation: Location? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate()")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = intervalInMilliSeconds.toLong()
            fastestInterval = fastestIntervalInMilliSeconds.toLong()
            smallestDisplacement = 20F
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (checkEndTime()) {
                    currentLocation = locationResult.lastLocation
                    fn_update(location = currentLocation!!)

                } else {
                    Log.d(TAG, "END TIME unsubscribeToLocationUpdates()")
                    unsubscribeToLocationUpdates()
                }
            }
        }
    }

    var locationsList: MutableList<Location> = mutableListOf()
    var dis = 0.0F
    private fun fn_update(location: Location) {

        if (locationsList.isNotEmpty()) {
            val loc1 = locationsList[locationsList.size - 1]
            val newdist = location.distanceTo(loc1) / 1000
            dis += newdist
//            appPreference.totalDistance = dis
            Log.e(
                "totalDistance--",
                "" + location.latitude.toString() + "//Long--" + location.longitude.toString() + "//Dis--" + (dis) + "km"
            )
            sendBrodcast(location, dis = dis.toDouble())

        } else {
            Log.e(
                "totalDistance!!--",
                "" + location.latitude.toString() + "//Long--" + location.longitude.toString() + "//Dis--" + (dis) + "km"
            )
            sendBrodcast(location, dis.toDouble())
        }
        if (serviceRunningInForeground) {
            notificationManager.notify(
                NOTIFICATION_ID,
                generateNotification(currentLocation, dis)
            )
        }

    }

    private fun sendBrodcast(location: Location, dis: Double) {
        locationsList.add(location)
        val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        intent.putExtra(DISTANCE, dis)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        val cancelLocationTrackingFromNotification =
            intent.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification) {
            unsubscribeToLocationUpdates()
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")
        if (!configurationChange && AppPreference.instance.isServiceIsRunnig) {
            Log.d(TAG, "Start foreground service")
            val notification = generateNotification(currentLocation, dis)
            startForeground(NOTIFICATION_ID, notification)
            serviceRunningInForeground = true
        }
        return true
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    @SuppressLint("MissingPermission")
    fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")
        locationsList.clear()
        dis = 0.0F
//        appPreference.totalDistance = 0.0F
        appPreference.isServiceIsRunnig = true
        startService(Intent(applicationContext, ForegroundOnlyLocationService::class.java))
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            appPreference.isServiceIsRunnig = false
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates() {
        locationsList.clear()
        dis = 0.0F
//        appPreference.totalDistance = 0.0F
        Log.d(TAG, "unsubscribeToLocationUpdates()")
        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed.")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to remove Location Callback.")
                }
            }
            appPreference.isServiceIsRunnig = false
        } catch (unlikely: SecurityException) {
            AppPreference.instance.isServiceIsRunnig = true
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }


    private fun generateNotification(location: Location?, dis: Float): Notification {
        Log.d(TAG, "generateNotification()")
        val distance = dis.toDisplay()
        val mainNotificationText = location?.toText() + "\n" + distance
        val titleText = getString(com.appilary.radar.R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_MIN
            )
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val bigTextStyle = NotificationCompat.BigTextStyle().bigText(mainNotificationText)
            .setBigContentTitle(titleText)
        val launchActivityIntent = Intent(this, HomeActivity::class.java)
        var activityPendingIntent: PendingIntent? = null
        activityPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 0, launchActivityIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this, 0, launchActivityIntent, 0
            )
        }
        val notificationCompatBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.drawable.app_logo)
            .setSound(null)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_MIN)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .addAction(
                R.drawable.app_logo, getString(R.string.launch_activity),
                activityPendingIntent
            )
            .build()
    }

    fun getDistance(): Float {
        return dis
    }

    inner class LocalBinder : Binder() {
        internal val service: ForegroundOnlyLocationService
            get() = this@ForegroundOnlyLocationService
    }

    fun checkEndTime(): Boolean {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val c = Calendar.getInstance()
        val date = c.time
        val formattedDate = dateFormat.format(date)
        val currentDate = dateFormat.parse(formattedDate)
        val endDate = dateFormat.parse(DEFAULT_END_TIME)
        return currentDate.before(endDate)
    }

    companion object {
        private val DEFAULT_START_TIME = "07:00"
        private val DEFAULT_END_TIME = "22:00"
        private const val TAG = "LocationService"
        private const val PACKAGE_NAME = "com.example.locationupdateservice"
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
        internal const val DISTANCE = "$PACKAGE_NAME.extra.DISTANCE"
        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"
        private const val NOTIFICATION_ID = 22091992
        private const val NOTIFICATION_CHANNEL_ID = "location_update_service"
    }
}