package com.hlag.routine

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.log

class StepsAdapter internal constructor(context: Context?, data: List<Step>) :
    RecyclerView.Adapter<StepsAdapter.ViewHolder>() {
    interface StepAdapterListener {
        fun onItemPlayPressed(step: Step)
        fun onItemChecked(step: Step?)
    }

    var stepAdapterListener: StepAdapterListener? = null

    private val mData: List<Step> = data
    private val TAG = "StepsAdapter"
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null
    private val visible = intArrayOf(
        View.VISIBLE,
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    private val invisible = intArrayOf(View.GONE, 0, 0)

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = mInflater.inflate(R.layout.step_row, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val step = mData[position]

        //set visibility
        val visibility = if (step.checked) invisible else visible
        holder.itemView.visibility = visibility[0]
        val params = holder.itemView.layoutParams
        params.width = visibility[1]
        params.height = visibility[2]
        holder.itemView.layoutParams = params


        holder.myTextView.text = step.text

        holder.checkBtn.setOnClickListener { v ->
            step.checked = true
            stepAdapterListener!!.onItemChecked(step)

            //hides checked item
            holder.itemView.visibility = visible[0]
            val params2 = holder.itemView.layoutParams
            params2.height = 0
            params2.width = 0
            holder.itemView.layoutParams = params2
        }

        holder.playBtn.setOnClickListener {
            stepAdapterListener!!.onItemPlayPressed(step)
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var playBtn: ImageButton = itemView.findViewById(R.id.step_play_row)
        var myTextView: TextView = itemView.findViewById(R.id.tvAnimalName)
        var checkBtn: ImageButton = itemView.findViewById(R.id.step_finished)
        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): Step {
        return mData[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}