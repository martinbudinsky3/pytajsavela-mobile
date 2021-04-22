package com.example.mtaafe.views.viewholders

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R

class ImageFormViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val imageView: ImageView? = view.findViewById(R.id.image)

    fun setImage(uri: Uri?) {
        imageView?.setImageURI(uri)
    }
}