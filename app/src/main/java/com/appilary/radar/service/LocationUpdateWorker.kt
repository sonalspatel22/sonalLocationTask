package com.appilary.radar.service

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.appilary.radar.api.*
import com.appilary.radar.api.body.LocUpdateBody
import com.appilary.radar.api.response.BaseErrorResponse
import com.appilary.radar.utils.AppPreference
import com.appilary.radar.utils.AppUtils
import com.google.android.gms.location.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LocationUpdateWorker(private val mContext: Context, workerParams: WorkerParameters) : Worker(mContext, workerParams) {
    /**
     * The current location.
     */
    private var mLocation: Location? = null
    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null

    override fun doWork(): Result {
        Log.d(TAG, "doWork: Done")

        Log.d(TAG, "onStartJob: STARTING JOB..")

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val c = Calendar.getInstance()
        val date = c.time
        val formattedDate = dateFormat.format(date)
        try {
            val currentDate = dateFormat.parse(formattedDate)
            val startDate = dateFormat.parse(DEFAULT_START_TIME)
            val endDate = dateFormat.parse(DEFAULT_END_TIME)
            val isDayEnd = AppPreference.instance.isDayEndSubimitted
            val token = AppPreference.instance.authToken
            val isAttendanceNotMarked = AppUtils.isNeedToShowAttendance()
            val isUnderTimeFrame = currentDate.after(startDate) && currentDate.before(endDate)

            if (isUnderTimeFrame && !isDayEnd && token.isNotEmpty() && !isAttendanceNotMarked) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
                    }
                }

                val mLocationRequest = LocationRequest()
                mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
                mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                try {
                    mFusedLocationClient!!
                            .lastLocation
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful && task.result != null) {
                                    mLocation = task.result
                                    Log.d(TAG, "Location : " + mLocation!!)
//                                    val def = GlobalScope.async {
//                                        AppRestClientService.service.locUpdate(LocUpdateBody(mLocation!!.latitude, mLocation!!.longitude)).execute()
//                                    }.onAwait

                                    mLocation?.let {
                                        Log.e("Sonal","addOnCompleteListener")
                                        updateLocToServer(it.latitude, it.longitude)
                                    }
//                                    try {
//                                        AppRestClientService.service.locUpdate(LocUpdateBody(mLocation!!.latitude, mLocation!!.longitude)).execute()
//                                    } catch (unlikely: IOException) {
//                                        Log.e(TAG, "IO Exception.$unlikely")
//                                    }

                                    // Create the NotificationChannel, but only on API 26+ because
                                    // the NotificationChannel class is new and not in the support library
                                    //                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    //                                            CharSequence name = mContext.getString(R.string.app_name);
                                    //                                            String description = mContext.getString(R.string.app_name);
                                    //                                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    //                                            NotificationChannel channel = new NotificationChannel(mContext.getString(R.string.app_name), name, importance);
                                    //                                            channel.setDescription(description);
                                    //                                            // Register the channel with the system; you can't change the importance
                                    //                                            // or other notification behaviors after this
                                    //                                            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
                                    //                                            notificationManager.createNotificationChannel(channel);
                                    //                                        }
                                    //
                                    //                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.app_name))
                                    //                                                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                                    //                                                .setContentTitle("New Location Update")
                                    //                                                .setContentText("You are at " + getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude()))
                                    //                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    //                                                .setStyle(new NotificationCompat.BigTextStyle().bigText("You are at " + getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude())));
                                    //
                                    //                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                                    //
                                    //                                        // notificationId is a unique int for each notification that you must define
                                    //                                        notificationManager.notify(1001, builder.build());

                                    mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
                                } else {
                                    Log.w(TAG, "Failed to get location.")
                                }
                            }
                } catch (unlikely: SecurityException) {
                    Log.e(TAG, "Lost location permission.$unlikely")
                }

                try {
                    mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, null)
                } catch (unlikely: SecurityException) {
                    //Utils.setRequestingLocationUpdates(this, false);
                    Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
                }

            } else {
                Log.d(TAG, "Time up to get location. Your time is : $DEFAULT_START_TIME to $DEFAULT_END_TIME")
            }
        } catch (ignored: ParseException) {

        }

        return Result.success()
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(mContext, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder()

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return strAdd
    }

    private fun updateLocToServer(lt: Double, lg: Double) {
        Log.e("Sonal","updateLocToServer")
        val apiController = ApiControllerImpl(ApiPresenterImpl(object : ApiResponseDisplay {
            override fun <T> onResponse(event: ApiAction, t: T) {

            }

            override fun onError(event: ApiAction, error: BaseErrorResponse) {

            }

            override fun onFailure(event: ApiAction, msg: String?) {

            }
        }))

        apiController.handle(LocUpdateAction(LocUpdateBody(lt, lg)))
    }

    companion object {
        private val DEFAULT_START_TIME = "07:00"
        private val DEFAULT_END_TIME = "22:00"

        private val TAG = "LocationUpdateWorker"

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

}