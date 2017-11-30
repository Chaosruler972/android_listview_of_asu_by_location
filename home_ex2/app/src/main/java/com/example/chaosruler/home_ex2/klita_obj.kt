package com.example.chaosruler.home_ex2

import android.content.Context
import java.sql.Date
import java.text.SimpleDateFormat


/*
    a class representation of a recepetion table row
 */
class klita_obj(private var id:Int,private var latitude:Double,private var longtitude:Double,private var date:String,private var asu:Int)
{

    public fun getid(): Int = this.id

    public fun get_lat():Double = this.latitude

    public fun get_long():Double = this.longtitude

    public fun get_date():String = this.date

    public fun get_asu():Int = this.asu

    public fun setid(new_id:Int)
    {
        this.id = new_id
    }

    public fun set_lat(new_lat:Double)
    {
        this.latitude = new_lat
    }

    public fun set_long(new_long:Double)
    {
        this.longtitude = new_long
    }

    public fun set_date(new_date:String)
    {
        this.date = new_date
    }

    public fun set_asu(new_asu:Int)
    {
        this.asu = new_asu
    }

    /*
        prints date string
     */
    public fun get_date_str(context: Context):String = SimpleDateFormat(context.getString(R.string.date_pattern)).format(Date(date.toLong()))
    /*
        copy ctor
     */
    fun copy(other: klita_obj) = klita_obj(other.id,other.latitude,other.longtitude,other.date,other.asu)

}