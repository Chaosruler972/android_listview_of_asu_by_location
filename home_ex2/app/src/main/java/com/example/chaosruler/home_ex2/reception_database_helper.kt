package com.example.chaosruler.home_ex2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by chaosruler on 11/29/17.
 */
class reception_database_helper(private val con: Context) : local_SQL_Helper(con, con.getString(R.string.DB_FILE_NAME),null,con.getString(R.string.KLITA_DB_VERSION).toInt(),con.getString(R.string.KLITA_TABLE_NAME) )
{
    /*
        database variables, DB name and filename and db version stored in father
     */

    private val ID = con.getString(R.string.klita_ID)
    private val ASU = con.getString(R.string.klita_ASU)
    private val LATITUDE = con.getString(R.string.klita_latitude)
    private val LONGTITUDE = con.getString(R.string.klita_longtitude)
    private val DATE = con.getString(R.string.klita_date_str)

    init
    {
        /*
            inits the abstract database implentation with knowledge of database metadata
         */
        var vector: Vector<String> = Vector()
        vector.add(ID)
        vector.add(ASU)
        vector.add(LATITUDE)
        vector.add(LONGTITUDE)
        vector.add(DATE)
        init_vector_of_variables(vector)
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        /*
            database metadata typeset
         */
        var map:HashMap<String,String> = HashMap()
        map[ID] = " " + con.getString(R.string.klita_ID_type)
        map[ASU] = " " + con.getString(R.string.klita_asu_type)
        map[LATITUDE] = " " + con.getString(R.string.klita_latitude_type)
        map[LONGTITUDE] = " " + con.getString(R.string.klita_longtitude_type)
        map[DATE] = " " + con.getString(R.string.klita_date_str_type)
        createDB(db,map)
    }

    public fun add_new_reception(input: klita_obj):Boolean
    {
        if(input.get_asu() < 0) // patch for GSM only
            return false
        var check_exists = find_reception_by_lat_long(input.get_lat(),input.get_long())
        if(check_exists != null) // exists, should update
        {
            return update_reception(check_exists,input)
        }
        else // doesn't exist, should create new
        {
            return insert_new_reception(input)
        }
    }

    private fun insert_new_reception(input:klita_obj):Boolean
    {
        /*
        creates containers
         */
       var to_database_format:Vector<HashMap<String,String>> = Vector()
       var data:HashMap<String,String> = HashMap()
        /*
            inputs data
         */
       data[ASU] = input.get_asu().toString()
       data[LATITUDE] = input.get_lat().toString()
       data[LONGTITUDE] = input.get_long().toString()
       data[DATE] = input.get_date()
       to_database_format.addElement(data)

        /*
            send to SQL
         */
       return add_data(to_database_format)
    }

    private fun update_reception(old:klita_obj, updated:klita_obj):Boolean
    {
        /*
            creates container
         */
        var update_to:HashMap<String,String> = HashMap()
        update_to[ASU] = updated.get_asu().toString()
        update_to[LATITUDE] = updated.get_lat().toString()
        update_to[LONGTITUDE] = updated.get_long().toString()
        update_to[DATE] = updated.get_date()
        /*
            sends to SQL
         */
        return update_data(ID, arrayOf(old.getid().toString()),update_to)
    }

    public fun remove_reception(reception:klita_obj):Boolean=remove_from_db(ID, arrayOf(reception.getid().toString()))


    private fun find_reception_by_lat_long(latitude:Double,longtitude:Double):klita_obj?
    {
        /*
            creates container
         */
        var query_map = HashMap<String,String>()
        query_map[LATITUDE] = latitude.toString()
        query_map[LONGTITUDE] = longtitude.toString()
        /*
            sends to SQL
         */
        val query_result = get_rows(query_map)
        /*
            if results were found, return result, else return null - code for no result
         */
        if(query_result.size > 0)
        {
            return klita_obj(query_result.firstElement()!![ID]!!.toInt(),query_result.firstElement()!![LATITUDE]!!.toDouble(),query_result.firstElement()!![LONGTITUDE]!!.toDouble(),query_result.firstElement()!![DATE]!!,query_result.firstElement()!![ASU]!!.toInt())
        }
        return null
    }

    public fun find_reception_by_id(id:Int):klita_obj?
    {
        /*
            creates container
         */
        var query_map = HashMap<String,String>()
        query_map[ID] = id.toString()
        /*
            sends to SQL
         */
        val query_result = get_rows(query_map)
        /*
            if results were found, return result, else return null - code for no result
         */
        if(query_result.size > 0)
        {
            return klita_obj(query_result.firstElement()!![ID]!!.toInt(),query_result.firstElement()!![LATITUDE]!!.toDouble(),query_result.firstElement()!![LONGTITUDE]!!.toDouble(),query_result.firstElement()!![DATE]!!,query_result.firstElement()!![ASU]!!.toInt())
        }
        return null
    }

    public fun get_entire_db():Vector<klita_obj>
    {
        /*
            container for data, return object...
         */
        var receptions:Vector<klita_obj> = Vector()
        /*
            grabs result from select * from receptions in Vector<HashMap<String,String>> format
         */
        var select_wildcard_from_receptions = get_db()
        select_wildcard_from_receptions.mapTo(// adds each result
                receptions) { klita_obj( it[ID]!!.toInt(), it[LATITUDE]!!.toDouble(), it[LONGTITUDE]!!.toDouble(), it[DATE]!!.toString(), it[ASU]!!.toInt() ) }
        // returns results, receptions.size == 0 is code for empty table
        return receptions
    }





    /*
        only with sortage
     */
    public fun get_entire_db(isAsu:Boolean,isAscending:Boolean):Vector<klita_obj>
    {
        /*
            container for data, return object...
         */
        var receptions:Vector<klita_obj> = Vector()
        /*
            grabs result from select * from receptions in Vector<HashMap<String,String>> format
         */
        var select_wildcard_from_receptions:Vector<HashMap<String,String>> = if(isAsu)
        {
            get_db(ASU,isAscending)
        }
        else
        {
            get_db(DATE,isAscending)
        }
        select_wildcard_from_receptions.mapTo(// adds each result
                receptions) { klita_obj( it[ID]!!.toInt(), it[LATITUDE]!!.toDouble(), it[LONGTITUDE]!!.toDouble(), it[DATE]!!.toString(), it[ASU]!!.toInt() ) }
        // returns results, receptions.size == 0 is code for empty table
        return receptions
    }

}