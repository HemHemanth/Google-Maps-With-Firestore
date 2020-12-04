package com.hemanth.tailwebs

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hemanth.tailwebs.helper.LocationHelper
import com.hemanth.tailwebs.interfaces.IListener
import com.hemanth.tailwebs.model.LatLng
import com.hemanth.tailwebs.model.Markers
import kotlinx.android.synthetic.main.activity_tracking.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback, IListener {
    private var mMap: GoogleMap? = null
    private var markers: ArrayList<LatLng> = ArrayList()
    private var markerPoints: ArrayList<com.google.android.gms.maps.model.LatLng> = ArrayList()
    private lateinit var locationHelper: LocationHelper
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var fireStoreRef: CollectionReference
    private var handler: Handler = Handler()
    val startTime = System.currentTimeMillis()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        //Initialize FireStore
        fireStore = FirebaseFirestore.getInstance()
        fireStoreRef = fireStore.collection("markerPoints")

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationHelper = LocationHelper()

        txtStopTracking.setOnClickListener {
            insertMarkerPoints()
            //Stop timer
            handler.removeCallbacks(runnable)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        handler.postDelayed(runnable, 1000)

    }

    //Run timer for every second
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - startTime
            val countDownTime = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
            txtTimer.text = countDownTime
            handler.postDelayed(this, 1000)
        }
    }

    //Verify permissions
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {


            var readPermissionAskedBefore = SharedPreferencesManager.isLocationPermissionShow(this)

            if (readPermissionAskedBefore) {
                showCustomAlert("Location Permission Needed")
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        1011
                )

            }

        } else {
            SharedPreferencesManager.setLocationPermissionShow(this, false)
            locationHelper.getLocation(this, this)
        }
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        //Get Location permission
        getLocationPermission()
    }

    override fun onLocationFound(location: Location) {
        markers.add(LatLng(location.latitude, location.longitude))
        Log.d("LatLang", "${location.latitude} : ${location.longitude}")
        addPath(com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude))
    }


    //Draw path with marker points
    private fun addPath(latLng: com.google.android.gms.maps.model.LatLng) {
        //Clear previous markers from Map
        mMap?.clear()
        markerPoints.add(latLng)
        //Add user recent location marker
        mMap?.addMarker(MarkerOptions().position(latLng))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0F))

        mMap?.addPolyline(PolylineOptions()
                .addAll(
                        markerPoints
                )
        )
    }


    //Insert locations to Cloud fireStore
    private fun insertMarkerPoints() {
        val date = Date()
        val formatter = SimpleDateFormat("HH:mm dd MMM yyyy")
        Log.d("Date", formatter.format(date))
        var markers = Markers(formatter.format(date), markers)
        fireStoreRef.add(markers)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1011 -> {
                if (grantResults.isNotEmpty()) {
                    if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        SharedPreferencesManager.setLocationPermissionShow(this, false)
                        getLocationPermission()
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                        this,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                )
                        ) {
                            SharedPreferencesManager.setLocationPermissionShow(this, true)
                        } else {
                            finish()
                        }
                    }
                }
            }
        }
    }


    //Show alert for Permission to enable from Settings
    private fun showCustomAlert(message: String) {
        var builder = AlertDialog.Builder(this)

        builder.setTitle("Permission Needed")
        builder.setMessage(message)

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
            var uri = Uri.fromParts(
                    "package",
                    this.packageName,
                    null
            )

            var intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            intent.data = uri
            startActivityForResult(intent, 1011)
        })
        builder.setNegativeButton("Cancle", DialogInterface.OnClickListener { _, _ ->

        })

        var alertDialog = builder.create()
        alertDialog.show()
    }
}