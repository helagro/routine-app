package com.hlag.routine

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.woxthebox.draglistview.DragItemAdapter

import kotlinx.android.synthetic.main.activity_steps.*

class StepsActivity : AppCompatActivity() {
    lateinit var routine: Routine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)
        setSupportActionBar(toolbar)

        routine = intent.getParcelableExtra("routine")
        title = routine.name

        var adapter = ArrayAdapter<Step>(this, android.R.layout.simple_list_item_1)
        var adapter2 =  DragItemAdapter<String, DragItemAdapter.ViewHolder>(this, )
        steps_list.setAdapter(adapter2)

        fab.setOnClickListener { view ->
            addStep()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {return true}
            R.id.action_delete -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun addStep(){
        routine.steps.add(Step(0, "Step"))
    }
}
