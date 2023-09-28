package com.cloudsheeptech.vocabulary.learning

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
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

    private val vocabulary = Vocabulary()

    init {
        _learningVocab.value = "Learning"
        _translateVocab.value = "Lernen"
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
        Log.i("LearningViewModel", "Updated vocab to: ${_learningVocab.value}")
    }
}