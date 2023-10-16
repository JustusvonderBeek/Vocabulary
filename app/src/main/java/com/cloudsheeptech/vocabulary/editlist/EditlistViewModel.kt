package com.cloudsheeptech.vocabulary.editlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditlistViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    val vocabList = MutableLiveData<MutableList<Word>>()

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing : LiveData<Boolean>
        get() = _refreshing

    val learningVocabulary = MutableLiveData<String>()
    val translateVocabulary = MutableLiveData<String>()

    // Navigation
    private val _navigateToAddWord = MutableLiveData<Boolean>(false)
    val navigateToAdd : LiveData<Boolean> get() = _navigateToAddWord
    private val _navigateToEditWord = MutableLiveData<Int>(-1)
    val navigateToEdit : LiveData<Int> get() = _navigateToEditWord
    // ----

    init {
        vocabList.value = mutableListOf()
        for (word in vocabulary.wordList) {
            vocabList.value!!.add(word)
        }
        _refreshing.value = false
    }

    fun updateVocabulary() {
        scope.launch {
            withContext(Dispatchers.Main) {
                _refreshing.value = false
            }
            vocabulary.updateVocabulary()
            withContext(Dispatchers.Main) {
                _refreshing.value = false
            }
        }
    }

    fun removeVocabularyItem(id : Int) {
        scope.launch {
            withContext(Dispatchers.Main) {
                _refreshing.value = true
            }
            vocabulary.removeVocabularyItem(id)
            withContext(Dispatchers.Main) {
                _refreshing.value = false
            }
        }
    }

    fun editWord(id : Int) {
        val oldWord = vocabulary.wordList[id]
        _navigateToEditWord.value = oldWord.ID
    }

    fun onEditWordNavigated() {
        _navigateToEditWord.value = -1
    }

    fun navigateToAddWord() {
        _navigateToAddWord.value = true
    }

    fun onAddWordNavigated() {
        _navigateToAddWord.value = false
    }

}