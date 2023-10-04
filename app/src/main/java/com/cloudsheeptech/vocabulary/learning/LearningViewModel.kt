package com.cloudsheeptech.vocabulary.learning

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

class LearningViewModel(private val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val vmScope = CoroutineScope(Dispatchers.Main + job)

    private var _learningVocab = MutableLiveData<String>()
    val learningVocabulary : LiveData<String>
        get() = _learningVocab

    private val _translateVocab = MutableLiveData<String>()
    val translateVocabulary : LiveData<String>
        get() = _translateVocab

    private val _progress = MutableLiveData<String>()
    val progress : LiveData<String>
        get() = _progress

    private val _editToggle = MutableLiveData<Boolean>()
    val editingWord : LiveData<Boolean>
        get() = _editToggle

    private var currVocabIdx = 0

    init {
        _learningVocab.value = "Learning"
        _translateVocab.value = "Lernen"
        _progress.value = "0/0"
        _editToggle.value = false
    }

    fun requestVocabulary() {
        vmScope.launch {
            vocabulary.updateVocabulary()
            // Modifies data in this class, cannot run on background thread
            withContext(Dispatchers.Main) {
                showNextWord()
            }
        }
    }

    fun showNextWord() {
        Log.i("LearningViewModel", "Get next word")
        var next = Word(ID = 0, Vocabulary = "Empty", Translation = "Empty")
        if (vocabulary.vocabulary.isNotEmpty()) {
            next = vocabulary.vocabulary[currVocabIdx]
            currVocabIdx = (currVocabIdx + 1) % vocabulary.vocabulary.size
        }
        this._learningVocab.value = next.Vocabulary
        this._translateVocab.value = next.Translation
        this._progress.value = (currVocabIdx + 1).toString() + "/" + vocabulary.vocabulary.size.toString()
        Log.i("LearningViewModel", "Updated vocab to: ${_learningVocab.value}")
    }

    fun removeWord() {
        val removeIdx = Math.floorMod(currVocabIdx-1, vocabulary.vocabulary.size)
        Log.i("LearningViewModel", "Removing word at $removeIdx - $currVocabIdx - ${vocabulary.vocabulary.size}")
        vmScope.launch {
            vocabulary.removeVocabularyItem(removeIdx)
        }
    }

    fun editWord() {
        Log.i("LearningViewModel", "Switched editing vocab")
        _editToggle.value = !_editToggle.value!!
    }

    fun wordEdited() {
        _editToggle.value = false
        if (_learningVocab.value == null || _translateVocab.value == null)
            return
        val updatedWord = Word(currVocabIdx, _learningVocab.value!!, _translateVocab.value!!)
        vmScope.launch {
            vocabulary.postVocabularyItem(updatedWord)
        }
    }
}