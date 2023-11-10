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
import kotlin.random.Random

class RecapViewModel(val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val recapScope = CoroutineScope(Dispatchers.IO + job)

    val showText = MutableLiveData<String>()
    val inputText = MutableLiveData<String>()
    val hintText = MutableLiveData<String>()
    val resultText = MutableLiveData<String>()
    private val recapList = LearningStack()
    val currentWord = MutableLiveData<Word>()
    private var currentDirection = RecapDirection.GERMAN_TO_SPANISH
    private var toggleForward = false
    private var updateCorrect = false

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

    private val _hideKeyboard = MutableLiveData<Boolean>(false)
    val hideKeyboard : LiveData<Boolean> get() = _hideKeyboard

    init {
        currentWord.value = Word(0, "Press check to start", "Press check to start")
        showText.value = currentWord.value!!.Vocabulary
        inputText.value = ""
        hintText.value = ""
        resultText.value = "Correct"
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
                currentDirection = RecapDirection.GERMAN_TO_SPANISH
                RecapDirection.GERMAN_TO_SPANISH
            }
            RecapDirection.SPANISH_TO_GERMAN -> {
                Log.i("RecapViewModel", "Selected spanish to german")
                currentDirection = RecapDirection.SPANISH_TO_GERMAN
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
       updateCorrect = true
    }

    // Returns the number of indices that do not match -> 0 == equal
    private fun wordEqualsInput(word : Word, input : String, direction: RecapDirection) : Int {
        when(direction) {
            RecapDirection.SPANISH_TO_GERMAN -> {
                return compareExpectedToInput(word.Translation, input)
            }
            RecapDirection.GERMAN_TO_SPANISH -> {
                return compareExpectedToInput(word.Vocabulary, input)
            }
            else -> {
                Log.e("RecapViewModel", "Given direction is not clear! Error is program")
            }
        }
        // Should NEVER happen
        return -1
    }

    // Returns the number of indices that do not match -> 0 == equal
    private fun compareExpectedToInput(expected : String, input : String) : Int {
        val expectedTrimmed = expected.trim()
        val inputTrimmed = input.trim()
        if (expectedTrimmed == inputTrimmed)
            return 0
        // Employ custom comparison
        var wrongCharCounter = 0
        for ((i,c) in expectedTrimmed.withIndex()) {
            if (i >= inputTrimmed.length) {
                wrongCharCounter += (expectedTrimmed.length - inputTrimmed.length)
                break
            }
            if (c.lowercase() != inputTrimmed[i].lowercase())
                wrongCharCounter++
        }
        return wrongCharCounter
    }

    fun compareWords() {
        if (toggleForward) {
            toggleForward = false
            if (updateCorrect) {
                updateWordCorrect(currentWord.value!!)
            } else {
                updateWordIncorrect(currentWord.value!!)
            }
            _forward.value = SingleEvent(false)
            updateCorrect = false
            showNextWord()
            return
        }
        // Pressed for the first time, compare given input
        if (inputText.value.isNullOrEmpty()) {
            Log.i("RecapViewModel", "Given input is empty")
            return
        }
        hideKeyboard()
        val result = wordEqualsInput(currentWord.value!!, inputText.value!!, currentDirection)
        if (result == 0) {
            resultText.value = "Correct"
            _result.value = RecapResult.CORRECT
            updateCorrect = true
        } else {
            if (result in 1..2) {
                updateCorrect = true
                resultText.value = "Correct"
                _result.value = RecapResult.COUNT_AS_CORRECT
            } else {
                updateCorrect = false
                resultText.value = "Incorrect"
                _result.value = RecapResult.INCORRECT
            }
            when (currentDirection) {
                RecapDirection.SPANISH_TO_GERMAN -> {
                    if (result in 1..2)
                        hintText.value = "Minor mistake: ${currentWord.value!!.Translation}\n~=\n${inputText.value}"
                    else
                        hintText.value = "Expected: ${currentWord.value!!.Translation}\nGot: ${inputText.value}"
                }
                RecapDirection.GERMAN_TO_SPANISH -> {
                    if (result in 1..2)
                        hintText.value = "Minor mistake: ${currentWord.value!!.Vocabulary}\n~=\n${inputText.value}"
                    else
                        hintText.value = "Expected: ${currentWord.value!!.Vocabulary}\nGot: ${inputText.value}"
                }
                else -> {
                    Log.i("RecapViewModel", "Incorrect state, this direction is not allowed")
                }
            }
        }
        toggleForward = true
        _forward.value = SingleEvent(true)
    }

    private fun showNextWord() {
        // Reset helping text etc.
        _result.value = RecapResult.NONE
        val next = recapList.getNextWord()
        if (next == null) {
            // TODO: Maybe add some recap overview?
            // Nothing more to show, navigate to home I guess
            navigateToRecapStart()
            return
        }
        currentWord.value = next!!
        inputText.value = ""
        hintText.value = ""
        // Decide what to show
        when(_direction) {
            RecapDirection.BOTH -> {
                // Decide which direction to use
                val direction = Random.nextInt(0, 2)
                if (direction == 0) {
                    Log.i("RecapViewModel", "Direction is German to Spanish")
                    showText.value = next.Translation
                    currentDirection = RecapDirection.GERMAN_TO_SPANISH
                } else {
                    Log.i("RecapViewModel", "Direction is Spanish to German")
                    showText.value = next.Vocabulary
                    currentDirection = RecapDirection.SPANISH_TO_GERMAN
                }
            }
            RecapDirection.GERMAN_TO_SPANISH -> {
                showText.value = next.Translation
            }
            RecapDirection.SPANISH_TO_GERMAN -> {
                showText.value = next.Vocabulary
            }
        }
    }

    private fun hideKeyboard() {
        _hideKeyboard.value = true
    }

    fun keyboardHidden() {
        _hideKeyboard.value = false
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