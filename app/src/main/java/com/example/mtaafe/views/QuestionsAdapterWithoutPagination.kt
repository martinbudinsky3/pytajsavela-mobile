package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.QuestionItem

class QuestionsAdapterWithoutPagination(private val questionsList: ArrayList<QuestionItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_item, parent, false)

        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is QuestionViewHolder) {
            val questionItem = questionsList[position]
            holder.setQuestionItemData(questionItem)
        }
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }

    fun updateData(questionsListNew: ArrayList<QuestionItem>) {
        questionsList.clear()
        questionsList.addAll(questionsListNew)
        notifyDataSetChanged()
    }
}