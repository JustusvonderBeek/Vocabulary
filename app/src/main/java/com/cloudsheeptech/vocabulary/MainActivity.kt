package com.cloudsheeptech.vocabulary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cloudsheeptech.vocabulary.databinding.ActivityMainBinding
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun requestVocabulary() {
        Log.i("MainActivity", "Request started...")
        val url = URL("http://localhost:50000/v1/words")
//        val url = URL("https://vocabulary.cloudsheeptech.com/v1/words")
        val urlConnection = url.openConnection()
        try {
            val income = BufferedInputStream(urlConnection.getInputStream())
            val data = income.readBytes()
            val string = data.decodeToString()
            income.close()
            Log.i("MainActivity", "Got data: $string")
        } catch (ex : Exception) {
            Log.i("MainActivity", "Failed to make request")
        }
    }
}