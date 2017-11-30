package com.example.chaosruler.home_ex2

import android.content.Context
import android.telephony.SignalStrength
import android.telephony.PhoneStateListener
import android.widget.Toast


public class asu_listener(private var context: Context) : PhoneStateListener()
{

    var signalStrengthDbm = INVALID
    var signalStrengthAsuLevel = INVALID

    override fun onSignalStrengthsChanged(signalStrength: SignalStrength)
    {
        /*
            updates latest values
         */
        signalStrengthDbm = getSignalStrengthByName(signalStrength, context.getString(R.string.getdbm))
        signalStrengthAsuLevel = getSignalStrengthByName(signalStrength, context.getString(R.string.getAsuLevel))

    }

    private fun getSignalStrengthByName(signalStrength: SignalStrength, methodName: String): Int
    {
        try
        {
            // metadata call for subroutine
            val classFromName = Class.forName(SignalStrength::class.java.name)
            val method = classFromName.getDeclaredMethod(methodName)
            val `object` = method.invoke(signalStrength)
            return `object` as Int
        }
        catch (ex: Exception)
        {
            return INVALID
        }

    }

    companion object
    {
        //sign error
        val INVALID = Integer.MAX_VALUE
    }
}