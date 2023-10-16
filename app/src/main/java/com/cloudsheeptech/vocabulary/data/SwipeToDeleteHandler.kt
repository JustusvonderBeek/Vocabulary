package com.cloudsheeptech.vocabulary.data

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cloudsheeptech.vocabulary.edit.WordListItemAdapter

class SwipeToDeleteHandler(private val adapter: WordListItemAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.deleteItemAt(viewHolder.adapterPosition)
    }

}