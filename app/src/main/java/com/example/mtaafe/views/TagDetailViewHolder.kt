package com.example.mtaafe.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Tag

class TagDetailViewHolder(private val view: View): RecyclerView.ViewHolder(view)  {
    private var tagNameText: TextView? = view.findViewById(R.id.tagNameText)
    private var questionsCountText: TextView? = view.findViewById(R.id.questionsCountText)

    fun setTagItemData(tag: Tag) {
        tagNameText?.text = tag.name
        questionsCountText?.text = "${tag.questionsCount}x"
    }
}