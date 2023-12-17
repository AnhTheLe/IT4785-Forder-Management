package com.example.filemanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ListAdapter (private val items: ArrayList<ItemModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            itemView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_view, parent, false)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
        } else {
            itemView = convertView
            viewHolder = itemView.tag as ViewHolder
        }

        viewHolder.textCaption.text = items[position].name
        viewHolder.imageThumb.setImageResource(items[position].image)

        return itemView
    }

    class ViewHolder(itemView: View) {
        val textCaption: TextView
        val imageThumb: ImageView
        init {
            textCaption = itemView.findViewById(R.id.name)
            imageThumb = itemView.findViewById(R.id.image)
        }

    }
}