package com.cloudsheeptech.vocabulary.data

enum class Confidence {
    PERFECT,    // Bigger 95 - just repeat long term
    GOOD,       // Bigger 75 - not perfect yet, repeat regularly to make them stick
    BAD,        // Bigger 50 - more or less random, only a few per lesson but focused until good
    POOR,       // Less 30 - often mistakes, very hard, should be included in every lesson
    NEW         // 0 - should be added if all the others are not too bad, new words
}