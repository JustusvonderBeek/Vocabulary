package com.cloudsheeptech.vocabulary.learning

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.SingleEvent
import com.cloudsheeptech.vocabulary.data.LearnWord
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.datastructures.LearningStack
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

    val vocabList = MutableLiveData<MutableList<Word>>()

    private val _progress = MutableLiveData<String>()
    val progress : LiveData<String>
        get() = _progress

    private val _navigateToEdit = MutableLiveData<Int>(-1)
    val navigateToEdit : LiveData<Int> get() = _navigateToEdit

    private var currVocabIdx = 0
    private var learningList = LearningStack(vocabulary.wordList)

    init {
        learningVocabulary.value = "Click on next"
        translateVocabulary.value = "Auf weiter klicken"
        _progress.value = "0/0"
//        for (word in vocabulary.vocabulary) {
//            vocabList.value!!.add(word)
//        }
    }

    fun showNextWord() {
        Log.i("LearningViewModel", "pressed next word")

        var next = Word(ID = 0, Vocabulary = "Empty", Translation = "Empty")
        if (learningList.isNotEmpty()) {
            val tmp = learningList.getNextWord()
            if (tmp != null) {
                next = tmp.word
                currVocabIdx = next.ID
            }
        } else {
            learningList.addAllWords(vocabulary.wordList)
            showNextWord()
        }
        this.learningVocabulary.value = next.Vocabulary
        this.translateVocabulary.value = next.Translation
        this._progress.value = (currVocabIdx + 1).toString() + "/" + vocabulary.wordList.size.toString()
        Log.i("LearningViewModel", "Updated vocab to: ${learningVocabulary.value}")
    }

    fun removeWord() {
        val removeIdx = Math.floorMod(currVocabIdx, vocabulary.wordList.size)
        Log.i("LearningViewModel", "Removing word at $removeIdx - $currVocabIdx - ${vocabulary.wordList.size}")
        vmScope.launch {
            vocabulary.removeVocabularyItem(removeIdx)
        }
    }

    fun editWord() {
        _navigateToEdit.value = currVocabIdx
    }

    fun navigatedToEditWord() {
        _navigateToEdit.value = -1
    }

}