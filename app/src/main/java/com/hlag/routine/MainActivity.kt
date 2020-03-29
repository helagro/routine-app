package com.hlag.routine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.GridView

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var routines: ArrayList<Routine> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        routines_gridview.adapter = RoutineAdapter(this, routines)
        routines_gridview.setOnItemClickListener {parent, view, position, id ->
           //RoutineActivity.launch(this, routines.get(position))
            startActivity(Intent(this, StepsActivity::class.java).putExtra("routine", routines.get(0)))
        }

        fab.setOnClickListener { view ->
            addRoutine()
            (routines_gridview.adapter as RoutineAdapter).notifyDataSetChanged()
        }
        loadFromStorage()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun loadFromStorage(){
        addRoutine()
        (routines_gridview.adapter as RoutineAdapter).notifyDataSetChanged()
    }

    fun addRoutine(){
        routines.add(Routine(1, arrayListOf(Step(10, "Step"), Step(11, "Step1"), Step(12, "Step2")), "Test"))
    }
}
