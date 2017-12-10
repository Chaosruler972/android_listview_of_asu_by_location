package com.example.chaosruler.home_ex2

import android.content.Context
import android.telephony.*
import android.util.Log

/**
 * Created by chaosruler on 12/5/17.
 */
class phone_recp_listener() : PhoneStateListener()
{

    companion object
    {
        public var ERROR:Int = 0
        public var asu_level:Int= ERROR
        public var telephone_manager:TelephonyManager? = null
        public var signalStrength:SignalStrength? = null
        private var ctx:Context? = null
        private var GSM_SIGNAL_STRENGTH:String? = null

        public fun init_ERROR(context: Context,telephonyManager: TelephonyManager)
        {
           ERROR = context.getString(R.string.error_code).toInt()
           asu_level = ERROR
           telephone_manager = telephonyManager
           GSM_SIGNAL_STRENGTH = context.getString(R.string.reflection_GSM_ASU)
           ctx = context
        }

        fun get_max_from_all_signal_str():Int = asu_level



        /*
        public fun isGSM():Boolean
        {
            val networkType = telephone_manager!!.networkType
            return when (networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> true // 2g, basically all 2g's are GSM or GSM based
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> false // 3g
                TelephonyManager.NETWORK_TYPE_LTE -> false // 4g
                else -> false // 5g?
            }
        }
        */






    } // companion end


    override fun onSignalStrengthsChanged(signalStrength: SignalStrength)
    {

        super.onSignalStrengthsChanged(signalStrength)
        var definations = signalStrength.toString().split(' ')
        asu_level = try
        {

            if( signalStrength.isGsm)
            {
                if(signalStrength.gsmSignalStrength != 99)
                    ERROR
                else
                    signalStrength.gsmSignalStrength
            }
            else
            {
                ERROR
            }
        }
        catch (e:Exception)
        {
            ERROR
        }
    }
}