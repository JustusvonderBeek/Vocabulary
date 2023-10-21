package com.cloudsheeptech.vocabulary.datastructures

/*
* This class contains the share in percent that each group is included in the final
* selection of words to learn. In case one group does not have sufficient numbers
* of words, the next lower group gets the share.
 */
object LearningStackDistribution {
    const val PERFECT_SHARE = 3
    const val GOOD_SHARE = 7
    const val BAD_SHARE = 10
    const val POOR_SHARE = 40
    const val NEW_SHARE = 40
}