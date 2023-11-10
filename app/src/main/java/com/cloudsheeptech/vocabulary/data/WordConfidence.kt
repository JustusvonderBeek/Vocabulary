package com.cloudsheeptech.vocabulary.data

import kotlinx.serialization.Serializable

@Serializable
data class WordConfidence(
    val ID : Int,
    val Confidence : Int,
    val Repeat : Int,
)
