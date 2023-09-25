package com.cloudsheeptech.vocabulary.learning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LearningViewModel : ViewModel() {
    private val _learningVocab = MutableLiveData<String>()
    val learningVocabulary : LiveData<String>
        get() = _learningVocab

    private val _translateVocab = MutableLiveData<String>()
    val translateVocabulary : LiveData<String>
        get() = _translateVocab

    init {
        _learningVocab.value = "Learning"
        _translateVocab.value = "Lernen"
    }

}