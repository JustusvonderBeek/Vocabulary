package com.cloudsheeptech.vocabulary.editlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cloudsheeptech.vocabulary.data.Vocabulary

class EditlistViewModelFactory(val vocabulary: Vocabulary) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditlistViewModel::class.java)) {
            return EditlistViewModel(vocabulary) as T
        }
        throw IllegalArgumentException("Unknown class")
    }

}