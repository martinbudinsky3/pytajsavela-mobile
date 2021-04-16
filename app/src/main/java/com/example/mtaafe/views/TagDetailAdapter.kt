package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Tag

class TagDetailAdapter(private val tagsList: ArrayList<Tag>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_ITEM = 0
    val TYPE_FOOTER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_ITEM) {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_detail_item, parent, false)

            return TagDetailViewHolder(view)
        }

        else {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pagination, parent, false)

            return PaginationViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is TagDetailViewHolder) {
            val tagItem = tagsList[position]
            holder.setTagItemData(tagItem)
        }

        if(holder is PaginationViewHolder) {
            // TODO
        }
    }

    override fun getItemCount(): Int {
        return tagsList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if(position == tagsList.size) {
            return TYPE_FOOTER
        }

        return TYPE_ITEM
    }

    fun updateData(tagsListNew: ArrayList<Tag>) {
        tagsList.clear()
        tagsList.addAll(tagsListNew)
        notifyDataSetChanged()
    }
}