package com.appilary.radar.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.appilary.radar.bean.LocAddress
import com.appilary.radar.frag.DashBoardFrag
import com.appilary.radar.frag.DialogFragment
import com.appilary.radar.utils.AppUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*
import com.appilary.radar.service.ForegroundOnlyLocationService
import com.appilary.radar.utils.AppPreference

abstract class BaseActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private val TAG = "LocationUpdate"
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null
    var appPreference = AppPreference.instance

    // Google client to interact with Google API
    var mGoogleApiClient: GoogleApiClient? = null
    var mLastLocation: Location? = null
    var address: String? = null
    var isLocationSend = false
    private val PLAY_SERVICES_REQUEST = 1000
    private val REQUEST_CHECK_SETTINGS = 2001
    val mLocationRequest = LocationRequest()

    var disableBack: Boolean = false
    lateinit var mainActivity: FragmentActivity
    private val TIME_INTERVAL =
        2000 // # milliseconds, desired time passed between two back presses.
    private var mBackPressed: Long = 0
    private var locationUpdatedListener: LocationUpdated? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
//        sendLocPriodically()
    }

    override fun onBackPressed() {
        if (disableBack)
            return
        val frags = supportFragmentManager.fragments
        if (supportFragmentManager.backStackEntryCount > 1)
            super.onBackPressed()
        else if (frags.size > 0 && frags[frags.size - 1] is DialogFragment)
            super.onBackPressed()
        else if (frags != null && frags.size > 0 && frags[frags.size - 1] is DashBoardFrag) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                finish()
                return
            } else
                AppUtils.showToast("Press back again to exit")
            mBackPressed = System.currentTimeMillis()
        } else
            finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            getLocation()
        } else if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_CANCELED) {
//            FragmentOpener.instance.addDashBoardFrag(mainActivity)
        } else {
            AppUtils.showToast("Please provide location Permission")
//            for (fragment in supportFragmentManager.fragments) {
//                fragment.onActivityResult(requestCode, resultCode, data)
//            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 107) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation() //granted
            }else {
                //not granted
                AppUtils.showToast("Please provide location Permission")
//                FragmentOpener.instance.addDashBoardFrag(mainActivity)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    // For location
    fun findLocation(locationUpdatedListener: LocationUpdated?) {
        removeOldData()
        this.locationUpdatedListener = locationUpdatedListener
        if (checkPlayServices())
            buildGoogleApiClient()


//        if (isNetworkAvailable(mainActivity)) {
        if (checkLocationPermission(this)) {
            if (mLastLocation != null) {
                val latitude = mLastLocation?.latitude
                val longitude = mLastLocation?.longitude
//                    address = getAddress(latitude, longitude)
                sendLocation()
                stopLocationUpdates()
            } else {
                if (checkPlayServices())
                    buildGoogleApiClient()
                else
                    getLocation()
            }
        } else
            requestPermission()
//        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            107
        )
    }

    /**
     * Method to display the location on UI
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkLocationPermission(this)) {
            try {
                val loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient!!)
                if (loc != null) {
                    mLastLocation = loc
//                    address = getAddress(loc.latitude, loc.longitude)
                    sendLocation()
//                    stopLocationUpdates()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Creating google api client object
     */

    @Synchronized
    private fun buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()

            mGoogleApiClient?.connect()

            mLocationRequest.interval = 2000
            mLocationRequest.fastestInterval = 2000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)

            val result = LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient!!,
                builder.build()
            )

            result.setResultCallback {
                val status = it.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS ->
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation()
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                        status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        } else
            getLocation()
    }

    /**
     * Method to verify google play services on the device
     */

    private fun checkPlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability?.isGooglePlayServicesAvailable(this)
        if (resultCode != null && resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(
                    this,
                    resultCode,
                    PLAY_SERVICES_REQUEST
                )?.show()
            } else {
                AppUtils.showToast("This device is not supported.")
//                FragmentOpener.instance.addDashBoardFrag(mainActivity)
            }
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(foregroundOnlyServiceConnection)
        super.onStop()
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkLocationPermission(this)) {
            // The final argument to {@code requestLocationUpdates()} is a LocationListener
            // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
            if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected)
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient!!,
                    mLocationRequest,
                    this
                )
        }
    }


    private fun stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        //        showTost("Loc update stoped");
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient!!, this)

        mGoogleApiClient?.disconnect()
        mGoogleApiClient = null

    }

    private fun removeOldData() {
        mLastLocation = null
        address = null
    }


    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            mLastLocation = p0
