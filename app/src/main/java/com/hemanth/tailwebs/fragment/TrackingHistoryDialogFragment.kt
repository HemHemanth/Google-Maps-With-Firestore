package com.hemanth.tailwebs.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.hemanth.tailwebs.R
import com.hemanth.tailwebs.adapter.TrackingHistoryAdapter
import com.hemanth.tailwebs.interfaces.ITrackingHistoryAdapter
import com.hemanth.tailwebs.interfaces.ITrackingHistoryDialogFragment
import com.hemanth.tailwebs.model.LatLng
import com.hemanth.tailwebs.model.Markers
import kotlinx.android.synthetic.main.fragment_tracking_history_dialog.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TrackingHistoryDialogFragment : DialogFragment(), ITrackingHistoryAdapter {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var fireStoreRef: CollectionReference

    private lateinit var iTrackingHistoryDialogFragment: ITrackingHistoryDialogFragment

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
        fireStore = FirebaseFirestore.getInstance()
        fireStoreRef = fireStore.collection("markerPoints")
        return inflater.inflate(R.layout.fragment_tracking_history_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress.visibility = View.VISIBLE
        fireStoreRef.orderBy("date", Query.Direction.ASCENDING).get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
            override fun onComplete(task: Task<QuerySnapshot>) {
                if (task.isSuccessful) {

                    var markersList = ArrayList<Markers>()
                    for (document: DocumentSnapshot in task.result) {
                        val markers: Markers? = document.toObject(Markers::class.java)
                        if (markers != null) {
                            markersList.add(markers)
                        }
                    }

                    recyclerViewHistory.apply {
                        layoutManager = LinearLayoutManager(activity)
                        adapter = activity?.let { TrackingHistoryAdapter(it, markersList, this@TrackingHistoryDialogFragment) }
                    }
                    progress.visibility = View.GONE
                }
            }
        });

        imgClose.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onTrackingItemTapped(latLng: ArrayList<LatLng>) {
        iTrackingHistoryDialogFragment.onTrackingItemTapped(latLng)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        iTrackingHistoryDialogFragment = context as ITrackingHistoryDialogFragment
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackingHistoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}