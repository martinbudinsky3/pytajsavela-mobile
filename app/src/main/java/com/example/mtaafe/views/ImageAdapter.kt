package com.example.mtaafe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.AnswerItem
import com.example.mtaafe.data.models.Image

class ImageAdapter (private val images: ArrayList<Image>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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

    fun updateData(imagesNew: ArrayList<Image>) {
        images.clear()
        images.addAll(imagesNew)
        notifyDataSetChanged()
    }
}