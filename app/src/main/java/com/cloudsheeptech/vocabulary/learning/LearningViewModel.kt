package com.cloudsheeptech.vocabulary.learning

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.URL

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

    fun requestVocabulary() {
        val url = URL("http://localhost:50000/v1/words")
//        val url = URL("https://vocabulary.cloudsheeptech.com/v1/words")
        val urlConnection = url.openConnection()
        try {
            val income = BufferedInputStream(urlConnection.getInputStream())
            val data = income.readBytes()
            val string = data.decodeToString()
            income.close()
            Log.i("LearningViewModel", "Got data: $string")
        } catch (ex : Exception) {
            Log.i("LearningViewModel", "Failed to make request")
        }
    }

}