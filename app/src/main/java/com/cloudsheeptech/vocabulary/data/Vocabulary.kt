package com.cloudsheeptech.vocabulary.data

import android.os.Environment
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Request
import java.io.File
import kotlin.Exception

class Vocabulary(private val vocabularyLocation : File) {

    private val baseUrl = "https://vocabulary.cloudsheeptech.com:50002/"
//    private val baseUrl = "https://10.0.2.2:50002/"
    private var _vocabulary = mutableListOf<Word>()

    val vocabulary : List<Word>
        get() = _vocabulary

    private lateinit var client : HttpClient

    init {
        loadVocabularyFromDisk()
    }

    private fun loadVocabularyFromDisk() {
        try {
            val file = vocabularyLocation
            val reader = file.reader(Charsets.UTF_8)
            val fileContent = reader.readText()
            reader.close()
            val wordList = Json.decodeFromString<List<Word>>(fileContent)
            Log.i("Vocabulary", "Loaded vocabulary with ${wordList.size} words from disk")
            _vocabulary.addAll(wordList)
        } catch (ex : Exception) {
            Log.i("Vocabulary", "Failed to load vocabulary from disk: $ex")
        }
    }

    private suspend fun storeVocabularyToDisk() {
        withContext(Dispatchers.IO) {
            try {
                val file = vocabularyLocation
                val writer = file.writer(Charsets.UTF_8)
                val stringVocab = Json.encodeToString(_vocabulary)
                writer.write(stringVocab)
                writer.close()
                Log.i("Vocabulary", "Stored vocabulary to disk")
            } catch (ex : Exception) {
                Log.i("Vocabulary", "Failed to write vocabulary to disk: $ex")
            }
        }
    }

    private suspend fun initClient() {
        withContext(Dispatchers.IO) {
            client = HttpClient(OkHttp) {
                engine {
                    addInterceptor(AuthenticationInterceptor("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdXRob3JpemVkIjp0cnVlLCJ1c2VySWQiOiJsZXJuZXIifQ.Jg2RA1ctZVb77s8laIEvDmBs0pCu4M-0Mabozqlwnqd2skBYsEVXK0wNJmN8GIiGTzc4JiJt6FzwKkkOTl6EHQ"))
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
//                val getRequest = Request.Builder().url(baseUrl+"words").addHeader("Authorization", credentials)
//                val response : HttpResponse = client.get { getRequest.get() }
                val response : HttpResponse = client.get(baseUrl + "words")
                println("Body:\n${response.bodyAsText(Charsets.UTF_8)}")
                val bdy = response.body<List<Word>>()
                _vocabulary.clear()
                _vocabulary.addAll(bdy)
                storeVocabularyToDisk()
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
                val response : HttpResponse = client.get(baseUrl + "words/$id")
                println("Body:\n${response.bodyAsText()}")
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to get item:\n$ex")
            }
        }
    }

    suspend fun postVocabulary(vocab : String, translation : String) {
        val word = Word(vocabulary.size, vocab, translation)
        postVocabularyItem(word)
    }

    suspend fun postVocabularyItem(word: Word) {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init)
                initClient()
            try {
                val rawWord = Json.encodeToString(word)
                val response : HttpResponse = client.post(baseUrl + "words") {
                    setBody(rawWord)
                }
                if (response.status != HttpStatusCode.Created) {
                    Log.e("Vocabulary", "Creation of vocabulary not successful")
                }
                return@withContext
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to post item:\n$ex")
            }
        }
    }

    suspend fun modifyVocabularyItem(word: Word) {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init)
                initClient()
            try {
                Log.i("Vocabulary", "Got word for modification: $word")
                val rawWord = Json.encodeToString(word)
                val response : HttpResponse = client.post(baseUrl + "words/" + word.ID) {
                    setBody(rawWord)
                }
                if (response.status != HttpStatusCode.Created) {
                    Log.e("Vocabulary", "Modification of vocabulary not successful")
                } else {
                    val bdy = response.body<List<Word>>()
                    _vocabulary.clear()
                    _vocabulary.addAll(bdy)
                    storeVocabularyToDisk()
                }
                return@withContext
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
                if (vocabulary.isEmpty()) {
                    Log.i("Vocabulary", "Own vocabulary is empty. Cannot remove")
                    return@withContext
                }
                if (id >= vocabulary.size || id < 0) {
                    Log.e("Vocabulary", "Given index $id is larger than vocabulary size ${vocabulary.size} or invalid")
                    return@withContext
                }
                val removeWord = vocabulary[id]
                val rawRemoveWord = Json.encodeToString(removeWord)
                val response : HttpResponse = client.delete(baseUrl + "words/$id") {
                    setBody(rawRemoveWord)
                }
                if (response.status != HttpStatusCode.OK) {
                    Log.e("Vocabulary", "Item not successfully removed")
                }
                withContext(Dispatchers.Main) {
                    val decoded = Json.decodeFromString<MutableList<Word>>(response.bodyAsText(Charsets.UTF_8))
                    _vocabulary = decoded
                }
                return@withContext
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to remove item:\n$ex")
            }
        }
    }
}