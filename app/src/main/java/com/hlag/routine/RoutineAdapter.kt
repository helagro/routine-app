package com.hlag.routine

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.util.Log

class RoutineAdapter(context: Context, routines: ArrayList<Routine>) : ArrayAdapter<Routine>(context, R.layout.routine_picker_grid_item, routines) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        return super.getView(position, convertView, parent)
    }


}