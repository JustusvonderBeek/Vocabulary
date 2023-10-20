package com.cloudsheeptech.vocabulary

import com.cloudsheeptech.vocabulary.data.Confidence
import com.cloudsheeptech.vocabulary.data.LearnWord
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.datastructures.LearningStack
import org.junit.Assert
import org.junit.Test

class LearningStackTest {

    @Test
    fun correctInsertion() {
        val wordlist = mutableListOf<LearnWord>()
        val word = Word(0, "Wort 1", "Wort 1")
        val word1 = LearnWord(word, Confidence.NEW, 0)
        wordlist.add(word1)
        val word2 = LearnWord(word, Confidence.BAD, 0)
        wordlist.add(word2)
        val word3 = LearnWord(word, Confidence.PERFECT, 0)
        wordlist.add(word3)
        wordlist.add(word3)
        println("Wordlist: $wordlist")
        val stack = LearningStack()
        stack.addAll(wordlist)
        println(stack)
        var length = stack.selectItems(10)
        Assert.assertEquals(4, length)
        length = stack.selectItems(3)
        Assert.assertEquals(3, length)
    }

    fun testTakeOut() {

    }

}