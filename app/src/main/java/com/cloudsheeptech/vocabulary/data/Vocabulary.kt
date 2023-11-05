package com.cloudsheeptech.vocabulary.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.Exception
import kotlin.concurrent.Volatile

class Vocabulary private constructor(private val vocabularyLocation : File) {

    private val baseUrl = "https://vocabulary.cloudsheeptech.com:50002/"
//    private val baseUrl = "https://10.0.2.2:50002/"

    private var _vocabulary = mutableListOf<Word>()
    val wordList : List<Word>
        get() = _vocabulary

    private val jsonHandler = Json {
        encodeDefaults = true
        ignoreUnknownKeys = false
    }

    private val _liveWordList = MutableLiveData<MutableList<Word>>()
    val liveWordList : LiveData<MutableList<Word>> get() = _liveWordList

    private lateinit var client : HttpClient

    companion object {

        @Volatile
        private var instance : Vocabulary? = null

        fun getInstance(file : File) = instance?: synchronized(this) {
            instance ?: Vocabulary(file).also { instance = it }
        }
    }

    init {
        _liveWordList.value = mutableListOf()
        loadVocabularyFromDisk()
    }

    private fun convertListWordV1ToWord(items : List<WordV1>) : List<Word> {
        val convertedList = mutableListOf<Word>()
        for (item in items) {
            convertedList.add(convertWordV1ToWord(item))
        }
        return convertedList
    }

    private fun convertWordV1ToWord(input: WordV1): Word {
        return Word(
            input.ID,
            input.Vocabulary,
            input.Translation,
            Confidence.toInt(input.Confidence),
            input.Repeat
        )
    }

    private fun loadVocabularyFromDisk() {
        try {
            val file = vocabularyLocation
            val reader = file.reader(Charsets.UTF_8)
            val fileContent = reader.readText()
            reader.close()
            var wordList : List<Word>
            try {
                wordList = jsonHandler.decodeFromString<List<Word>>(fileContent)
            } catch (ex : Exception) {
                Log.i("Vocabulary", "The data given is in an old format. Converting...")
                val oldWordList = jsonHandler.decodeFromString<List<WordV1>>(fileContent)
                wordList = convertListWordV1ToWord(oldWordList)
            }
            Log.i("Vocabulary", "Loaded vocabulary with ${wordList.size} words from disk")
            val fixedList = fixWhitespace(wordList)
            _vocabulary.addAll(fixedList)
            _liveWordList.value!!.addAll(fixedList)
        } catch (ex : Exception) {
            Log.i("Vocabulary", "Failed to load vocabulary from disk: $ex")
        }
    }

