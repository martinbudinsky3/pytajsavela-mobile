package com.example.mtaafe.views

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.mtaafe.R

class PaginationViewHolder(view: View): RecyclerView.ViewHolder(view){

    private var firstPageButton: Button = view.findViewById(R.id.firstPageButton)
    private var previousPageButton: Button = view.findViewById(R.id.previousPageButton)
    private var nextPageButton: Button = view.findViewById(R.id.nextPageButton)
    private var lastPageButton: Button = view.findViewById(R.id.lastPageButton)
    private var activity: IPageButtonClickListener = view.context as IPageButtonClickListener

    init {
        firstPageButton.setOnClickListener{
            activity.handleFirstPageButtonClick()
        }

        previousPageButton.setOnClickListener{
            activity.handlePreviousPageButtonClick()
        }

        nextPageButton.setOnClickListener{
            activity.handleNextPageButtonClick()
        }

        lastPageButton.setOnClickListener{
            activity.handleLastPageButtonClick()
        }
    }
}