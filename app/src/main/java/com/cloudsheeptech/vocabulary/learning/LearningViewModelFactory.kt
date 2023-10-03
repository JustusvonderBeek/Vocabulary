package com.cloudsheeptech.vocabulary.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cloudsheeptech.vocabulary.data.Vocabulary
import kotlin.IllegalArgumentException

class LearningViewModelFactory(val vocabulary: Vocabulary) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
            return LearningViewModel(vocabulary) as T
        }
        throw IllegalArgumentException("Unknown class")
    }
}