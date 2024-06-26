package com.cloudsheeptech.vocabulary.datastructures

import android.util.Log
import com.cloudsheeptech.vocabulary.data.Confidence
import com.cloudsheeptech.vocabulary.data.Word
import java.util.Stack
import kotlin.math.ceil
import kotlin.math.floor

class LearningStack {

    private val learningStack = mutableListOf<Word>()
    private val sortedItems = mutableListOf<Word>()
    private val itemMap = HashMap<Confidence, Stack<Word>>(5)

    fun addAll(items : List<Word>) {
        val sorted = items.sortedDescending()
        println("Sorted: $sorted")
        sortedItems.addAll(sorted)
        initMap(items)
        Log.i("LearningStack", "Added ${sortedItems.size} items")
    }

    private fun initMap(items: List<Word>) {
        itemMap.clear()
        for (item in items) {
            if (itemMap[Confidence.convertIntToConfidence(item.Confidence)] == null)
                itemMap[Confidence.convertIntToConfidence(item.Confidence)] = Stack()
            itemMap[Confidence.convertIntToConfidence(item.Confidence)]!!.add(item)
        }
    }

    private fun searchItemWithConfidence(confidence: Confidence) : Word? {
        if (itemMap[confidence] == null || itemMap[confidence]!!.isEmpty())
            return null
        return itemMap[confidence]!!.removeFirst()
    }

    fun selectItems(selectCount : Int) : Int {
        learningStack.clear()
        // Make sure to always include as much words as possible
        if (sortedItems.size < selectCount) {
            learningStack.addAll(sortedItems)
            learningStack.shuffle()
            return learningStack.size
        }
        // Take the share according to the given constants
        val perf = floor((LearningStackDistribution.PERFECT_SHARE / 100.0) * selectCount).toInt()
        val good = floor((LearningStackDistribution.GOOD_SHARE / 100.0) * selectCount).toInt()
        var bad = ceil((LearningStackDistribution.BAD_SHARE / 100.0) * selectCount).toInt()
        var poor = ceil((LearningStackDistribution.POOR_SHARE / 100.0) * selectCount).toInt()
        var new = ceil((LearningStackDistribution.NEW_SHARE / 100.0) * selectCount).toInt()
        val total = perf + good + bad + poor + new
        if (total > selectCount) {
            // From top to bottom remove -1 (the ceiling) until we fit
            var difference = total - selectCount
            println("Selected $difference too much")
            if (difference > 2) {
                new -= 1
            }
            if (difference > 1) {
                poor -= 1
            }
            if (difference > 0) {
                bad -= 1
            }
        }
        println("Distribution wants:\nperf: $perf, good: $good, bad: $bad, poor: $poor, new: $new")
        val shareList = mutableListOf(perf, good, bad, poor, new)
        val searchOrder = mutableListOf(Confidence.PERFECT, Confidence.GOOD, Confidence.BAD, Confidence.POOR, Confidence.NEW)
        var searchedConfidence : Confidence
        var missingItems = 0
        for ((idx,share) in shareList.withIndex()) {
            searchedConfidence = searchOrder[idx]
            val currentShare = share + missingItems
            missingItems = 0
            for (id in 0 until currentShare) {
                val word = searchItemWithConfidence(searchedConfidence)
                if (word == null) {
                    missingItems = currentShare - id
                    break
                }
                learningStack.add(word)
            }
        }
        Log.i( "LearningStack", "Selected (${learningStack.size}): $learningStack")
        Log.i("LearningStack", "Missing total of $missingItems items")
        learningStack.shuffle()
        return learningStack.size
    }

    fun getNextWord() : Word? {
        return learningStack.removeFirstOrNull()
    }

    fun isEmpty() : Boolean {
        return learningStack.size == 0
    }

    fun isNotEmpty() : Boolean {
        return !isEmpty()
    }

    override fun toString() : String {
        val listString = StringBuilder()
        for (word in learningStack) {
            listString.append("$word, ")
        }
        return "[$listString]"
    }

}