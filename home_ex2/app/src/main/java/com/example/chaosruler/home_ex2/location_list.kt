package com.example.chaosruler.home_ex2

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.widget.ListView

import java.time.Instant
import java.util.*

/**
 * Created by chaosruler on 11/30/17.
 */
class location_list(private var context: Context, private var reception_database_helper: reception_database_helper, private var asu_listener: asu_listener,private var list_view:ListView) : LocationListener
{
    private var last_known_location:Location? = null

    override fun onLocationChanged(location: Location?)
    {
        if(location!=null)
        {
            /*
                grab time of now using either mechanisms, depends on android version
             */
            var date_long:Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Instant.now().toEpochMilli()
            }
            else
            {
                Calendar.getInstance().time.time
            }

            /*
                grabs latest updated ASU value
             */
            var asu = asu_listener.signalStrengthAsuLevel

            /*
                checks if ASU level is within range
             */
            if(asu<=context.getString(R.string.max_ASU).toInt() && asu>=context.getString(R.string.min_ASU).toInt())
            {
                // creates new data
                var reception:klita_obj = klita_obj(0,location.latitude,location.longitude,date_long.toString(),asu)
                //add to db
                reception_database_helper.add_new_reception(reception)
            }

            //updated listview adapter, garbage collector will kill the previous
            list_view.adapter = listview_adapter(context,R.layout.listview_item,reception_database_helper.get_entire_db(),reception_database_helper)
            last_known_location = location
        }
    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?)
    {

    }

    override fun onProviderEnabled(provider: String?)
    {

    }

    override fun onProviderDisabled(provider: String?)
    {

    }

}