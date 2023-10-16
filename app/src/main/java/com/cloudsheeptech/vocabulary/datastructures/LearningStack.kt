package com.cloudsheeptech.vocabulary.datastructures

import com.cloudsheeptech.vocabulary.data.LearnWord
import com.cloudsheeptech.vocabulary.data.Word

class LearningStack(wordlist : List<Word>) {

    private val learningStack = mutableListOf<LearnWord>()

    init {
        addAllWords(wordlist)
    }

    fun addAllWords(wordlist : List<Word>) {
        for (word in wordlist) {
            val learnWord = LearnWord(word)
            addWord(learnWord)
        }
    }

    fun addWord(word : LearnWord) {
        var idx = 0
        var added = false
        for(lw in learningStack) {
            if (lw.confidence > word.confidence) {
                learningStack.add(idx, word)
                added = true
                break
            }
            idx += 1
        }
        if (!added) {
            appendWord(word)
        }
    }

    fun appendWord(word : LearnWord) {
        learningStack.add(word)
    }

    fun getNextWord() : LearnWord? {
        val word = learningStack.firstOrNull()
        if (word != null)
            learningStack.removeAt(0)
        return word
    }

    fun isEmpty() : Boolean {
        return learningStack.size == 0
    }

    fun isNotEmpty() : Boolean {
        return !isEmpty()
    }

}