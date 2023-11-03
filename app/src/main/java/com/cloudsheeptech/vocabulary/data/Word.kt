package com.cloudsheeptech.vocabulary.data

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val ID : Int,
    val Vocabulary : String,
    val Translation : String,
    var Confidence : Int = 0,
    var Repeat : Int = 0
) : Comparable<Word> {
    override fun compareTo(other: Word): Int {
        return if (this.Confidence != other.Confidence)
            this.Confidence - other.Confidence
        else
            this.Repeat - other.Repeat
    }

    override fun equals(other: Any?): Boolean {
        if (other is Word) {
            return (this.Vocabulary == other.Vocabulary) && (this.Translation == other.Translation) && (this.ID == other.ID)
        }
        return false
    }

    fun reset() {
        this.Confidence = 0
        this.Repeat = 0
    }
}
