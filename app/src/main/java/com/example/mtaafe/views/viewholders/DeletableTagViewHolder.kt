package com.example.mtaafe.views.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Tag
import com.example.mtaafe.views.activities.TagDeleteClickListener

class DeletableTagViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private var tagNameText: TextView? = view.findViewById(R.id.tagNameText)
    private var closeText: TextView? = view.findViewById(R.id.closeText)
    private var activity: TagDeleteClickListener = view.context as TagDeleteClickListener

    fun setTagData(tag: Tag) {
        tagNameText?.text = tag.name
        closeText?.setOnClickListener {
            activity.removeTag(bindingAdapterPosition)
        }
    }
}