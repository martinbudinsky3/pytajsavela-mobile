package com.example.mtaafe.views.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Tag

class TagViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private var tagNameText: TextView? = view.findViewById(R.id.tagNameText)

    fun setTagData(tag: Tag) {
        tagNameText?.text = tag.name
    }

}