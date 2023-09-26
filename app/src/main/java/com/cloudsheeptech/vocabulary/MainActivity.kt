package com.cloudsheeptech.vocabulary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cloudsheeptech.vocabulary.databinding.ActivityMainBinding
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.lang.Exception
import java.net.URL
import kotlin.concurrent.thread

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
            withContext(Dispatchers.IO) {
                val client = HttpClient()
                val response : HttpResponse = client.get("http://10.0.2.2:50000/words")
                println(response)
                println(response.bodyAsText(Charsets.UTF_8))
                client.close()
            }
        }
    }
}