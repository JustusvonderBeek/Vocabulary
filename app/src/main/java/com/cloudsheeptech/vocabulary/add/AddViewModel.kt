package com.cloudsheeptech.vocabulary.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val editVmScope = CoroutineScope(Dispatchers.IO + job)

    val word = MutableLiveData<String>()
    val translation = MutableLiveData<String>()

    fun addNewWord() {
        editVmScope.launch {
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
        }
    }

}