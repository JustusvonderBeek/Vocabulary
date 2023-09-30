package com.cloudsheeptech.vocabulary.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cloudsheeptech.vocabulary.data.Vocabulary

class AddViewModelFactory(val vocabulary: Vocabulary) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(vocabulary) as T
        }
        throw IllegalArgumentException("Unknown class")
    }

}