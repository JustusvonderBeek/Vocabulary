package com.cloudsheeptech.vocabulary.data

import kotlinx.serialization.Serializable

@Serializable
enum class Confidence(val value : Int) {
    NEW(0),         // 0 - should be added if all the others are not too bad, new words
    POOR(1),       // Less 30 - often mistakes, very hard, should be included in every lesson
    BAD(2),        // Bigger 50 - more or less random, only a few per lesson but focused until good
    GOOD(3),       // Bigger 75 - not perfect yet, repeat regularly to make them stick
    PERFECT(4);    // Bigger 95 - just repeat long term

    companion object {

        private const val NEW_LOWER_LIMIT = 0
        private const val POOR_LOWER_LIMIT = 1
        private const val BAD_LOWER_LIMIT = 30
        private const val GOOD_LOWER_LIMIT = 70
        private const val PERFECT_LOWER_LIMIT = 90
        private const val PERFECT_UPPER_LIMIT = 100

        fun fromInt(value: Int) = Confidence.values().first { it.value == value }

        fun convertIntToConfidence(value : Int) : Confidence {
            return if (value == NEW_LOWER_LIMIT) {
                NEW
            } else if (value in POOR_LOWER_LIMIT..BAD_LOWER_LIMIT) {
                POOR
            } else if (value in (BAD_LOWER_LIMIT + 1)..GOOD_LOWER_LIMIT) {
                BAD
            } else if (value in (GOOD_LOWER_LIMIT + 1)..PERFECT_LOWER_LIMIT) {
                GOOD
            } else if (value in (PERFECT_LOWER_LIMIT + 1)..PERFECT_UPPER_LIMIT) {
                PERFECT
            } else {
                NEW
            }
        }

        fun toInt(value : Confidence) : Int {
            return if (value == NEW) {
                NEW_LOWER_LIMIT
            } else if (value == POOR) {
                POOR_LOWER_LIMIT
            } else if (value == BAD) {
                BAD_LOWER_LIMIT
            } else if (value == GOOD) {
                GOOD_LOWER_LIMIT
            } else if (value == PERFECT) {
                PERFECT_LOWER_LIMIT
            } else {
                NEW_LOWER_LIMIT
            }
        }

    }

}