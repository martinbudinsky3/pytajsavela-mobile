package com.example.mtaafe.views.viewholders

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.views.activities.IQuestionDetailOpener
import com.example.mtaafe.views.activities.ImageClickListener
import com.example.mtaafe.views.adapters.ImageFormAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ImageFormViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
    private val imageView: ImageView? = view.findViewById(R.id.image)
    private val closeButton: FloatingActionButton? = view.findViewById(R.id.closeButton)
    private var activity: ImageClickListener = view.context as ImageClickListener

    fun setImage(uri: Uri?) {
        imageView?.setImageURI(uri)
        closeButton?.setOnClickListener {
            activity.removeImage(bindingAdapterPosition)
        }
    }
}