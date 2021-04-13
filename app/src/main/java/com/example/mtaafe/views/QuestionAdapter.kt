package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.QuestionItem

class QuestionAdapter (private val questionsList: ArrayList<QuestionItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_ITEM = 0
    private val TYPE_FOOTER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_ITEM) {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.question_item, parent, false)

            return QuestionViewHolder(view)
        }

        else {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pagination, parent, false)

            return PaginationViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is QuestionViewHolder) {
            val questionItem = questionsList[position]
            holder.setQuestionItemData(questionItem)
        }

        if(holder is PaginationViewHolder) {
            // TODO
        }
    }

    override fun getItemCount(): Int {
        return questionsList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if(position == questionsList.size) {
            return TYPE_FOOTER
        }

        return TYPE_ITEM
    }

    fun updateData(questionsListNew: ArrayList<QuestionItem>) {
        questionsList.clear()
        questionsList.addAll(questionsListNew)
        notifyDataSetChanged()
    }
}
