package com.example.mtaafe.views

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.QuestionItem

class QuestionAdapter (private val questionsList: List<QuestionItem>): RecyclerView.Adapter<QuestionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.question_item, parent, false)

        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val questionItem = questionsList[position]
        holder.setQuestionItemData(questionItem)
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }
}