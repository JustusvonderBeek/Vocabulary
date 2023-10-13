package com.cloudsheeptech.vocabulary.data

data class LearnWord(
    var word : Word,
    var confidence: Confidence = Confidence.NEW,
    var repeat: Int = 0,
)