package com.cloudsheeptech.vocabulary.recap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.SingleEvent
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.datastructures.LearningStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.exp

class RecapViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val recapScope = CoroutineScope(Dispatchers.IO + job)

    val showText = MutableLiveData<String>()
    val inputText = MutableLiveData<String>()
    val hintText = MutableLiveData<String>()
    private val recapList = LearningStack()
    val currentWord = MutableLiveData<Word>()
    private var currentDirection = RecapDirection.GERMAN_TO_SPANISH
    private var toggleForward = false

    private var _direction = RecapDirection.BOTH
    private var _directionToggle = false
    private val _result = MutableLiveData<RecapResult>(RecapResult.NONE)
    val result : LiveData<RecapResult> get() = _result

    private val _navigateToRecap = MutableLiveData<Boolean>(false)
    val navigateToRecap : LiveData<Boolean> get() = _navigateToRecap

    private val _navigateToRecapStart = MutableLiveData<Boolean>(false)
    val navigateToRecapStart : LiveData<Boolean> get() = _navigateToRecapStart

    private val _forward = MutableLiveData<SingleEvent<Boolean>>(SingleEvent(false))
    val forward : LiveData<SingleEvent<Boolean>> get() = _forward

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
        _direction = when(selectionResult) {
            RecapDirection.BOTH -> {
                Log.i("RecapViewModel", "Selected both")
                RecapDirection.BOTH
            }
            RecapDirection.GERMAN_TO_SPANISH -> {
                Log.i("RecapViewModel", "Selected german to spanish")
                RecapDirection.GERMAN_TO_SPANISH
            }
            RecapDirection.SPANISH_TO_GERMAN -> {
                Log.i("RecapViewModel", "Selected spanish to german")
                RecapDirection.SPANISH_TO_GERMAN
            }
        }
    }

    private fun updateWordCorrect(word : Word) {
        recapScope.launch {
            vocabulary.updateCorrectRepeat(word)
        }
    }

    private fun updateWordIncorrect(word : Word) {
        recapScope.launch {
            vocabulary.updateIncorrectRepeat(word)
        }
    }

    fun countAsCorrect() {
        Log.i("RecapViewModel", "Counted as correct")
        _result.value = RecapResult.COUNT_AS_CORRECT
        currentWord.value!!.Repeat -= 1
        updateWordCorrect(currentWord.value!!)
    }

    private fun wordEqualsInput(word : Word, input : String, direction: RecapDirection) : Boolean {
        when(direction) {
            RecapDirection.SPANISH_TO_GERMAN -> {
                return compareExpectedToInput(word.Vocabulary, input)
            }
            RecapDirection.GERMAN_TO_SPANISH -> {
                return compareExpectedToInput(word.Translation, input)
            }
            else -> {
                Log.e("RecapViewModel", "Given direction is not clear! Error is program")
            }
        }
        return false
    }

    private fun compareExpectedToInput(expected : String, input : String) : Boolean {
        val expectedTrimmed = expected.trim()
        val inputTrimmed = input.trim()
        if (expectedTrimmed == inputTrimmed)
            return true
        // Employ custom comparison
        var wrongCharCounter = 0
        for ((i,c) in expectedTrimmed.withIndex()) {
            if (i >= inputTrimmed.length) {
                wrongCharCounter += (expectedTrimmed.length - inputTrimmed.length)
                break
            }
            if (c != inputTrimmed[i])
                wrongCharCounter++
        }
        return wrongCharCounter < 2
    }

    fun compareWords() {
        if (toggleForward) {
            toggleForward = false
            _forward.value = SingleEvent(false)
            showNextWord()
            return
        }
        // Pressed for the first time, compare given input
        if (inputText.value.isNullOrEmpty()) {
            Log.i("RecapViewModel", "Given input is empty")
            return
        }
        val result = wordEqualsInput(currentWord.value!!, inputText.value!!, currentDirection)
        if (result) {

        }
        if (_direction == RecapDirection.BOTH || _direction == RecapDirection.SPANISH_TO_GERMAN) {
            if (!inputText.value.equals(currentWord.value!!.Translation, true)) {
                _result.value = RecapResult.INCORRECT
                hintText.value = "Expected: ${currentWord.value!!.Translation}\nGot: ${inputText.value}"
                updateWordIncorrect(currentWord.value!!)
            } else {
                _result.value = RecapResult.CORRECT
                updateWordCorrect(currentWord.value!!)
            }
        } else {
            if (!inputText.value.equals(currentWord.value!!.Vocabulary, true)) {
                _result.value = RecapResult.INCORRECT
                hintText.value = "Expected: ${currentWord.value!!.Vocabulary}\nGot: ${inputText.value}"
                updateWordIncorrect(currentWord.value!!)
            } else {
                _result.value = RecapResult.CORRECT
                updateWordCorrect(currentWord.value!!)
            }
        }
        toggleForward = true
        _forward.value = SingleEvent(true)
    }

    fun showNextWord() {
        _result.value = RecapResult.NONE
        val next = recapList.getNextWord()
        if (next == null) {
            // Nothing more to show, navigate to home I guess
            navigateToRecapStart()
            return
        }
        currentWord.value = next!!
        // Decide what to show
        if (_direction == RecapDirection.BOTH || _direction == RecapDirection.SPANISH_TO_GERMAN) {
            showText.value = next.Vocabulary
        } else {
            showText.value = next.Translation
        }
        inputText.value = ""
        hintText.value = ""
    }

    private fun prepareRecap() {
        recapList.addAll(vocabulary.wordList)
        recapList.selectItems(10)
    }

    fun navigateToRecap() {
        prepareRecap()
        showNextWord()
        _navigateToRecap.value = true
    }

    fun onRecapNavigated() {
        _directionToggle = true
        _navigateToRecap.value = false
    }

    private fun navigateToRecapStart() {
        _directionToggle = false
        _navigateToRecapStart.value = true
    }

    fun onStartNavigated() {
        _navigateToRecapStart.value = false
    }

}