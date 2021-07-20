package com.hlag.routine

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ViewFacory {
    companion object {
        fun setupRecycler(stepsActivity: StepsActivity, steps_list: RecyclerView) {
            val app = stepsActivity.routinePlayer

            //setup steps ui
            steps_list.layoutManager = LinearLayoutManager(stepsActivity)
            val stepAdapter = StepsAdapter(stepsActivity, stepsActivity.routine.steps)
            stepAdapter.setClickListener(stepsActivity)
            steps_list.adapter = stepAdapter

            //reorganize setup
            val itemTouchHelperCallbackList =
                object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        Collections.swap(
                            stepsActivity.routine.steps,
                            viewHolder.adapterPosition,
                            target.adapterPosition
                        )
                        stepAdapter.notifyItemMoved(
                            viewHolder.adapterPosition,
                            target.adapterPosition
                        )
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        TODO("Not yet implemented")
                    }
                }
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallbackList)
            itemTouchHelper.attachToRecyclerView(steps_list)

            val divider = DividerItemDecoration(stepsActivity, DividerItemDecoration.VERTICAL)
            steps_list.addItemDecoration(divider)

            stepAdapter.stepAdapterListener = stepsActivity
        }
    }
}