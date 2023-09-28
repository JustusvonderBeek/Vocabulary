package com.cloudsheeptech.vocabulary.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.Identity.encode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Vocabulary {

    private val _vocabulary = mutableListOf<Word>()

    val vocabulary : List<Word>
        get() = _vocabulary

    private var wordIndex = 0

    fun getNextVocabulary() : Word {
        if (_vocabulary.isEmpty()) {
            return Word(-1, "Null", "Null")
        }
        return vocabulary[wordIndex++ % vocabulary.size]
    }

    suspend fun updateVocabulary() {
        withContext(Dispatchers.IO) {
            val client = HttpClient() {
                install(ContentNegotiation) {
                    json()
                }
            }
            val response : HttpResponse = client.get("http://10.0.2.2:50000/words")
            println("Body:\n${response.bodyAsText(Charsets.UTF_8)}")
            val bdy = response.body<List<Word>>()
            _vocabulary.addAll(bdy)
//            Log.i("Vocabulary", "Updated list to $_vocabulary")
            client.close()
        }
    }

    suspend fun getVocabularyItem(id : Int) {
        withContext(Dispatchers.IO) {
            val client = HttpClient() {
                install(ContentNegotiation) {
                    json()
                }
            }
            val response : HttpResponse = client.get("http://10.0.2.2:50000/words/$id")
            println("Body:\n${response.bodyAsText()}")
            client.close()
        }
    }

    suspend fun postVocabulary(vocab : String, translation : String) {
        withContext(Dispatchers.IO) {
            val client = HttpClient() {
                install(ContentNegotiation) {
                    json()
                }
            }
            val word = Word(vocabulary.size, vocab, translation)
            val rawWord = Json.encodeToString(word)
            val response : HttpResponse = client.post("http://10.0.2.2:50000/words") {
                setBody(rawWord)
            }
            client.close()
        }
    }
}