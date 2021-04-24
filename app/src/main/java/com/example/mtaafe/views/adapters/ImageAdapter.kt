package com.example.mtaafe.views.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R
import com.example.mtaafe.data.models.DecodedImage
import com.example.mtaafe.views.viewholders.ImageViewHolder

class ImageAdapter (private var images: ArrayList<Bitmap?>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false)

        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ImageViewHolder) {
            val image = images[position]
            if (image != null) {
                holder.setImage(image)
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

//    fun updateData(newImages: ArrayList<Image>) {
//        images.clear()
//        notifyDataSetChanged()
//    }

    fun updateSize(size: Int) {
        for(i in 0 until size) {
            images.add(null)
        }
        notifyDataSetChanged()
    }

    fun addItem(image: DecodedImage) {
        images.add(image.index, image.bitmap)
        notifyItemInserted(image.index)
        notifyItemRangeInserted(itemCount, 1)
    }
}