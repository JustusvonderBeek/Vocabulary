package com.cloudsheeptech.vocabulary.data

enum class Confidence(val value : Int) {
    NEW(0),         // 0 - should be added if all the others are not too bad, new words
    POOR(1),       // Less 30 - often mistakes, very hard, should be included in every lesson
    BAD(2),        // Bigger 50 - more or less random, only a few per lesson but focused until good
    GOOD(3),       // Bigger 75 - not perfect yet, repeat regularly to make them stick
    PERFECT(4);    // Bigger 95 - just repeat long term

    companion object {
        fun fromInt(value: Int) = Confidence.values().first { it.value == value }
    }

}