package com.cloudsheeptech.vocabulary.datastructures

/*
* This class contains the share in percent that each group is included in the final
* selection of words to learn. In case one group does not have sufficient numbers
* of words, the next lower group gets the share.
 */
object LearningStackDistribution {
    const val PERFECT_SHARE = 5
    const val GOOD_SHARE = 15
    const val BAD_SHARE = 30
    const val POOR_SHARE = 35
    const val NEW_SHARE = 15
}