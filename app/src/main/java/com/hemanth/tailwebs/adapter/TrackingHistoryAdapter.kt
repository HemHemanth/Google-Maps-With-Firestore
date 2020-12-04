package com.hemanth.tailwebs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hemanth.tailwebs.R
import com.hemanth.tailwebs.interfaces.ITrackingHistoryAdapter
import com.hemanth.tailwebs.model.LatLng
import com.hemanth.tailwebs.model.Markers
import kotlinx.android.synthetic.main.tracking_history_item.view.*

class TrackingHistoryAdapter(
    private val context: Context,
    private val markersList: ArrayList<Markers>,
    private val iTrackingHistoryAdapter: ITrackingHistoryAdapter
): RecyclerView.Adapter<TrackingHistoryAdapter.TrackingHistoryHolder>()
    {

    class TrackingHistoryHolder(v: View): RecyclerView.ViewHolder(v) {
        val txtTrackingTime: TextView = v.txtTrackingTime
        val layoutTrackingHistory: RelativeLayout = v.layoutTrackingHistory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingHistoryHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.tracking_history_item,
            parent,
            false)

        return TrackingHistoryHolder(view)
    }

    override fun onBindViewHolder(holder: TrackingHistoryHolder, position: Int) {
        holder.txtTrackingTime.text = markersList[position].date

        holder.layoutTrackingHistory.setOnClickListener {

            //Retrieve marker points from Markers object list
            val markers = ArrayList<List<com.hemanth.tailwebs.model.LatLng>>()
            for (i in position until markersList.size) {
                markers.add(markersList[i].latLng)
            }

            val marker = ArrayList<LatLng>()
            for (mark: List<LatLng> in markers) {
                for (j in 0 until mark.size) {
                    marker.add(mark[j])
                }
            }
            iTrackingHistoryAdapter.onTrackingItemTapped(marker)
        }
    }

    override fun getItemCount(): Int = markersList.size
}