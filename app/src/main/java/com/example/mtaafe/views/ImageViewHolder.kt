package com.example.mtaafe.views

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem
import com.example.mtaafe.data.models.Image

class ImageViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val image: ImageView? = view.findViewById(R.id.img)

    fun setImage(image: Image) {
        Log.d("a", "sd")
    }
}