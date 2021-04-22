package com.example.mtaafe.views.viewholders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem

class AnswerViewHolder(private val view: View, private val userId : Long): RecyclerView.ViewHolder(view), View.OnClickListener  {
    private val answerBodyText: TextView? = view.findViewById(R.id.answerBodyText)
    private val authorText2: TextView? = view.findViewById(R.id.authorText2)
    private val createdAtText2: TextView? = view.findViewById(R.id.createdAtText2)

    private val editBtn: Button = view.findViewById(R.id.editBtn2)
    private val deleteBtn: Button = view.findViewById(R.id.deleteBtn2)

    private var listener: OnAnswerClickListener = view.context as OnAnswerClickListener

    init {
        view.setOnClickListener(this)
    }

    fun setAnswerItemData(answerItem: AnswerItem) {
        answerBodyText?.text = answerItem.body
        authorText2?.text = answerItem.author.name
        createdAtText2?.text = answerItem.createdAt.toString()

        deleteBtn.setOnClickListener(View.OnClickListener {
            onClick(it)
        })

        editBtn.setOnClickListener(View.OnClickListener {
            onClickEdit(it)
        })

        if (answerItem.author.id != userId) {
            editBtn.visibility = View.INVISIBLE
            deleteBtn.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        val position: Int = adapterPosition

        if (position != RecyclerView.NO_POSITION)
            listener.onClickDeleteAnswer(position)
    }

    fun onClickEdit(v: View?) {
        val position: Int = adapterPosition

        if (position != RecyclerView.NO_POSITION)
            listener.onClickEditAnswer(position)
    }
}

interface OnAnswerClickListener {
    fun onClickDeleteAnswer(position: Int)
    fun onClickEditAnswer(position: Int)
}