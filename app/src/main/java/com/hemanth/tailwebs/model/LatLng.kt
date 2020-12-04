package com.hemanth.tailwebs.model

data class LatLng(
    var latitude: Double,
    var longitude: Double
) {
    constructor() : this(0.0, 0.0)
}
