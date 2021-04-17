package com.example.mtaafe.views

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.config.Constants
import com.example.mtaafe.config.Constants.TAGS_LIST_COLS
import com.example.mtaafe.data.models.Tag

class TagDetailViewHolder(private val view: View): RecyclerView.ViewHolder(view)  {
    private var tagNameText: TextView? = view.findViewById(R.id.tagNameText)
    private var questionsCountText: TextView? = view.findViewById(R.id.questionsCountText)


    fun setTagItemData(tag: Tag) {
        tagNameText?.text = tag.name
        questionsCountText?.text = "${tag.questionsCount}x"

        view.setOnClickListener{

        }
    }

    private fun removeMargin() {
        val params = view.findViewById<LinearLayout>(R.id.tagDetailItemRoot).layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = 0
        view.layoutParams = params
    }
}