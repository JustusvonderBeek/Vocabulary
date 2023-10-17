package com.cloudsheeptech.vocabulary.editlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.databinding.WordListItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordListItemAdapter(private val vocabulary: Vocabulary, val clickListener: WordListItemListener) : ListAdapter<Word, WordListItemAdapter.WordListItemViewHolder>(WordDiffCallback()) {

    suspend fun deleteItemAt(position : Int) {
        Log.i("WordListItemAdapter", "Remove item at $position")
        vocabulary.removeVocabularyItem(position)
    }

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