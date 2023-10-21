package com.cloudsheeptech.vocabulary.data

import kotlinx.serialization.Serializable

@Serializable
data class WordV1(
    val ID : Int,
    val Vocabulary : String,
    val Translation : String,
    var Confidence : Confidence,
    var Repeat : Int = 0
)
