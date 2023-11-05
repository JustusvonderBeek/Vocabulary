package com.cloudsheeptech.vocabulary.learning

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.datastructures.LearningStack
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LearningViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val vmScope = CoroutineScope(Dispatchers.Main + job)

    val learningVocabulary = MutableLiveData<String>()
    val translateVocabulary = MutableLiveData<String>()

    private val _currentVocabId = MutableLiveData<String>()
    val currentVocabId : LiveData<String>
        get() = _currentVocabId

    private val _totalVocab = MutableLiveData<String>()
    val totalVocab : LiveData<String> get() = _totalVocab

    private val _navigateToEdit = MutableLiveData<Int>(-1)
    val navigateToEdit : LiveData<Int> get() = _navigateToEdit

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl : LiveData<String> get() = _imageUrl

    private var currVocabIdx = -1
    private var learningList = LearningStack()

    init {
        learningVocabulary.value = "Click on next"
        translateVocabulary.value = "Auf weiter klicken"
        _totalVocab.value = vocabulary.length().toString()
        _currentVocabId.value = "0"
    }

    fun showNextWord() {
        Log.i("LearningViewModel", "pressed next word")

        var next = Word(ID = 0, Vocabulary = "Empty", Translation = "Empty")
//        if (learningList.isNotEmpty()) {
//            val tmp = learningList.getNextWord()
//            if (tmp != null) {
//                next = tmp.word
//                currVocabIdx = next.ID
//            }
//        } else {
//            learningList.addAllWords(vocabulary.wordList)
//            showNextWord()
//            return
//        }
        if (vocabulary.wordList.isNotEmpty()) {
            currVocabIdx = Math.floorMod(currVocabIdx + 1, vocabulary.wordList.size)
            next = vocabulary.wordList[currVocabIdx]
//            vmScope.launch {
////                loadImageToWord(next.Translation)
//            }
        }
        this.learningVocabulary.value = next.Vocabulary
        this.translateVocabulary.value = next.Translation
        this._currentVocabId.value = (next.ID + 1).toString()
        Log.i("LearningViewModel", "Updated vocab to: ${learningVocabulary.value}")
    }

    fun showPreviousWord() {
        Log.i("LearningViewModel", "pressed previous word")

        var previous = Word(ID = 0, Vocabulary = "No previous word", Translation = "Empty")
        if (currVocabIdx != -1) {
            currVocabIdx = Math.floorMod(currVocabIdx - 1, vocabulary.wordList.size)
            previous = vocabulary.wordList[currVocabIdx]
        }
        this.learningVocabulary.value = previous.Vocabulary
        this.translateVocabulary.value = previous.Translation
        this._currentVocabId.value = (previous.ID + 1).toString()
    }

    fun removeWord() {
        val removeIdx = Math.floorMod(currVocabIdx, vocabulary.wordList.size)
        Log.i("LearningViewModel", "Removing word at $removeIdx - $currVocabIdx - ${vocabulary.wordList.size}")
        vmScope.launch {
            vocabulary.removeVocabularyItem(removeIdx)
        }
    }

    private suspend fun loadImageToWord(word : String) {
        withContext(Dispatchers.IO) {
            val client = HttpClient()
            val response : HttpResponse = client.get("https://www.google.com/search?tbm=isch&q=$word")
            Log.i("LearningViewModel","Body:\n${response.bodyAsText(Charsets.UTF_8)}")
        }
    }

    fun editWord() {
        _navigateToEdit.value = currVocabIdx
    }

    fun navigatedToEditWord() {
        _navigateToEdit.value = -1
    }

}