//            address = getAddress(p0.latitude, p0.longitude)
            sendLocation()
            stopLocationUpdates()
        }
    }

    private fun getAddress(lat: Double?, lon: Double?): String {
        if (lat == null || lon == null)
            return "Nearby"
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(
                lat,
                lon,
                5
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var currentLocation: String?
        if (addresses != null && addresses.isNotEmpty()) {
            addresses.forEach {
                val state = it.adminArea
                val city = it.locality
                val area = it.subLocality
                currentLocation = if (!TextUtils.isEmpty(area)) "$area, " else ""
                if (TextUtils.isEmpty(currentLocation)) {
                    currentLocation = if (!TextUtils.isEmpty(city)) "$city, " else ""
                    currentLocation += if (!TextUtils.isEmpty(state)) state else ""
                } else
                    currentLocation += if (!TextUtils.isEmpty(city)) city else ""
                if (!TextUtils.isEmpty(currentLocation))
                    return currentLocation!!
            }
        }
        return "Nearby"
    }


    /**
     * Google api callback methods
     */

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.e("Loc Failed", "loc failed")
    }

    override fun onConnected(arg0: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(arg0: Int) {
        if (mGoogleApiClient != null)
            mGoogleApiClient!!.connect()
    }

    fun sendLocation() {
        locationUpdatedListener?.onLocCapture(
            mLastLocation?.latitude,
            mLastLocation?.longitude,
            address
        )
    }


    fun getCurrentLoc(): LocAddress {
        return LocAddress(mLastLocation?.latitude, mLastLocation?.longitude, address)
    }

//    override fun onPostResume() {
//        super.onPostResume()
//        stopLocWorker()
//    }
//
//    override fun finish() {
//        super.finish()
//        stopLocationUpdates()
//        startLocationWorker()
//    }
//
//    fun sendLocPriodically() {
//        lifecycleScope.launch {
//            val ticker = ticker(900000, 0)
//            for (event in ticker) {
//                mLastLocation?.let {
//                    ApiCallUtils.sendLocation(it.latitude, it.longitude)
//                }
//            }
//        }
//    }
//
//    private fun isWorkScheduled(workInfos: List<WorkInfo>?): Boolean {
//        var running = false
//        if (workInfos == null || workInfos.size == 0) return false
//        for (workStatus in workInfos) {
//            running =
//                (workStatus.state == WorkInfo.State.RUNNING) or (workStatus.state == WorkInfo.State.ENQUEUED)
//        }
//        return running
//    }
//
//    private fun startLocationWorker() {
//
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
////        // START Worker
//        val periodicWork =
//            PeriodicWorkRequest.Builder(LocationUpdateWorker::class.java, 15, TimeUnit.MINUTES)
//                .addTag(TAG)
//                .setConstraints(constraints)
//                .build()
//        WorkManager.getInstance(App.mInstance)
//            .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, periodicWork)
//
//    }
//
//    private fun stopLocWorker() {
//        WorkManager.getInstance().cancelAllWorkByTag(TAG)
//    }

//    override fun onUserLeaveHint() {
//        super.onUserLeaveHint()
//        if (!App.isCameraIntent)
//            startLocationWorker()
//    }

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
//            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
//            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onDestroy() {
        AppUtils.hideProgressDialog();
        stopLocationUpdates()
//        startLocationWorker()
        super.onDestroy()
    }


    fun showProgressBar() {
        AppUtils.showProgressDialog(this)
    }

    fun hideProgressBar() {
        AppUtils.hideProgressDialog()
    }

    fun startService() {
        if (!appPreference.isServiceIsRunnig) {
            if (checkLocationPermission(this)) {
                foregroundOnlyLocationService?.subscribeToLocationUpdates() ?: Log.d(
                    TAG,
                    "Service Not Bound"
                )
            } else {
                requestPermission()
            }
        }
    }

    fun getTotalDistance(): Float = foregroundOnlyLocationService?.getDistance() ?: 0.0F

    fun StopService() {
        foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
    }

}

interface LocationUpdated {
    fun onLocCapture(lat: Double?, lang: Double?, address: String?)
}

