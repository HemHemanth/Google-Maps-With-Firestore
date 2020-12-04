package com.hemanth.tailwebs.interfaces

import com.hemanth.tailwebs.model.LatLng

interface ITrackingHistoryAdapter {
    fun onTrackingItemTapped(latLng: ArrayList<LatLng>)
}