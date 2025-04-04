package com.i69.utils

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings


fun turnGPSOn(context: Context) {
    val provider: String =
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        )

    if (!provider.contains("gps")) { //if gps is disabled
        val poke = Intent()
        poke.setClassName(
            "com.android.settings",
            "com.android.settings.widget.SettingsAppWidgetProvider"
        )
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
        poke.setData(Uri.parse("3"))
        context.sendBroadcast(poke)
    }

}

//fun isGPSEnabled(context: Context): Boolean {
//    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//}

fun promptEnableGPS(context: Context) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    context.startActivity(intent)
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}