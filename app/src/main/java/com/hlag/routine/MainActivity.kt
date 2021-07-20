package com.hlag.routine

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_steps.*
import java.io.File

class MainActivity : AppCompatActivity() {
    var routines: ArrayList<Routine> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Permissions
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
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

                FileManager.writeRoutine(this, routine)

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

        when (item.itemId) {
            R.id.action_settings -> {
                val builder = AlertDialog.Builder(this.applicationContext)
                builder.setTitle("Test dialog")
                builder.setMessage("Content")
                val alert = builder.create()
                alert.getWindow()!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                alert.show()
                return true
            }
            R.id.action_setFolder -> {
                DirDialog().show(supportFragmentManager, "tag")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadFromStorage(){
        val dir = File(FileManager.dir)

        if(!dir.isDirectory){
            Log.d("Is file", dir.toString())
            return
        }

        for(file in dir.listFiles()){
            routines.add(Routine(file.nameWithoutExtension, ArrayList<Step>()))
        }
        (routines_gridview.adapter as RoutineAdapter).notifyDataSetChanged()
    }
}
