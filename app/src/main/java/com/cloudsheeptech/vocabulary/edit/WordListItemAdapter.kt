package com.cloudsheeptech.vocabulary.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.databinding.WordListItemBinding

class WordListItemAdapter(val clickListener: WordListItemListener) : ListAdapter<Word, WordListItemAdapter.WordListItemViewHolder>(WordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListItemViewHolder {
        return WordListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: WordListItemViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }

    class WordListItemViewHolder private constructor(val binding : WordListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: WordListItemListener, item : Word) {
            binding.word = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent : ViewGroup) : WordListItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = WordListItemBinding.inflate(layoutInflater, parent, false)
                return WordListItemViewHolder(binding)
            }
        }
    }

    class WordListItemListener(val clickListener: (wordId: Int) -> Unit) {
        fun onClick(word: Word) = clickListener(word.ID)
    }

    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.ID == newItem.ID && oldItem.Vocabulary == newItem.Vocabulary && oldItem.Translation == newItem.Translation
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
}