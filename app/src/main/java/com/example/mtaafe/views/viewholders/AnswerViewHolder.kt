package com.example.mtaafe.views.viewholders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.Answer
import com.example.mtaafe.data.models.AnswersDecodedImage
import com.example.mtaafe.views.activities.OnAnswerClickListener
import com.example.mtaafe.views.adapters.ImageAdapter

class AnswerViewHolder(private val view: View, private val userId : Long): RecyclerView.ViewHolder(view)  {
    private val answerBodyText: TextView? = view.findViewById(R.id.answerBodyText)
    private val authorText: TextView? = view.findViewById(R.id.authorText2)
    private val createdAtText: TextView? = view.findViewById(R.id.createdAtText2)
    val imagesListRecycler: RecyclerView = view.findViewById(R.id.imagesRecyclerView)
    private val editBtn: ImageButton = view.findViewById(R.id.editBtn2)
    private val deleteBtn: ImageButton = view.findViewById(R.id.deleteBtn2)

    private lateinit var imageAdapter: ImageAdapter
    private var listener: OnAnswerClickListener = view.context as OnAnswerClickListener


    fun setAnswerItemData(answer: Answer) {
        answerBodyText?.text = answer.body
        authorText?.text = answer.author.name
        createdAtText?.text = answer.createdAt.toString()

        imageAdapter = ImageAdapter(ArrayList())
        imagesListRecycler.layoutManager = LinearLayoutManager(view.context)
        imagesListRecycler.adapter = imageAdapter

        imageAdapter.updateSize(answer.images.size)

        deleteBtn.setOnClickListener{
            onClickDelete(it)
        }

        editBtn.setOnClickListener {
            onClickEdit(it)
        }

        if (answer.author.id != userId) {
            editBtn.visibility = View.GONE
            deleteBtn.visibility = View.GONE
        }
    }

    fun showImage(answersDecodedImage: AnswersDecodedImage) {
        imageAdapter.addItem(answersDecodedImage.imageIndex, answersDecodedImage.bitmap)
    }

    private fun onClickDelete(v: View?) {
        val position: Int = bindingAdapterPosition

        if (position != RecyclerView.NO_POSITION)
            listener.onClickDeleteAnswer(position)
    }

    private fun onClickEdit(v: View?) {
        val position: Int = bindingAdapterPosition

        if (position != RecyclerView.NO_POSITION)
            listener.onClickEditAnswer(position)
    }
}