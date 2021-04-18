package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem

class UserAnswerAdapter(private val answersList: ArrayList<AnswerItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_answer_item, parent, false)

        return UserAnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is UserAnswerViewHolder) {
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
}