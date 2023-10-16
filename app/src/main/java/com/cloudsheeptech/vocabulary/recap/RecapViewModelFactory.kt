package com.cloudsheeptech.vocabulary.recap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cloudsheeptech.vocabulary.addedit.AddViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary

class RecapViewModelFactory(val vocabulary: Vocabulary) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecapViewModel::class.java)) {
            return RecapViewModel(vocabulary) as T
        }
        throw IllegalArgumentException("Unknown class")
    }
}