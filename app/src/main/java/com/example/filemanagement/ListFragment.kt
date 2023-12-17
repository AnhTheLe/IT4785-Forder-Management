package com.example.filemanagement

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.*
import java.io.File


class ListFragment : Fragment() {

    private val listViewModel: ListViewModel by activityViewModels()
    private lateinit var selectedItem : File
    private var items = arrayListOf<ItemModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        // Inflate the layout for this fragment
        registerForContextMenu(view.findViewById(R.id.listView))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listView)
        fun getData() {
            items.clear()
            val root = File(listViewModel.path.value)
            val listFiles = root.listFiles()
            if (listFiles != null)
                for (item in listFiles) {
                    if (item.isDirectory) {
                        items.add(
                            (ItemModel(
                                item.name,
                                true,
                                resources.getIdentifier("baseline_folder_24", "drawable", "com.example.filemanagement")
                            ))
                        )
                    } else if (item.isFile) {
                        items.add(
                            (ItemModel(
                                item.name,
                                false,
                                resources.getIdentifier(
                                    "baseline_file_present_24",
                                    "drawable",
                                    "com.example.filemanagement"
                                )
                            ))
                        )
                    }
                }
            listView.adapter = ListAdapter(items)
        }

        getData()
        listView.setOnItemClickListener { _, _, position, _ ->
            if (items[position].isFolder) {
                listViewModel.path.value?.let { listViewModel.addLastPath(it) }
                listViewModel.setPath(listViewModel.path.value + "/${items[position].name}")
                getData()
            } else {
                listViewModel.setFilePath(listViewModel.path.value + "/${items[position].name}")
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<DetailFragment>(R.id.container_body)
                    addToBackStack("Blank")
                }
            }
        }

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            if (listViewModel.lastPaths.value!!.size > 0) {
                listViewModel.setPath(listViewModel.lastPaths.value!![0])
                listViewModel.removeLastPath()
                getData()
            }
        }

    }

    private fun showRenameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Rename")
        val input = EditText(requireContext())
        input.setText(selectedItem.name)
        builder.setView(input)

        builder.setPositiveButton("Rename") { _, _ ->
            val newName = input.text.toString()
            val newFile = File(listViewModel.path.value, newName)
            if (!newFile.exists()) {
                selectedItem.renameTo(newFile)
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<ListFragment>(R.id.container_body)
                }
            } else {
                Toast.makeText(requireContext(),"File/Folder with the same name already exists", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete")
        builder.setMessage("Are you sure you want to delete ${selectedItem.name}?")

        builder.setPositiveButton("Delete") { _, _ ->
            selectedItem.delete()
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<ListFragment>(R.id.container_body)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showCopyDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Copy to...")
        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("Copy") { _, _ ->
            val destinationPath = input.text.toString()
            val destinationDirectory = File(Environment.getExternalStorageDirectory().path + "/$destinationPath")
            if (destinationDirectory.exists() && destinationDirectory.isDirectory) {
                val newFile = File(destinationDirectory, selectedItem.name)
                selectedItem.copyTo(newFile)
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<ListFragment>(R.id.container_body)
                }
            } else {
                Toast.makeText(requireContext(),"Invalid destination path", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo

        selectedItem = File(listViewModel.path.value + "/${items[info.position].name}")
        if (selectedItem.isDirectory){
            activity?.menuInflater?.inflate(R.menu.context_folder_menu,menu)
        }else if (selectedItem.isFile){
            activity?.menuInflater?.inflate(R.menu.context_file_menu,menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.rename_folder, R.id.rename_file -> {
                showRenameDialog()
                return true
            }

            R.id.delete_folder, R.id.delete_file -> {
                showDeleteDialog()
                return true
            }

            R.id.copy_file -> {
                showCopyDialog()
                return true
            }

            else -> return super.onContextItemSelected(item)

        }
    }
}