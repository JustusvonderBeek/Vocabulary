package com.cloudsheeptech.vocabulary.data

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val ID : Int,
    val Vocabulary : String,
    val Translation : String,
    val Confidence : Confidence = com.cloudsheeptech.vocabulary.data.Confidence.NEW,
    val Repeat : Int = 0
) : Comparable<Word> {
    override fun compareTo(other: Word): Int {
        return if (this.Confidence.ordinal != other.Confidence.ordinal)
            this.Confidence.ordinal - other.Confidence.ordinal
        else
            this.Repeat - other.Repeat
    }
}
