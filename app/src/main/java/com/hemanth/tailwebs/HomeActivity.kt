package com.hemanth.tailwebs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hemanth.tailwebs.fragment.TrackViewDialogFragment
import com.hemanth.tailwebs.fragment.TrackingHistoryDialogFragment
import com.hemanth.tailwebs.interfaces.ITrackingHistoryDialogFragment
import com.hemanth.tailwebs.model.LatLng
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), ITrackingHistoryDialogFragment {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txtYes.setOnClickListener {
            val intent = Intent(this, TrackingActivity::class.java)
            startActivity(intent)
        }

        txtTrackingHistory.setOnClickListener {
            TrackingHistoryDialogFragment.newInstance("", "").show(supportFragmentManager, "")
        }
    }

    override fun onTrackingItemTapped(latLng: ArrayList<LatLng>) {
        TrackViewDialogFragment.newInstance("", "", latLng).show(supportFragmentManager, "")
    }
}