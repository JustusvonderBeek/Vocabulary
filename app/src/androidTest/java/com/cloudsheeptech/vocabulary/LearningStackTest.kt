package com.cloudsheeptech.vocabulary

import com.cloudsheeptech.vocabulary.data.Confidence
import com.cloudsheeptech.vocabulary.data.Word
import com.cloudsheeptech.vocabulary.datastructures.LearningStack
import org.junit.Assert
import org.junit.Test

class LearningStackTest {

    @Test
    fun correctInsertion() {
        val wordlist = mutableListOf<Word>()
        val word1 = Word(0, "Wort 1", "Wort 1", 0, 0)
        wordlist.add(word1)
        val word2 = Word(1, "Wort 2", "Wort 2", 0, 0)
        wordlist.add(word2)
        val word3 = Word(2, "Wort 3", "Wort 3", 0, 0)
        wordlist.add(word3)
        val word4 = Word(3, "Wort 4", "Wort 4", 0, 0)
        wordlist.add(word4)
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