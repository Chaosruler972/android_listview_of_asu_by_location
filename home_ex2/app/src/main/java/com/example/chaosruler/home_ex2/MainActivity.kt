package com.example.chaosruler.home_ex2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.location.Criteria
import android.telephony.TelephonyManager
import android.telephony.PhoneStateListener
import android.widget.ArrayAdapter
import kotlin.collections.ArrayList


class MainActivity : Activity()
{

    /*
    request codes
     */
    private var MY_PERMISSIONS_REQUEST_LOCATION_ABSOLUT:Int =0
    private var MY_PERMISSIONS_REQUEST_LOCATION:Int = 0
    private var PHONE_STATE_PERMISSION_CODE:Int = 0
    /*
        GPS frequency variables
     */
    private  var MIN_TIME_FOR_UPDATE: Long =0.toLong()
    private  var MIN_DIS_FOR_UPFATE: Float =0.toFloat()

    /*
        behavior flag
     */
    private var premission_flag = true


    private var locationManager: LocationManager? = null

    private var telephone_manager:TelephonyManager? = null


    private var location_listener:location_list? = null



    private var reception_db:reception_database_helper? = null
    private var phone_state_listen:asu_listener? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init_codes()
        request_premission_subroutine(Manifest.permission.ACCESS_FINE_LOCATION,MY_PERMISSIONS_REQUEST_LOCATION)
    }

    private fun initdb()
    {
        reception_db = reception_database_helper(baseContext)
    }

    private fun after_premission_check()
    {
        if(!premission_flag)
        {
            mark_undoable()
            return
        }
        init_buttons()
        init_telephony_manager()
        initdb()
        init_location_manager()
        init_listview()
    }

    private fun init_listview()
    {
        main_list_view.adapter = listview_adapter(this,R.layout.listview_item,reception_db!!.get_entire_db(),reception_db!!)
    }


    private fun mark_undoable()
    {
        /*
        disables all inputs
         */
        main_btn_sort_date.visibility = View.GONE
        main_btn_sort_date.isEnabled = false
        main_btn_sort_asu.visibility = View.GONE
        main_btn_sort_asu.isEnabled = false

        /*
            creates a display of an error string on the listview
         */
        var list = ArrayList<String>()
        list.add(0,getString(R.string.unsuffcied_premission))

        main_list_view.adapter = ArrayAdapter<String>(baseContext,android.R.layout.simple_list_item_1,list )

    }

    private fun init_location_manager()
    {
        /*
        grab service
         */
        locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        try
        {
            /*
            inits a new location service with access to internal items
             */
            location_listener = location_list(baseContext,reception_db!!,phone_state_listen!!,main_list_view)
            /*
                register for update locations service
             */
            locationManager?.requestLocationUpdates(best_location(), MIN_TIME_FOR_UPDATE, MIN_DIS_FOR_UPFATE, location_listener)
        }
        catch(ex: SecurityException)
        {

        }
    }

    private fun best_location():String
    {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_MEDIUM // grab accuracy
        criteria.isAltitudeRequired = true // grab requirements
        criteria.powerRequirement = Criteria.POWER_HIGH // set requiremen'ts
        criteria.isCostAllowed = true

        return locationManager!!.getBestProvider(criteria, false)
    }

    private fun init_telephony_manager()
    {
        /*
            grabs service
         */
        telephone_manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        /*
            inits new ASU listener
         */
        phone_state_listen = asu_listener(this)

        /*
            inits listener states
         */
        telephone_manager!!.listen(phone_state_listen,
        PhoneStateListener.LISTEN_CALL_STATE
        or PhoneStateListener.LISTEN_CELL_INFO // Requires API 17

        or PhoneStateListener.LISTEN_CELL_LOCATION
        or PhoneStateListener.LISTEN_DATA_ACTIVITY
        or PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
        or PhoneStateListener.LISTEN_SERVICE_STATE
        or PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
        or PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
        or PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR)
    }


    private fun init_buttons()
    {
        main_btn_sort_asu.setOnClickListener({
            when(main_btn_sort_asu.text.toString())
            {
                getString(R.string.btn_sort_asu_asc)->
                {
                    // case we should order by ascending
                    main_list_view.adapter = listview_adapter(this,R.layout.listview_item,reception_db!!.get_entire_db(true,true),reception_db!!)
                    //change button text for new mode
                    main_btn_sort_asu.text = getString(R.string.btn_sort_asu_desc)
                }
                getString(R.string.btn_sort_asu_desc)->
                {
                    // case we should order by descending
                    main_list_view.adapter = listview_adapter(this,R.layout.listview_item,reception_db!!.get_entire_db(true,false),reception_db!!)
                    //change button text for new mode
                    main_btn_sort_asu.text = getString(R.string.btn_sort_asu_asc)
                }
            }
        })

        main_btn_sort_date.setOnClickListener({
            when(main_btn_sort_date.text.toString())
            {
                getString(R.string.btn_sort_date_asc)->
                {
                    // case we should order by ascending
                    main_list_view.adapter = listview_adapter(this,R.layout.listview_item,reception_db!!.get_entire_db(false,true),reception_db!!)
                    //change button text for new mode
                    main_btn_sort_date.text = getString(R.string.btn_sort_date_desc)
                }
                getString(R.string.btn_sort_date_desc)->
                {
                    // case we should order by descending
                    main_list_view.adapter = listview_adapter(this,R.layout.listview_item,reception_db!!.get_entire_db(false,false),reception_db!!)
                    //change button text for new mode
                    main_btn_sort_date.text = getString(R.string.btn_sort_date_asc)
                }
            }
        })
    }

    private fun init_codes()
    {
        /*
        request codes
         */
        MY_PERMISSIONS_REQUEST_LOCATION = getString(R.string.location_GPS_permission_code).toInt()
        MY_PERMISSIONS_REQUEST_LOCATION_ABSOLUT = getString(R.string.location_wifi_permission_code).toInt()
        PHONE_STATE_PERMISSION_CODE = getString(R.string.phone_state_permission_code).toInt()
        /*
            frequencey
         */
        MIN_TIME_FOR_UPDATE = getString(R.string.interval_sync_time).toLong()
        MIN_DIS_FOR_UPFATE = getString(R.string.interval_sync_dist).toFloat()

    }

    private fun request_premission_subroutine(premission:String,request_code:Int)
    {

        val premission_request = ContextCompat.checkSelfPermission(this, premission)
        if(premission_request == PackageManager.PERMISSION_GRANTED)
        {
            //next premission handle
            request_next_permission(request_code)
        }
        else
        {
            // request the permission which was not granted
            ActivityCompat.requestPermissions(this
                    , arrayOf(premission),
                    request_code)

        }

    }

    private fun request_next_permission(code:Int)
    {
        //snowball mechanism
        when(code)
        {
            MY_PERMISSIONS_REQUEST_LOCATION->
                request_premission_subroutine(Manifest.permission.READ_PHONE_STATE,PHONE_STATE_PERMISSION_CODE)
            PHONE_STATE_PERMISSION_CODE->
                request_premission_subroutine(Manifest.permission.ACCESS_COARSE_LOCATION,MY_PERMISSIONS_REQUEST_LOCATION_ABSOLUT)
            MY_PERMISSIONS_REQUEST_LOCATION_ABSOLUT->
                after_premission_check()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults!!.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // goto-next
            request_next_permission(requestCode)
        }
        else
        {
            //mark failure to grab premissions
            premission_flag=false
            after_premission_check()
        }
    }


    override fun onDestroy()
    {
        super.onDestroy()
        /*
            close services
         */
        if(locationManager!=null)
            locationManager!!.removeUpdates(location_listener)
        if(telephone_manager!=null)
            telephone_manager!!.listen(phone_state_listen,PhoneStateListener.LISTEN_NONE)
    }
}
