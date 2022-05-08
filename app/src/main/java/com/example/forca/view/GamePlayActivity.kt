package com.example.forca.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.forca.databinding.ActivityPlayGameBinding
import com.example.forca.viewModel.GameViewModel
import java.text.Normalizer

class GamePlayActivity : AppCompatActivity() {

    private var teclado = false
    private var word: String = ""
    private var win = false
    private lateinit var viewModel: GameViewModel

    private val activityGameBinding: ActivityPlayGameBinding by lazy {
        ActivityPlayGameBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityGameBinding.root)

        viewModel = ViewModelProvider
            .AndroidViewModelFactory(this.application)
            .create(GameViewModel::class.java)

        activityGameBinding.btnNext?.setOnClickListener {
            viewModel.finishRound(win)
        }

        btnLetters()
        startGamePlay()
        obsserveWord()
        observeIdentifiers()
        getTotalRounds()
        observeCurrentRound()
        observeRounds()
        observeGameFinished()
    }

    private fun startGamePlay() {
        viewModel.startGamePlay()
    }

    private fun CharSequence.unaccent(): String {
        val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

    /**
     * Verifica a tentativa (Letra) selecionada
     */
    @SuppressLint("SetTextI18n")
    fun attempt(key: String) {
        viewModel.attempt(key)
        val stringBuilder = StringBuilder()
        stringBuilder.append(activityGameBinding.txtLetras?.text)
        if (activityGameBinding.txtLetras?.text?.length!! > 0) {
            stringBuilder.append(" - ")
        }
        stringBuilder.append(key)

        if (word.uppercase().contains(key.uppercase())) {
            Toast.makeText(this, "Alternativa correta", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Alternativa incorreta", Toast.LENGTH_SHORT).show()
        }

        for (index in word.indices) {
            val guessingWord = activityGameBinding.txtCamposPalavra?.text.toString()
            if (word[index].toString().unaccent().uppercase() == key.uppercase()) {
                var before = " "
                var after = ""
                if (index > 0) {
                    before = guessingWord.substring(0, index * 2 + 1)
                }
                if (index < word.length - 1) {
                    after = guessingWord.substring((index + 1) * 2)
                }

                activityGameBinding.txtCamposPalavra?.text = "${before}${key}${after}"
                if (!activityGameBinding.txtCamposPalavra?.text?.contains("_")!!) {
                    activityGameBinding.btnLinear?.visibility = View.VISIBLE
                    win = true
                    Toast.makeText(this, "Você Acertou", Toast.LENGTH_SHORT).show()
                }
            }
        }

        activityGameBinding.txtLetras!!.text = stringBuilder.toString()
    }

    private fun obsserveWord() {
        viewModel.wordLiveData.observe(this) { updatedWord ->
            teclado = true
            word = updatedWord.word
            val stringBuilder = StringBuilder()

            for (index in 0 until updatedWord.letters) {
                stringBuilder.append(" _")
            }
            runOnUiThread {
                activityGameBinding.txtCamposPalavra?.text = stringBuilder.toString()
                activityGameBinding.txtLetras?.text = ""
                activityGameBinding.btnLinear?.visibility = View.GONE
                win = false
                enabledAllKeyboardKeys()
            }
        }
    }

    private fun observeIdentifiers() {
        viewModel.identifiersLiveData.observe(this) {
            viewModel.randomIdentifier()
            viewModel.nextRound()
        }
    }

    private fun getTotalRounds() {
        val total = viewModel.getRounds()
        activityGameBinding.txtTotalRounds?.text = total.toString()
    }

    @SuppressLint("SetTextI18n")
    fun observeCurrentRound() {
        viewModel.roundLiveData.observe(this) { currentRound ->
            runOnUiThread {
                activityGameBinding.txtRoundsGame?.text = "Rodada $currentRound de "
                val total = activityGameBinding.txtTotalRounds?.text.toString().toInt()
                if (currentRound < total) {
                    activityGameBinding.btnNext?.text = "Próxima rodada"
                } else {
                    activityGameBinding.btnNext?.text = "Ver resultados"
                }
            }
        }
    }

    private fun observeRounds() {
        viewModel.attemptsLiveData.observe(this) { attempts ->
            updateAttempts(attempts)
        }
    }

    private fun updateAttempts(remainingAttempts: Int) {

        activityGameBinding.opCabeca?.paintFlags = 0
        activityGameBinding.opTronco?.paintFlags = 0
        activityGameBinding.opBracoDireito?.paintFlags = 0
        activityGameBinding.opBracoEsquerdo?.paintFlags = 0
        activityGameBinding.opPernaDireita?.paintFlags = 0
        activityGameBinding.opPernaEsquerda?.paintFlags = 0

        if (remainingAttempts < 6) {
            activityGameBinding.opCabeca?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 5) {
            activityGameBinding.opTronco?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 4) {
            activityGameBinding.opBracoDireito?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 3) {
            activityGameBinding.opBracoEsquerdo?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 2) {
            activityGameBinding.opPernaDireita?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if (remainingAttempts < 1) {
            activityGameBinding.opPernaEsquerda?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            activityGameBinding.btnLinear?.visibility = View.VISIBLE
            Toast.makeText(this, "Você perdeu esta rodada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun btnLetters() {
        with(activityGameBinding) {
            btnA?.setOnClickListener { letterSelect("A") }
            btnB?.setOnClickListener { letterSelect("B") }
            btnC?.setOnClickListener { letterSelect("C") }
            btnD?.setOnClickListener { letterSelect("D") }
            btnE?.setOnClickListener { letterSelect("E") }
            btnF?.setOnClickListener { letterSelect("F") }
            btnG?.setOnClickListener { letterSelect("G") }
            btnH?.setOnClickListener { letterSelect("H") }
            btnI?.setOnClickListener { letterSelect("I") }
            btnJ?.setOnClickListener { letterSelect("J") }
            btnK?.setOnClickListener { letterSelect("K") }
            btnL?.setOnClickListener { letterSelect("L") }
            btnM?.setOnClickListener { letterSelect("M") }
            btnN?.setOnClickListener { letterSelect("N") }
            btnO?.setOnClickListener { letterSelect("O") }
            btnP?.setOnClickListener { letterSelect("P") }
            btnQ?.setOnClickListener { letterSelect("Q") }
            btnR?.setOnClickListener { letterSelect("R") }
            btnS?.setOnClickListener { letterSelect("S") }
            btnT?.setOnClickListener { letterSelect("T") }
            btnU?.setOnClickListener { letterSelect("U") }
            btnV?.setOnClickListener { letterSelect("V") }
            btnW?.setOnClickListener { letterSelect("W") }
            btnX?.setOnClickListener { letterSelect("X") }
            btnY?.setOnClickListener { letterSelect("Y") }
            btnZ?.setOnClickListener { letterSelect("Z") }
        }
    }

    private fun enabledAllKeyboardKeys() {
        with(activityGameBinding) {
            btnA?.isEnabled = true
            btnB?.isEnabled = true
            btnC?.isEnabled = true
            btnD?.isEnabled = true
            btnE?.isEnabled = true
            btnF?.isEnabled = true
            btnG?.isEnabled = true
            btnH?.isEnabled = true
            btnI?.isEnabled = true
            btnJ?.isEnabled = true
            btnK?.isEnabled = true
            btnL?.isEnabled = true
            btnM?.isEnabled = true
            btnN?.isEnabled = true
            btnO?.isEnabled = true
            btnP?.isEnabled = true
            btnQ?.isEnabled = true
            btnR?.isEnabled = true
            btnS?.isEnabled = true
            btnT?.isEnabled = true
            btnU?.isEnabled = true
            btnV?.isEnabled = true
            btnW?.isEnabled = true
            btnX?.isEnabled = true
            btnY?.isEnabled = true
            btnZ?.isEnabled = true
        }
    }

    private fun disableLetters(key: String) {
        with(activityGameBinding) {
            when (key) {
                "A" -> btnA?.isEnabled = false
                "B" -> btnB?.isEnabled = false
                "C" -> btnC?.isEnabled = false
                "D" -> btnD?.isEnabled = false
                "E" -> btnE?.isEnabled = false
                "F" -> btnF?.isEnabled = false
                "G" -> btnG?.isEnabled = false
                "H" -> btnH?.isEnabled = false
                "I" -> btnI?.isEnabled = false
                "J" -> btnJ?.isEnabled = false
                "K" -> btnK?.isEnabled = false
                "L" -> btnL?.isEnabled = false
                "M" -> btnM?.isEnabled = false
                "N" -> btnN?.isEnabled = false
                "O" -> btnO?.isEnabled = false
                "P" -> btnP?.isEnabled = false
                "Q" -> btnQ?.isEnabled = false
                "R" -> btnR?.isEnabled = false
                "S" -> btnS?.isEnabled = false
                "T" -> btnT?.isEnabled = false
                "U" -> btnU?.isEnabled = false
                "V" -> btnV?.isEnabled = false
                "W" -> btnW?.isEnabled = false
                "X" -> btnX?.isEnabled = false
                "Y" -> btnY?.isEnabled = false
                "Z" -> btnZ?.isEnabled = false
            }
        }
    }

    private fun letterSelect(key: String) {
        if (teclado) {
            disableLetters(key)
            attempt(key)
        }
    }

    private fun observeGameFinished() {
        viewModel.gameEndLiveData.observe(this) { end ->
            if (end) {
                val intent = Intent(this, GameResultActivity::class.java)
                val wrongAnswers = viewModel.getErrorAnswers()
                val correctAnswers = viewModel.getCorrectAnswers()

                intent.putStringArrayListExtra("correctAnswers", ArrayList(correctAnswers))
                intent.putStringArrayListExtra("wrongAnswers", ArrayList(wrongAnswers))
                startActivity(intent)
                finish()
            }
        }
    }
}