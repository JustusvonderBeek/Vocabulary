package com.cloudsheeptech.vocabulary.recap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.datastructures.LearningStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class RecapViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val recapScope = CoroutineScope(Dispatchers.IO + job)

    val showText = MutableLiveData<String>()
    val inputText = MutableLiveData<String>()
    val hintText = MutableLiveData<String>()
    private val recapList = LearningStack(vocabulary.wordList)
    val currentWord = MutableLiveData<Word>()

    private var _direction = RecapDirection.BOTH
    private var _directionToggle = false
    private val _result = MutableLiveData<RecapResult>(RecapResult.NONE)
    val result : LiveData<RecapResult> get() = _result

    private var init = false
    private var toggleForward = false

    init {
        currentWord.value = Word(0, "Press check to start", "Press check to start")
        showText.value = currentWord.value!!.Vocabulary
        inputText.value = ""
        hintText.value = ""
    }

    fun setupDirection(selectionResult : RecapDirection) {
        if (_directionToggle) {
            Log.i("RecapViewModel", "Direction already selected")
            return
        }
        when(selectionResult) {
            RecapDirection.BOTH -> {
                Log.i("RecapViewModel", "Selected both")
                _direction = RecapDirection.BOTH
            }
            RecapDirection.GERMAN_TO_SPANISH -> {
                _direction = RecapDirection.GERMAN_TO_SPANISH
            }
            RecapDirection.SPANISH_TO_GERMAN -> {
                _direction = RecapDirection.SPANISH_TO_GERMAN
            }
        }
    }

    fun compareWords() {
        if (!init) {
            init = true
            showNextWord()
            return
        }
        if (toggleForward) {
            toggleForward = false
            showNextWord()
            return
        }
        if (inputText.value.isNullOrEmpty()) {
            Log.i("RecapViewModel", "Given input is empty")
            _result.value = RecapResult.INCORRECT
            return
        }
        if (_direction == RecapDirection.BOTH || _direction == RecapDirection.SPANISH_TO_GERMAN) {
            if (!inputText.value.equals(currentWord.value!!.Translation, true)) {
                _result.value = RecapResult.INCORRECT
                hintText.value = "Expected: ${currentWord.value!!.Translation}\nGot: ${inputText.value}"
            } else {
                _result.value = RecapResult.CORRECT
            }
        } else {
            if (!inputText.value.equals(currentWord.value!!.Vocabulary, true)) {
                _result.value = RecapResult.INCORRECT
                hintText.value = "Expected: ${currentWord.value!!.Vocabulary}\nGot: ${inputText.value}"
            } else {
                _result.value = RecapResult.CORRECT
            }
        }
        toggleForward = true
    }

    fun showNextWord() {
        _directionToggle = true
        _result.value = RecapResult.NONE
        val next = recapList.getNextWord()
        if (next == null) {
            // Nothing more to show, navigate to home I guess
            // TODO:
            return
        }
        currentWord.value = next.word
        // Decide what to show
        if (_direction == RecapDirection.BOTH || _direction == RecapDirection.SPANISH_TO_GERMAN) {
            showText.value = next.word.Vocabulary
        } else {
            showText.value = next.word.Translation
        }
        inputText.value = ""
        hintText.value = ""
    }

}