package com.hemanth.tailwebs.model

data class Markers(
    val date: String,
    val latLng: List<LatLng>
) {
    constructor(): this("", emptyList<LatLng>())
}
