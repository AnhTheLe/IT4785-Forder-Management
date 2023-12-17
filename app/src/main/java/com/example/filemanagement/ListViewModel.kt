package com.example.filemanagement

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListViewModel : ViewModel() {
    var path = MutableLiveData<String>(Environment.getExternalStorageDirectory().path)
    var lastPaths = MutableLiveData(arrayListOf<String>())
    var filePath = MutableLiveData<String>("")

    fun setPath(value:String){
        path.value = value
    }

    fun setFilePath(value:String){
        filePath.value = value
    }

    fun addLastPath(value: String){
        lastPaths.value?.add(0,value)
    }

    fun removeLastPath() {
        lastPaths.value?.removeAt(0)
    }
}