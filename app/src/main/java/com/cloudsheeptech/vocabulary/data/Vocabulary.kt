package com.cloudsheeptech.vocabulary.data

import android.app.Application
import android.os.Environment
import android.os.FileUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.Identity.encode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.Exception

class Vocabulary {

    private val _vocabulary = mutableListOf<Word>()

    val vocabulary : List<Word>
        get() = _vocabulary

    var size = vocabulary.size
    var wordIndex = -1

    private lateinit var client : HttpClient

    fun getNextVocabulary() : Word {
        if (_vocabulary.isEmpty()) {
            return Word(-1, "Null", "Null")
        }
        wordIndex = (wordIndex + 1) % vocabulary.size
        return vocabulary[wordIndex]
    }

    private fun loadVocabularyFromDisk() {

    }

    private fun storeVocabularyToDisk() {
        val file = File(Environment.getDataDirectory(), "vocabulary.json")
        val writer = file.writer(Charsets.UTF_8)
        val stringVocab = Json.encodeToString(_vocabulary)
        writer.write(stringVocab)
        writer.close()
    }

    private suspend fun initClient() {
        withContext(Dispatchers.IO) {
            client = HttpClient(OkHttp) {
                engine {
                    config {
                        hostnameVerifier {
                                _, _ -> true
                        }
                    }
                }
                install(ContentNegotiation) {
                    json()
                }
            }
        }
    }

    suspend fun updateVocabulary() {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init) {
                initClient()
            }
            try {
                val response : HttpResponse = client.get("https://vocabulary.cloudsheeptech.com:50002/words")
                println("Body:\n${response.bodyAsText(Charsets.UTF_8)}")
                val bdy = response.body<List<Word>>()
                _vocabulary.clear()
                _vocabulary.addAll(bdy)
                size = _vocabulary.size
//            Log.i("Vocabulary", "Updated list to $_vocabulary")
                Log.i("Vocabulary", "Update successful")
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to make request:\n$ex")
            }
        }
    }

    suspend fun getVocabularyItem(id : Int) {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init)
                initClient()
            try {
                val response : HttpResponse = client.get("https://vocabulary.cloudsheeptech.com:50002/words/$id")
                println("Body:\n${response.bodyAsText()}")
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to get item:\n$ex")
            }
        }
    }

    suspend fun postVocabulary(vocab : String, translation : String) {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init)
                initClient()
            val word = Word(vocabulary.size, vocab, translation)
            try {
                val rawWord = Json.encodeToString(word)
                val response : HttpResponse = client.post("https://vocabulary.cloudsheeptech.com:50002/words") {
                    setBody(rawWord)
                }
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to post item:\n$ex")
            }
        }
    }

    suspend fun postItemVocabulary(word: Word) {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init)
                initClient()
            try {
                val rawWord = Json.encodeToString(word)
                val response : HttpResponse = client.post("https://vocabulary.cloudsheeptech.com:50002/words/" + word.ID) {
                    setBody(rawWord)
                }
                Log.i("Vocabulary", "Updated word ${word.ID}")
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to post item:\n$ex")
            }
        }
    }

    suspend fun removeVocabularyItem(id : Int) {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init) {
                initClient()
            }
            try {
                val response : HttpResponse = client.delete("https://vocabulary.cloudsheeptech.com:50002/words/$id") {
                    setBody("")
                }
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to remove item:\n$ex")
            }
        }
    }
}