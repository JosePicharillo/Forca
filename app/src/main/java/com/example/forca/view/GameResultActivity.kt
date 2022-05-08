package com.example.forca.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.forca.databinding.ActivityGameResultBinding

class GameResultActivity : AppCompatActivity() {

    private val activityResultBinding: ActivityGameResultBinding by lazy {
        ActivityGameResultBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityResultBinding.root)

        val correctAnswers = intent.getStringArrayListExtra("correctAnswers")
        val wrongAnswers = intent.getStringArrayListExtra("wrongAnswers")

        /**
         * Lista de palavras corretas
         */
        val acertosStringBuffer = StringBuffer()
        for (index in 0 until (correctAnswers?.size ?: 0)) {
            acertosStringBuffer.append("- " + (correctAnswers?.get(index) ?: "") + "\n")
        }

        /**
         * Lista de palavras erradas
         */
        val errosStringBuffer = StringBuffer()
        for (index in 0 until (wrongAnswers?.size ?: 0)) {
            errosStringBuffer.append("- " + (wrongAnswers?.get(index) ?: "") + "\n")
        }

        activityResultBinding.txtAcertos?.text = "${correctAnswers?.size} Palavras Corretas:"
        activityResultBinding.txtErros?.text = "${wrongAnswers?.size} Palavras Erradas:"
        activityResultBinding.txtTotalWords?.text = "${(correctAnswers?.size ?: 0) + (wrongAnswers?.size ?: 0)} Rodadas"
        activityResultBinding.acertos?.text = acertosStringBuffer.toString()
        activityResultBinding.erros?.text = errosStringBuffer.toString()

        /**
         * Ações Botões
         */
        activityResultBinding.btnReturn?.setOnClickListener {
            finish()
        }
        activityResultBinding.btnNext?.setOnClickListener {
            val intent = Intent(this, GamePlayActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}