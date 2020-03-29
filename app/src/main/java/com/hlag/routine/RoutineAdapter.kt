package com.hlag.routine

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.util.Log

class RoutineAdapter(context: Context, routines: ArrayList<Routine>) : ArrayAdapter<Routine>(context, android.R.layout.simple_list_item_1, routines) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        return super.getView(position, convertView, parent)
    }


}