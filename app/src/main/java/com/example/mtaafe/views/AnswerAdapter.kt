package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem

class AnswerAdapter (private val answersList: ArrayList<AnswerItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.answer_item, parent, false)

        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is AnswerViewHolder) {
            val answerItem = answersList[position]
            holder.setAnswerItemData(answerItem)
        }
    }

    override fun getItemCount(): Int {
        return answersList.size + 1
    }
}