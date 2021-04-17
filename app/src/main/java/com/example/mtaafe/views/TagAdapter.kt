package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Tag

class TagAdapter (private val tagsList: ArrayList<Tag>): RecyclerView.Adapter<TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_item, parent, false)

        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagsList[position]
        holder.setTagData(tag)
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

}