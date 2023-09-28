package com.cloudsheeptech.data

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val ID : Int,
    val Vocabulary : String,
    val Translation : String
)
