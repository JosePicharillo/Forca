package com.example.forca.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.forca.model.GameForcaAPI
import com.example.forca.model.Identifier
import com.example.forca.model.WordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.Normalizer
import java.util.*

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val identifiersLiveData: MutableLiveData<Identifier> = MutableLiveData()
    val wordLiveData: MutableLiveData<WordDao> = MutableLiveData()
    val roundLiveData: MutableLiveData<Int> = MutableLiveData()
    val attemptsLiveData: MutableLiveData<Int> = MutableLiveData()
    val gameEndLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var correctAnswerCounter: MutableList<String> = mutableListOf()
    private var errorAnswerCounter: MutableList<String> = mutableListOf()
    private var difficulty: Int? = getDifficulty()
    private var total: Int? = getRounds()
    private var gameIdentifiers: MutableList<Int> = ArrayList()
    private val coroutine = CoroutineScope(Dispatchers.IO + Job())

    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val forcaApi: GameForcaAPI = retrofit.create(GameForcaAPI::class.java)

    companion object {
        const val BASE_URL = "https://www.nobile.pro.br/forcaws/"
        const val SHARED_PREFERENCES_KEY = "FORCA_SHARED_PREFERENCES_KEY"
        const val TOTAL_ROUNDS_KEY = "TOTAL_ROUNDS_KEY"
        const val TOTAL_ROUNDS_DEFAULT = 1
        const val DIFFICULTY_KEY = "DIFFICULTY_KEY"
        const val DIFFICULTY_DEFAULT = 1
    }

    fun startGamePlay() {
        roundLiveData.postValue(0)
        getIdentifiers(difficulty!!)
        correctAnswerCounter = mutableListOf()
        errorAnswerCounter = mutableListOf()
        gameEndLiveData.postValue(false)
    }

    fun nextRound() {
        val index = gameIdentifiers[roundLiveData.value!!]
        attemptsLiveData.postValue(6)
        getWord(index)
        roundLiveData.postValue(roundLiveData.value!! + 1)
    }

    fun finishRound(ganhouRound: Boolean) {
        if (ganhouRound) {
            correctAnswerCounter.add(wordLiveData.value?.word!!)
        } else {
            errorAnswerCounter.add(wordLiveData.value?.word!!)
        }

        if (roundLiveData.value!! < total!!) {
            nextRound()
        } else {
            gameEndLiveData.postValue(true)
        }
    }

    /**
     * Gerar um identificador aleatório para o jogo
     */
    fun randomIdentifier() {
        val random = Random()
        gameIdentifiers = ArrayList()
        while (gameIdentifiers.size < total!!) {
            val randomIndex = random.nextInt(identifiersLiveData.value!!.words.size - 1)
            val randomIdentifier = identifiersLiveData.value!!.words[randomIndex]
            if (!gameIdentifiers.contains(randomIdentifier)) {
                gameIdentifiers.add(randomIdentifier)
            }
        }
    }

    private fun CharSequence.unaccent(): String {
        val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

    fun attempt(key: String) {
        val word: WordDao = wordLiveData.value!!
        if (word.word.unaccent().uppercase().contains(key.uppercase())) {
            //TODO Não Implementado
        } else {
            val attempts: Int = attemptsLiveData.value!!
            attemptsLiveData.postValue(attempts - 1)
        }
    }

    fun getCorrectAnswers(): MutableList<String> {
        return correctAnswerCounter
    }

    fun getErrorAnswers(): MutableList<String> {
        return errorAnswerCounter
    }

    fun getRounds(): Int? {
        val application = getApplication<Application>()
        val sharedPref = application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return sharedPref?.getInt(TOTAL_ROUNDS_KEY, TOTAL_ROUNDS_DEFAULT)
    }

    fun setTotalRounds(rodadas: Int) {
        val application = getApplication<Application>()
        val sharedPref = application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(TOTAL_ROUNDS_KEY, rodadas)
            apply()
        }
        total = rodadas
    }

    fun getDifficulty(): Int? {
        val application = getApplication<Application>()
        val sharedPref = application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return sharedPref?.getInt(DIFFICULTY_KEY, DIFFICULTY_DEFAULT)
    }

    fun setDifficulty(dif: Int) {
        val application = getApplication<Application>()
        val sharedPref = application.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(DIFFICULTY_KEY, dif)
            apply()
        }
        difficulty = dif
    }

    private fun getIdentifiers(id: Int) {
        coroutine.launch {
            forcaApi.getIdentifier(id).enqueue(object : Callback<Array<Int>> {
                override fun onResponse(
                    call: Call<Array<Int>>,
                    response: Response<Array<Int>>
                ) {
                    val list: Array<Int> = response.body()!!
                    val identifier = Identifier(list)
                    identifiersLiveData.postValue(identifier)
                }

                override fun onFailure(call: Call<Array<Int>>, t: Throwable) {
                    //TODO Não Implementado
                }
            })
        }
    }

    private fun getWord(id: Int) {
        coroutine.launch {
            forcaApi.getWord(id).enqueue(object : Callback<Array<WordDao>> {
                override fun onResponse(
                    call: Call<Array<WordDao>>,
                    response: Response<Array<WordDao>>
                ) {
                    wordLiveData.postValue(response.body()!![0])
                }

                override fun onFailure(call: Call<Array<WordDao>>, t: Throwable) {
                    //TODO Não Implementado
                }
            })
        }
    }

}