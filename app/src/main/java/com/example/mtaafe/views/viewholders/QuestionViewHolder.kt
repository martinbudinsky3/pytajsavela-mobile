package com.example.mtaafe.views.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.QuestionItem
import com.example.mtaafe.views.activities.IQuestionDetailOpener
import com.example.mtaafe.views.adapters.TagAdapter
import com.google.android.flexbox.FlexboxLayoutManager

class QuestionViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val titleText: TextView? = view.findViewById(R.id.titleText)
    private val authorText: TextView? = view.findViewById(R.id.authorText)
    private val createdAtText: TextView? = view.findViewById(R.id.createdAtText)
    private val answersCountText: TextView? = view.findViewById(R.id.answersCountText)
    private val tagsListRecycler: RecyclerView = view.findViewById(R.id.tagsRecyclerView)
    private var activity: IQuestionDetailOpener = view.context as IQuestionDetailOpener


    fun setQuestionItemData(questionItem: QuestionItem) {
        titleText?.text = questionItem.title
        if (questionItem.author != null) {
            authorText?.text = questionItem.author.name
        } else {
            authorText?.text = ""
        }
        createdAtText?.text = questionItem.createdAt.toString()
        answersCountText?.text = questionItem.answersCount.toString()

        val adapter = TagAdapter(questionItem.tags)
        val layoutManager = FlexboxLayoutManager(view.context)
        tagsListRecycler.layoutManager = layoutManager
        tagsListRecycler.adapter = adapter

        view.setOnClickListener {
            activity.openQuestionDetailActivity(questionItem.id)
        }
    }
}