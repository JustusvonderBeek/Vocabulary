package com.cloudsheeptech.vocabulary.data

data class LearnWord(
    var word : Word,
    var confidence: Confidence = Confidence.NEW,
    var repeat: Int = 0,
) : Comparable<LearnWord> {
    override fun compareTo(other: LearnWord): Int {
        return if (this.confidence.ordinal != other.confidence.ordinal)
            this.confidence.ordinal - other.confidence.ordinal
        else
            this.repeat - other.repeat
    }

}