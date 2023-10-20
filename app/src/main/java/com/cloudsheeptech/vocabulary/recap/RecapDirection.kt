package com.cloudsheeptech.vocabulary.recap

enum class RecapDirection(val value : Int) {
    BOTH(0),
    SPANISH_TO_GERMAN(1),
    GERMAN_TO_SPANISH(2);

    companion object {
        fun fromInt(value: Int) = RecapDirection.values().first { it.value == value }
    }

}