package com.hlag.routine

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.GridView
import androidx.core.app.ActivityCompat

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    var routines: ArrayList<Routine> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Permissions
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0);
            return
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        routines_gridview.adapter = RoutineAdapter(this, routines)
        routines_gridview.setOnItemClickListener {parent, view, position, id ->
            startActivity(Intent(this, StepsActivity::class.java).putExtra("routine_name", routines.get(position).name))
        }

        fab.setOnClickListener { view ->
            val dialog = TextDialog(this)
            dialog.setOnDismissListener {
                val text = dialog.editText.text.toString()
                val routine = Routine(text, ArrayList<Step>())

                MyApp.writeRoutine(this, routine)

                routines.add(routine)
                (routines_gridview.adapter as RoutineAdapter).notifyDataSetChanged()
            }

        }
        loadFromStorage()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun loadFromStorage(){
        val dir = File(MyApp.dir)
        for(file in dir.listFiles()){
            routines.add(Routine(file.nameWithoutExtension, ArrayList<Step>()))
        }
        (routines_gridview.adapter as RoutineAdapter).notifyDataSetChanged()
    }
}
