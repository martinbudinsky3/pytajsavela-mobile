package com.example.mtaafe.views.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.views.viewholders.ImageFormViewHolder
import com.example.mtaafe.views.viewholders.ImageViewHolder

class ImageFormAdapter (private var images: ArrayList<Uri?>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_form_item, parent, false)

        return ImageFormViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ImageFormViewHolder) {
            val image = images[position]
            holder.setImage(image)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateData(imagesNew: ArrayList<Uri?>) {
        images.clear()
        images.addAll(imagesNew)
        notifyDataSetChanged()
    }
}