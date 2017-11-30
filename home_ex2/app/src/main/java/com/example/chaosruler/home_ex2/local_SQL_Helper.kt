package com.example.chaosruler.home_ex2


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*
import kotlin.collections.HashMap

abstract class local_SQL_Helper(context: Context, protected var DATABASE_NAME: String, factory: SQLiteDatabase.CursorFactory?, version: Int, protected var TABLE_NAME:String) : SQLiteOpenHelper(context, DATABASE_NAME, factory, version)
{

    /*
        Basic idea is to initate the vector with all the variables with a call to init_vector
        before doing SQL functions and do call onCreate with the variable types per key
     */
    protected lateinit var vector_of_variables:Vector<String>

    /*
        must call - inits vector that we work on later!
     */
    protected fun init_vector_of_variables(vector:Vector<String>)
    {
        vector_of_variables = vector
    }

    abstract override fun onCreate(db: SQLiteDatabase)

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            dropDB(db)
            onCreate(db)
        }
    }


    protected fun dropDB(db: SQLiteDatabase?) // subroutine to delete the entire database, including the file
    {
        if (db == null)
            return
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME)
    }

    public fun clearDB()
    {
        this.writableDatabase.execSQL("delete from " + TABLE_NAME)
    }

    /*
        subroutine to template a create table statement
            takes parameters neccesery
     */
    protected fun createDB(db: SQLiteDatabase ,variables: HashMap<String,String>) {

        var create_statement:String = "create table $TABLE_NAME ("
        for(element in vector_of_variables)
        {
            create_statement += element + " " + variables[element]
            if(vector_of_variables.lastElement() != element)
                create_statement+=" ,"
        }


        create_statement += ")"

        db.execSQL(create_statement)

    }

    /*
        subroutine gets entire database to vector of hashmap values, self colum feeder
     */
    protected fun get_db():Vector<HashMap<String,String>>
    {
        var db:SQLiteDatabase = this.readableDatabase
        var vector:Vector<HashMap<String,String>> = Vector()

        var syncToken:Object = Object()
        // to not hang the ui
        Thread({
            val c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
            try
            {
                c.moveToFirst()
            }
            catch (e: Exception)
            {
                synchronized(syncToken)
                {
                    syncToken.notify()
                }

            }
            while (!c.isAfterLast)
            {
                var small_map: HashMap<String, String> = HashMap()
                for (variable in vector_of_variables) {
                    small_map[variable] = c.getString(c.getColumnIndex(variable))
                }
                vector.addElement(small_map)
                c.moveToNext()
            }
            synchronized(syncToken)
            {
                syncToken.notify()
            }
        }).start()


        synchronized(syncToken)
        {
            try
            {
                syncToken.wait()

            } catch (e: InterruptedException) { }
        }
        db.close()
        return vector
    }
    /*
        subroutine that templates an add query
     */
    protected fun add_data(variables: Vector<HashMap<String,String>>):Boolean
    {
        var db:SQLiteDatabase = this.writableDatabase
        val return_value = variables.any { add_single_data(db, it) }
        db.close()
        return return_value
    }
    protected fun add_single_data(db:SQLiteDatabase,items:HashMap<String,String>):Boolean
    {
        val values = ContentValues()
        for(item in items)
            values.put(item.key,item.value)
        if(db.insertOrThrow(TABLE_NAME, null, values)>0)
            return true
        return false
    }

    /*
        subroutine that templates remove query
     */
    protected fun remove_from_db( where_clause:String,  equal_to: Array<String>) :Boolean
    {
        var result:Boolean = false
        var db:SQLiteDatabase = this.writableDatabase
        if(db.delete(TABLE_NAME,where_clause + "=?", equal_to) >0)
            result = true
        db.close()
        return result
    }
    /*
     subroutine that templates remove query with two or more where clauses
  */
    protected fun remove_from_db( where_clause:Array<String>,  equal_to: Array<String>) :Boolean
    {
        var result:Boolean = false
        var db:SQLiteDatabase = this.writableDatabase
        var where_clause_arguemnt = ""
        for(item in where_clause)
        {
            where_clause_arguemnt += item + " =?"
            if(item != where_clause.last())
                where_clause_arguemnt += " AND "
        }
        if(db.delete(TABLE_NAME,where_clause_arguemnt, equal_to) >0)
            result = true
        db.close()
        return result
    }



    /*
        subroutine that templates update query
     */
    protected fun update_data(where_clause: String, equal_to: Array<String>, update_to: HashMap<String,String>):Boolean
    {
        var result:Boolean = false
        var db:SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        for(item in update_to)
            values.put(item.key,item.value)
        if(db.update(TABLE_NAME,values,where_clause + "=?", equal_to)>0)
            result = true
        db.close()
        return result
    }

    /*
        subroutine to look for and get a row from the database that accepts certain conditions (ALL)
     */
    fun get_rows(map:HashMap<String,String>):Vector<HashMap<String,String>>
    {
        var db:SQLiteDatabase = this.readableDatabase
        var vector:Vector<HashMap<String,String>> = Vector()
        var sync_token:Object = Object()
        //to not hang the ui
        Thread({
            var sql_query: String = "SELECT * FROM $TABLE_NAME WHERE"
            var breaker: Int = 0
            for (item in map)
            {
                sql_query += " ${item.key} = ${item.value} "
                breaker++
                if (breaker < map.size)
                    sql_query += " AND"
                else
                    break
            }
            val c = db.rawQuery(sql_query, null)
            try
            {
                c.moveToFirst()
            } catch (e: Exception)
            {
                synchronized(sync_token)
                {
                    sync_token.notify()
                }
            }
            while (!c.isAfterLast)
            {
                var small_map: HashMap<String, String> = HashMap()
                for (variable in vector_of_variables) {
                    small_map[variable] = c.getString(c.getColumnIndex(variable))
                }
                vector.addElement(small_map)
                c.moveToNext()
            }
            synchronized(sync_token)
            {
                sync_token.notify()
            }
        }).start()

        synchronized(sync_token)
        {
            try
            {
                sync_token.wait()
            }
            catch (e:InterruptedException){}
        }
        db.close()
        return vector

    }



    /*
       subroutine gets entire database to vector of hashmap values, self colum feeder
       includes sorting
    */
    protected fun get_db(sort_by_value:String,isAscending:Boolean):Vector<HashMap<String,String>>
    {
        var db:SQLiteDatabase = this.readableDatabase
        var vector:Vector<HashMap<String,String>> = Vector()

        var syncToken:Object = Object()
        // to not hang the ui
        Thread({
            var ascending_descending_str:String
            if(isAscending)
            {
                ascending_descending_str = " ASC"
            }
            else
            {
                ascending_descending_str = " DESC"
            }
            val c = db.query(TABLE_NAME,null,null,null,null,null,sort_by_value+ascending_descending_str)
            try
            {
                c.moveToFirst()
            }
            catch (e: Exception)
            {
                synchronized(syncToken)
                {
                    syncToken.notify()
                }
            }
            while (!c.isAfterLast)
            {
                var small_map: HashMap<String, String> = HashMap()
                for (variable in vector_of_variables) {
                    small_map[variable] = c.getString(c.getColumnIndex(variable))
                }
                vector.addElement(small_map)
                c.moveToNext()
            }
            synchronized(syncToken)
            {
                syncToken.notify()
            }
        }).start()


        synchronized(syncToken)
        {
            try
            {
                syncToken.wait()

            } catch (e: InterruptedException) { }
        }
        db.close()
        return vector
    }

}