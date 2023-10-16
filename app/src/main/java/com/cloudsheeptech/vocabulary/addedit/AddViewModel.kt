package com.cloudsheeptech.vocabulary.addedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.SingleEvent
import com.cloudsheeptech.vocabulary.data.Vocabulary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val addVmScope = CoroutineScope(Dispatchers.IO + job)

    val word = MutableLiveData<String>()
    val translation = MutableLiveData<String>()

    private val handledToast = MutableLiveData<SingleEvent<String>>()
    val toast : LiveData<SingleEvent<String>>
        get() = handledToast

    fun addNewWord() {
        addVmScope.launch {
            if (word.value != null && translation.value != null) {
                vocabulary.postVocabulary(word.value!!, translation.value!!)
                resetValues()
            }
        }
    }

    private suspend fun resetValues() {
        withContext(Dispatchers.Main) {
            word.value = ""
            translation.value = ""
            handledToast.value = SingleEvent("Added vocabulary")
        }
    }

}