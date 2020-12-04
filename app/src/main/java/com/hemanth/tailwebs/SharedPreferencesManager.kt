package com.hemanth.tailwebs

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val LOCATION_PERMISSION_SHOW = "location_permission_show"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "TarlWebs",
            Context.MODE_PRIVATE
        )
    }

    fun setLocationPermissionShow(context: Context, isShow: Boolean) {
        val editor: SharedPreferences.Editor = getSharedPreferences(context).edit()
        editor.putBoolean(LOCATION_PERMISSION_SHOW, isShow)
        editor.commit()
    }

    fun isLocationPermissionShow(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(LOCATION_PERMISSION_SHOW, false)
    }
}