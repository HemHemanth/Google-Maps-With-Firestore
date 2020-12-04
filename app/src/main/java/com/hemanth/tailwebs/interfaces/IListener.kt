package com.hemanth.tailwebs.interfaces

import android.location.Location

interface IListener {
    fun onLocationFound(location: Location)
}