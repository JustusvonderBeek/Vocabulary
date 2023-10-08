package com.cloudsheeptech.vocabulary.learning

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.SingleEvent
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

    val learningVocabulary = MutableLiveData<String>()
    val translateVocabulary = MutableLiveData<String>()

    private val _progress = MutableLiveData<String>()
    val progress : LiveData<String>
        get() = _progress

    private var _internalEditToggle = false
    private var _internalEditAbort = false

    private val _editToggle = MutableLiveData<SingleEvent<Boolean>>()
    val editToggle : LiveData<SingleEvent<Boolean>>
        get() = _editToggle

    private val _editedToggle = MutableLiveData<SingleEvent<Boolean>>()
    val editedToggle : LiveData<SingleEvent<Boolean>>
        get() = _editedToggle

    private var currVocabIdx = 0

    init {
        learningVocabulary.value = "Click on next"
        translateVocabulary.value = "Auf weiter klicken"
        _progress.value = "0/0"
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
        // Check if current word is edited and abort in that case edit
        if (_internalEditToggle) {
            _internalEditAbort = true
            editWord()
        }
        var next = Word(ID = 0, Vocabulary = "Empty", Translation = "Empty")
        if (vocabulary.vocabulary.isNotEmpty()) {
            next = vocabulary.vocabulary[currVocabIdx % vocabulary.vocabulary.size]
            currVocabIdx = (currVocabIdx + 1) % vocabulary.vocabulary.size
        }
        this.learningVocabulary.value = next.Vocabulary
        this.translateVocabulary.value = next.Translation
        this._progress.value = (currVocabIdx + 1).toString() + "/" + vocabulary.vocabulary.size.toString()
        Log.i("LearningViewModel", "Updated vocab to: ${learningVocabulary.value}")
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
        if (_internalEditToggle) {
            _internalEditToggle = false
            _editedToggle.value = SingleEvent<Boolean>(true)
        } else {
            _internalEditToggle = true
            _editToggle.value = SingleEvent<Boolean>(true)
        }
    }

    fun wordEdited() {
        Log.i("LearningViewModel", "Word edited")
        if (learningVocabulary.value == null || learningVocabulary.value == null) {
            Log.i("LearningViewModel", "One of the two values is empty")
            return
        }
        if (_internalEditAbort) {
            Log.i("LearningViewModel", "Aborting word edit")
            _internalEditAbort = false
            return
        }
        val updatedIdx = Math.floorMod(currVocabIdx-1, vocabulary.vocabulary.size)
        val updatedWord = Word(updatedIdx, learningVocabulary.value!!, translateVocabulary.value!!)
        Log.i("LearningViewModel", "Updating word to: $updatedWord")
        vmScope.launch {
            vocabulary.modifyVocabularyItem(updatedWord)
        }
    }
}