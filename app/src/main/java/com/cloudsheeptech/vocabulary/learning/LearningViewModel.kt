package com.cloudsheeptech.vocabulary.learning

import android.util.Log
import android.widget.Toast
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.URL

class LearningViewModel : ViewModel() {

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

    private val vocabulary = Vocabulary()

    init {
        _learningVocab.value = "Learning"
        _translateVocab.value = "Lernen"
        _progress.value = "0/0"
        _editToggle.value = false
    }

    fun requestVocabulary() {
        vmScope.launch {
            vocabulary.updateVocabulary()
        }
    }

    fun showNewVocabulary() {
        Log.i("LearningViewModel", "Pressed new vocab")
        val next = vocabulary.getNextVocabulary()
        this._learningVocab.value = next.Vocabulary
        this._translateVocab.value = next.Translation
        this._progress.value = (vocabulary.wordIndex + 1).toString() + "/" + vocabulary.size.toString()
        Log.i("LearningViewModel", "Updated vocab to: ${_learningVocab.value}")
    }

    fun editWord() {
        Log.i("LearningViewModel", "Switched editing vocab")
        _editToggle.value = !_editToggle.value!!
    }

    fun wordEdited() {
        _editToggle.value = false
        if (_learningVocab.value == null || _translateVocab.value == null)
            return
        val updatedWord = Word(vocabulary.wordIndex, _learningVocab.value!!, _translateVocab.value!!)
        vmScope.launch {
            vocabulary.postItemVocabulary(updatedWord)
        }
    }
}