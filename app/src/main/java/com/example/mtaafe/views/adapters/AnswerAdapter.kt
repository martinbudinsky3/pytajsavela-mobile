package com.example.mtaafe.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Answer
import com.example.mtaafe.views.activities.OnAnswerClickListener
import com.example.mtaafe.views.viewholders.AnswerViewHolder


class AnswerAdapter (private val answersList: ArrayList<Answer>,
                     private val userId : Long,
                     private val listener: OnAnswerClickListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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

    fun updateData(answersListNew: ArrayList<Answer>) {
        answersList.clear()
        answersList.addAll(answersListNew)
        notifyDataSetChanged()
    }

    fun getAnswer(position: Int): Answer {
        return answersList[position]
    }
}