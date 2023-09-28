package com.cloudsheeptech.vocabulary

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cloudsheeptech.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestVocabulary()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun requestVocabulary() {
        Log.i("MainActivity", "Request started...")
        val job = Job()
        val scope = CoroutineScope(Dispatchers.Main + job)
        scope.launch {
            val vocab = Vocabulary()
            vocab.updateVocabulary()
            vocab.postVocabulary()
            vocab.getVocabularyItem(10)
        }
    }
}