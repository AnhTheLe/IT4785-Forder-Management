package com.example.filemanagement

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.provider.Settings
import android.Manifest
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import java.io.File

class MainActivity : AppCompatActivity() {
    private val listViewModel: ListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listViewModel.path.observe(this, Observer {
            // Perform an action with the latest item data
        })

        listViewModel.lastPaths.observe(this,Observer{

        })

        listViewModel.filePath.observe(this,Observer{

        })
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<ListFragment>(R.id.container_body)
            addToBackStack("Blank")
        }

        if (Build.VERSION.SDK_INT < 30) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                Log.v("TAG", "Permission Denied => Request permission")
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
            } else {
                Log.v("TAG", "Permission Granted")
            }
        } else {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }
    }

    private fun showNewFolderDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Folder")
        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Create") { _, _ ->
            val folderName = input.text.toString()
            val newFolder = File(listViewModel.path.value, folderName)
            if (!newFolder.exists()) {
                newFolder.mkdir()
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<ListFragment>(R.id.container_body)
                }
            } else {
                Toast.makeText(this,"Folder already exists",Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showNewFileDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Text File")
        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Create") { _, _ ->
            val fileName = input.text.toString() +".txt"


            val newFile = File(listViewModel.path.value, fileName)
            if (!newFile.exists()) {
                newFile.createNewFile()
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<ListFragment>(R.id.container_body)
                }
            } else {
                Toast.makeText(this,"Folder already exists",Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.add_folder -> {
                showNewFolderDialog()
                true
            }

            R.id.add_file ->{
                showNewFileDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}