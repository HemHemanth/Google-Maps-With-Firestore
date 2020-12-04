package com.hemanth.tailwebs.interfaces

import com.hemanth.tailwebs.model.LatLng

interface ITrackingHistoryDialogFragment {
    fun onTrackingItemTapped(latLng: ArrayList<LatLng>)
}