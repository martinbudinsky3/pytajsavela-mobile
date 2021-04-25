package com.example.mtaafe.views.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Tag
import com.example.mtaafe.views.viewholders.DeletableTagViewHolder

class DeletableTagAdapter(private val tagsList: ArrayList<Tag>): RecyclerView.Adapter<DeletableTagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletableTagViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deletable_tag_item, parent, false)

        return DeletableTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeletableTagViewHolder, position: Int) {
        val tag = tagsList[position]
        holder.setTagData(tag)
    }

    override fun getItemCount(): Int {
        return tagsList.size
    }

    fun removeItem(position: Int) {
        tagsList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, 1)
    }

    fun addItem(tag: Tag) {
        tagsList.add(tag)
        notifyItemInserted(itemCount + 1)
        notifyItemRangeInserted(itemCount, 1)
    }
}