    private suspend fun storeVocabularyToDisk() {
        withContext(Dispatchers.IO) {
            try {
                val file = vocabularyLocation
                val writer = file.writer(Charsets.UTF_8)
                val stringVocab = jsonHandler.encodeToString(_vocabulary)
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
                    addInterceptor(AuthenticationInterceptor("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdXRob3JpemVkIjp0cnVlLCJ1c2VySWQiOiJsZXJuZXIifQ.LO23i3B2ggDm4_yEO39HV8as5vwTyzCbfrSfR61faXF73UTYqhkj4_gDp58ZEnE-ONNsc_6BX4j7qwok63vwoA"))
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

    private fun vocabularyEqual(old : List<Word>, new : List<Word>) : Boolean {
        if (old.size != new.size)
            return false
        old.forEachIndexed { index, word ->
            if (new[index].Vocabulary != word.Vocabulary || new[index].Translation != word.Translation)
                return false
        }
        return true
    }

    private fun mergeVocabulary(old : List<Word>, new : List<Word>) : MutableList<Word> {
        val updatedList = mutableListOf<Word>()
//        /*
        Log.i("Vocabulary", "Merging list of length ${old.size} and ${new.size}")
        if (old.size == new.size || old.size < new.size) {
            Log.i("Vocabulary", "Old == new size")
            old.forEachIndexed { index, word ->
                if (new[index] != word) {
                    updatedList.add(new[index])
                } else {
                    updatedList.add(word)
                }
            }
        }
        if (old.size < new.size) {
            updatedList.addAll(new.subList(old.size, new.size))
            Log.i("Vocabulary", "Old < new size")
        }
        if (old.size > new.size) {
            Log.i("Vocabulary", "Old > new size")
            new.forEachIndexed { index, word ->
                if (word != old[index]) {
                    updatedList.add(word)
                } else {
                    updatedList.add(old[index])
                }
            }
            updatedList.addAll(old.subList(new.size, old.size))
        }
        return updatedList
//         */
    }

    private fun fixWhitespace(list : List<Word>) : MutableList<Word> {
        val updatedList = mutableListOf<Word>()
        list.forEachIndexed { _, word ->
            updatedList.add(Word(word.ID, word.Vocabulary.trim(), word.Translation.trim(), word.Confidence, word.Repeat))
        }
        return updatedList
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
                if (!vocabularyEqual(_vocabulary, bdy)) {
                    _vocabulary = mergeVocabulary(_vocabulary, bdy)
                    _liveWordList.value!!.clear()
                    _liveWordList.value!!.addAll(_vocabulary)
                    storeVocabularyToDisk()
                } else {
                    Log.i("Vocabulary", "Vocabulary has not changed")
                }
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
        val word = Word(wordList.size, vocab, translation, 0, 0)
        postVocabularyItem(word)
    }

    suspend fun postVocabularyItem(word: Word) {
        val init = this::client.isInitialized
        withContext(Dispatchers.IO) {
            if (!init)
                initClient()
            try {
                val rawWord = jsonHandler.encodeToString(word)
                Log.i("Vocabulary", "Serialized word is $rawWord")
                val response : HttpResponse = client.post(baseUrl + "words") {
                    setBody(rawWord)
                }
                if (response.status != HttpStatusCode.Created) {
                    Log.e("Vocabulary", "Creation of vocabulary not successful")
                }
                val bdy = response.body<List<Word>>()
                _vocabulary = mergeVocabulary(_vocabulary, bdy)
                _liveWordList.value!!.clear()
                _liveWordList.value!!.addAll(_vocabulary)
                storeVocabularyToDisk()
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
                val rawWord = jsonHandler.encodeToString(word)
                val response : HttpResponse = client.post(baseUrl + "words/" + word.ID) {
                    setBody(rawWord)
                }
                if (response.status != HttpStatusCode.Created) {
                    Log.e("Vocabulary", "Modification of vocabulary not successful")
                } else {
                    val bdy = response.body<List<Word>>()
                    _vocabulary = mergeVocabulary(_vocabulary, bdy)
                    _liveWordList.value!!.clear()
                    _liveWordList.value!!.addAll(_vocabulary)
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
                if (wordList.isEmpty()) {
                    Log.i("Vocabulary", "Own vocabulary is empty. Cannot remove")
                    return@withContext
                }
                if (id >= wordList.size || id < 0) {
                    Log.e("Vocabulary", "Given index $id is larger than vocabulary size ${wordList.size} or invalid")
                    return@withContext
                }
                val removeWord = wordList[id]
                val rawRemoveWord = jsonHandler.encodeToString(removeWord)
                val response : HttpResponse = client.delete(baseUrl + "words/$id") {
                    setBody(rawRemoveWord)
                }
                if (response.status != HttpStatusCode.OK) {
                    Log.e("Vocabulary", "Item not successfully removed")
                }
                withContext(Dispatchers.Main) {
                    val decoded = jsonHandler.decodeFromString<MutableList<Word>>(response.bodyAsText(Charsets.UTF_8))
                    _vocabulary = decoded
                    _liveWordList.value = decoded
                }
                return@withContext
            } catch (ex : Exception) {
                Log.e("Vocabulary", "Failed to remove item:\n$ex")
            }
        }
    }

    suspend fun updateCorrectRepeat(word : Word) {
        Log.i("Vocabulary", "Word $word correctly repeated")
        word.Repeat += 1
        word.Confidence = (word.Confidence + 10).coerceAtMost(100)
        _vocabulary.removeAt(word.ID)
        _vocabulary.add(word.ID, word)
        _liveWordList.value!!.clear()
        _liveWordList.value!!.addAll(_vocabulary)
        Log.i("Vocabulary", "Updated word to $word")
        Log.i("Vocabulary", "Updated vocabulary to $wordList")
        withContext(Dispatchers.IO) {
            storeVocabularyToDisk()
        }
    }

    suspend fun updateIncorrectRepeat(word : Word) {
        Log.i("Vocabulary", "Word $word incorrectly repeated")
        word.Repeat += 1
        word.Confidence = (word.Confidence - 5).coerceAtLeast(1)
        _vocabulary.removeAt(word.ID)
        _vocabulary.add(word.ID, word)
        _liveWordList.value!!.clear()
        _liveWordList.value!!.addAll(_vocabulary)
        Log.i("Vocabulary", "Updated word to $word")
        Log.i("Vocabulary", "Updated vocabulary to $wordList")
        withContext(Dispatchers.IO) {
            storeVocabularyToDisk()
        }
    }

    fun length() : Int {
        return wordList.size
    }

}