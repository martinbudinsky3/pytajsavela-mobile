package com.example.mtaafe.views.viewholders

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R

class ImageViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val image: ImageView? = view.findViewById(R.id.img)

    fun setImage(bitmap: Bitmap) {
        image?.setImageBitmap(bitmap)
    }
}