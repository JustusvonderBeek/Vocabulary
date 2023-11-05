package com.cloudsheeptech.vocabulary.editlist

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Confidence
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.databinding.WordListItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordListItemAdapter(private val vocabulary: Vocabulary, val clickListener: WordListItemListener, private val resource : Resources) : ListAdapter<Word, WordListItemAdapter.WordListItemViewHolder>(WordDiffCallback()) {

    suspend fun deleteItemAt(position : Int) {
        Log.i("WordListItemAdapter", "Remove item at $position")
        vocabulary.removeVocabularyItem(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListItemViewHolder {
        return WordListItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: WordListItemViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position), resource)
    }

    class WordListItemViewHolder private constructor(val binding : WordListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: WordListItemListener, item : Word, resource: Resources) {
            binding.word = item
            binding.clickListener = clickListener
            val theme = resource.newTheme()
            var color = resource.getColor(R.color.confidence_new, theme)
            var gradient = R.drawable.new_gradient
            when (Confidence.convertIntToConfidence(item.Confidence)) {
                Confidence.PERFECT -> {
                    color = resource.getColor(R.color.confidence_perfect, theme)
                    gradient = R.drawable.perfect_gradient
                }
                Confidence.GOOD -> {
                    color = resource.getColor(R.color.confidence_good, theme)
                    gradient = R.drawable.good_gradient
                }
                Confidence.BAD -> {
                    color = resource.getColor(R.color.confidence_bad, theme)
                    gradient = R.drawable.bad_gradient
                }
                Confidence.POOR -> {
                    color = resource.getColor(R.color.confidence_poor, theme)
                    gradient = R.drawable.poor_gradient
                }
                else -> {
                    // Does not happen
                }
            }
            binding.confidenceCircle.background.setTint(color)
            binding.itemWrapper.background = ResourcesCompat.getDrawable(resource, gradient, theme)
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