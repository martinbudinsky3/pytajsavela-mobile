package com.example.mtaafe.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem
import com.example.mtaafe.data.models.QuestionItem

class AnswerViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val answerBodyText: TextView? = view.findViewById(R.id.answerBodyText)
    private val authorText2: TextView? = view.findViewById(R.id.authorText2)
    private val createdAtText2: TextView? = view.findViewById(R.id.createdAtText2)

    fun setAnswerItemData(answerItem: AnswerItem) {
        answerBodyText?.text = answerItem.body
        authorText2?.text = answerItem.author.name
        createdAtText2?.text = answerItem.createdAt.toString()
    }
}