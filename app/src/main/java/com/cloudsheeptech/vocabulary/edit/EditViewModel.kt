package com.cloudsheeptech.vocabulary.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    val vocabList = MutableLiveData<MutableList<Word>>()

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing : LiveData<Boolean>
        get() = _refreshing

    val learningVocabulary = MutableLiveData<String>()
    val translateVocabulary = MutableLiveData<String>()

    init {
        vocabList.value = mutableListOf()
        for (word in vocabulary.vocabulary) {
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
        scope.launch {
            val oldWord = vocabulary.vocabulary[id]
            val updatedWord = Word(oldWord.ID, learningVocabulary.value!!, translateVocabulary.value!!)
            vocabulary.postVocabularyItem(updatedWord)
        }
    }

}