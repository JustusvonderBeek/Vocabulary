package com.cloudsheeptech.vocabulary.edit

import androidx.lifecycle.ViewModel
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.data.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditViewModel(private val vocabulary: Vocabulary) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    fun editVocabulary() {
        scope.launch {
            val word = Word(ID = 1, "Qualle", "lalala")
            vocabulary.postVocabularyItem(word)
        }
    }

}