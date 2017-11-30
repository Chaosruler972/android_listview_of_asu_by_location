package com.example.chaosruler.home_ex2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import java.util.*
import android.content.Intent
import android.net.Uri


class listview_adapter(context: Context?, resource: Int=R.layout.listview_item,private var objects: MutableList<klita_obj>, private var db:reception_database_helper) : ArrayAdapter<klita_obj>(context, resource, objects)
{

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
//        super.getView(position, convertView, parent)


        var convertView = convertView
        // Get the data item for this position
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false)
        }
        // Lookup view for data population
        val asu = get_view(convertView!!,R.id.list_item_ASU) as TextView
        val longtitude = get_view(convertView!!,R.id.list_item_longtitude) as TextView
        var latitude = get_view(convertView!!,R.id.list_item_latitude) as TextView
        var date = get_view(convertView!!,R.id.list_item_date) as TextView
        var delete_btn = get_view(convertView!!,R.id.list_item_delete) as Button


        draw_view(convertView!!,objects[position].get_asu())

        var recep = objects[position]
        // Populate the data into the template view using the data object
        input_text_to_view(asu,context.getString(R.string.list_asu),recep.get_asu().toString())
        input_text_to_view(longtitude,context.getString(R.string.list_long),recep.get_long().toString())
        input_text_to_view(latitude,context.getString(R.string.list_lat),recep.get_lat().toString())
        input_text_to_view(date,"",recep.get_date_str(context))


        delete_btn.setOnClickListener({

            var found_reception = db.find_reception_by_id(recep.getid()) // checks if db contains reception still
            if(found_reception!=null) // if it contains it, remove it
                db.remove_reception(found_reception)
            var updated_post = super.getPosition(recep) // get the updated position of the reception
            if(updated_post>=0)
            {
                super.remove(recep) // call for remove from adapter
            }

        })

        /*
            easter egg -> press the cordinates textview to open google maps location!
         */
        val uri = String.format(Locale.ENGLISH, "geo:%f,%f", recep.get_lat(), recep.get_long().toFloat().toFloat())
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        latitude.setOnClickListener({
            context.startActivity(intent)
        })

        longtitude.setOnClickListener({
            context.startActivity(intent)
        })


        // Return the completed view to render on screen
        return convertView
    }

    private fun draw_view(convertView: View,asu:Int)
    {
        // change color based on ASU value
        when(asu)
        {
            in id_to_int(R.string.asu_min1)..id_to_int(R.string.asu_mxa1)->
                convertView.setBackgroundColor(context.getColor(R.color.red))
            in id_to_int(R.string.asu_min2)..id_to_int(R.string.asu_max2)->
                convertView.setBackgroundColor(context.getColor(R.color.yellow))
            in id_to_int(R.string.asu_min3)..id_to_int(R.string.asu_max3)->
                convertView.setBackgroundColor(context.getColor(R.color.green))
        }
    }

    private fun id_to_int(id:Int):Int = context.getString(id).toInt() // makes a number from strings.xml converted to int


    private fun get_view(convertView: View,id:Int) : View = convertView.findViewById(id) // grabs the correpsonding view by id from layout

    private fun input_text_to_view(txtview:TextView,prefix:String,value:String) // .settext
    {
        txtview.text = prefix+" "+value
    }
}
