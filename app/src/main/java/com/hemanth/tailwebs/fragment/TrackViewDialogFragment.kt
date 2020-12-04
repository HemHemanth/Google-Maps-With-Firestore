 package com.hemanth.tailwebs.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.hemanth.tailwebs.R
import kotlinx.android.synthetic.main.fragment_track_view_dialog.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TrackViewDialogFragment : DialogFragment(), OnMapReadyCallback {
    private var param1: String? = null
    private var param2: String? = null
    private var latLngList: ArrayList<LatLng> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_track_view_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment: SupportMapFragment = activity?.supportFragmentManager?.findFragmentById(R.id.mapTrack) as SupportMapFragment
        mapFragment.getMapAsync(this)

        imgClose.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        //Remove map from transaction
        val f = fragmentManager?.findFragmentById(R.id.mapTrack) as SupportMapFragment?
        if (f != null) fragmentManager!!.beginTransaction().remove(f as Fragment).commit()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.addPolyline(PolylineOptions()
                .addAll(latLngList)
        )

        googleMap?.addMarker(MarkerOptions().position(latLngList[0]))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLngList[0]))
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, latLng: ArrayList<com.hemanth.tailwebs.model.LatLng>) =
            TrackViewDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                    //Add LatLng's to iterable list
                    for (latlng: com.hemanth.tailwebs.model.LatLng in latLng) {
                        latLngList.add(LatLng(latlng.latitude, latlng.longitude))
                    }
                }
            }
    }
}