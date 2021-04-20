package com.example.mtaafe.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem

class UserAnswerViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val questionTitleText: TextView? = view.findViewById(R.id.answersQuestionText)
    private val answerText: TextView? = view.findViewById(R.id.answerText)
    private val createdAtText: TextView? = view.findViewById(R.id.createdAtText)

    fun setAnswerItemData(answerItem: AnswerItem) {
        questionTitleText?.text = answerItem.question.title
        answerText?.text = answerItem.body
        createdAtText?.text = answerItem.createdAt.toString()
    }
}