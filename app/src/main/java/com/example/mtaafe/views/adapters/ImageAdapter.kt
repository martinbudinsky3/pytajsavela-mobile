package com.example.mtaafe.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.views.viewholders.ImageViewHolder

class ImageAdapter (private var images: List<ByteArray>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false)

        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ImageViewHolder) {
            val image = images[position]
            holder.setImage(image)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateData(imagesNew: List<ByteArray>) {
        images = mutableListOf<ByteArray>()
        for (image in imagesNew) {
            (images as MutableList<ByteArray>).add(image)
        }
        notifyDataSetChanged()
    }
}