package com.example.filemanagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.*
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException

class DetailFragment : Fragment() {

    private val listViewModel: ListViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = File(listViewModel.filePath.value.toString())

        view.findViewById<TextView>(R.id.fileName).text = file.name

        fun displayImage() {
            Glide.with(this).load(file).into(view.findViewById<ImageView>(R.id.fileImage))
            view.findViewById<TextView>(R.id.filecontent).visibility = View.GONE
        }
        try {
            if (file.name.toString().indexOf(".txt") > 0) {
                view.findViewById<TextView>(R.id.filecontent).text = file.readText()

            } else if (file.name.toString().indexOf(".jpg") > 0 || file.name.toString()
                    .indexOf(".png") > 0 || file.name.toString().indexOf(".bmp") > 0
            ) {
                displayImage()
            }else{
                view.findViewById<TextView>(R.id.filecontent).text ="Can not read this file!"
            }


        } catch (e: IOException) {
            e.printStackTrace()
            Log.v("TAG", "$e")
        }


        view.findViewById<ImageView>(R.id.returnArrow).setOnClickListener() {
            listViewModel.filePath.value = ""
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace<ListFragment>(R.id.container_body)
            }
        }
    }
}