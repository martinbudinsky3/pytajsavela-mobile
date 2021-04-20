package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem
import com.example.mtaafe.data.models.QuestionItem

class AnswerAdapter (private val answersList: ArrayList<AnswerItem>,
                     private val userId : Long,
                     private val listener: OnAnswerClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.answer_item, parent, false)

        return AnswerViewHolder(view, userId)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is AnswerViewHolder) {
            val answerItem = answersList[position]
            holder.setAnswerItemData(answerItem)
        }
    }

    override fun getItemCount(): Int {
        return answersList.size
    }

    fun updateData(answersListNew: ArrayList<AnswerItem>) {
        answersList.clear()
        answersList.addAll(answersListNew)
        notifyDataSetChanged()
    }

    fun getAnswer(position: Int): AnswerItem {
        return answersList[position]
    }
}