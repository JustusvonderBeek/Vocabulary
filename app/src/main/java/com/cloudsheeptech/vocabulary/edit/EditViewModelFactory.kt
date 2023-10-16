package com.cloudsheeptech.vocabulary.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cloudsheeptech.vocabulary.addedit.AddViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary

class EditViewModelFactory(val vocabulary: Vocabulary) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(vocabulary) as T
        }
        throw IllegalArgumentException("Unknown class")
    }
}