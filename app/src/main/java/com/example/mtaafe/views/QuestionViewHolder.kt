package com.example.mtaafe.views

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.QuestionItem

class QuestionViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private var titleText: TextView?
    private var authorText: TextView?
    private var createdAtText: TextView?
    private var answersCountText: TextView?

    init {
        titleText = view.findViewById(R.id.titleText)
        authorText = view.findViewById(R.id.authorText)
        createdAtText = view.findViewById(R.id.createdAtText)
        answersCountText = view.findViewById(R.id.answersCountText)
    }

    fun setQuestionItemData(questionItem: QuestionItem) {
        titleText?.text = questionItem.title
        authorText?.text = questionItem.author.name
        createdAtText?.text = questionItem.createdAt.toString()
        answersCountText?.text = questionItem.answersCount.toString()
    }
}