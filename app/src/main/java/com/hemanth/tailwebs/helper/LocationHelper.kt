package com.hemanth.tailwebs.helper

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.hemanth.tailwebs.interfaces.IListener


class LocationHelper: LocationListener {
    private var iListener: IListener? = null
    @SuppressWarnings("missingPermission")
    fun getLocation(context: Context, iListener: IListener) {
        this.iListener = iListener

        //Initialize LocationManager
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val criteria = Criteria()
        var provider = locationManager.getBestProvider(criteria, false)
        if (provider != null) {
            //Register GPS for Location Updates
            locationManager.requestLocationUpdates(provider, 0 , 0F, this)
        }
        val providers: List<String> = locationManager.getProviders(true)

        var location: Location? = null
        for (provider in providers) {
            val l: Location = locationManager.getLastKnownLocation(provider) ?: continue
            if (location == null || l.accuracy < location.getAccuracy()) {
                //Get Accurate location
                location = l
            }
        }
        if (location != null) {
            iListener?.onLocationFound(location)
        }
    }

    override fun onLocationChanged(location: Location) {
        iListener?.onLocationFound(location)
    }
}