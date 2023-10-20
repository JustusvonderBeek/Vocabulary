package com.cloudsheeptech.vocabulary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class AppViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val appModelScope = CoroutineScope(Dispatchers.IO + job)

    private val _wordList = MutableLiveData<MutableList<Word>>()
    val wordList : LiveData<MutableList<Word>> get() = _wordList



}