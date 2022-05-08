package com.example.forca.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.forca.databinding.ActivityGameSettingsBinding
import com.example.forca.viewModel.GameViewModel

class GameSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: GameViewModel

    private val activitySettingsBinding: ActivityGameSettingsBinding by lazy {
        ActivityGameSettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activitySettingsBinding.root)

        viewModel = ViewModelProvider.AndroidViewModelFactory(this.application).create(GameViewModel::class.java)

        componentsView()

        /**
         * Ação do SeekBar de Dificuldade
         */
        activitySettingsBinding.opDifficultySB.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                viewModel.setDifficulty(value)
                runOnUiThread {
                    activitySettingsBinding.txtDifficulty.text = "Nível Dificuldade: $value"
                    activitySettingsBinding.opDifficultySB.progress = value
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //TODO Não Implementado
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //TODO Não Implementado
            }
        })

        /**
         * Ação do SeekBar de Rodadas
         */
        activitySettingsBinding.opRoundsSB.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                viewModel.setTotalRounds(value)
                runOnUiThread {
                    activitySettingsBinding.txtRounds.text = "Número de Rodadas: $value"
                    activitySettingsBinding.opRoundsSB.progress = value
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //TODO Não Implementado
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                //TODO Não Implementado
            }
        })

        /**
         * Ação do Botão "Salvar e Voltar"
         */
        activitySettingsBinding.btnSaveAndReturn.setOnClickListener {
            finish()
        }
    }

    /**
     * Inicializa os componentes da view
     */
    @SuppressLint("SetTextI18n")
    fun componentsView() {
        val difficulty = viewModel.getDifficulty()
        val rounds = viewModel.getRounds()

        activitySettingsBinding.txtRounds.text = "Número de Rodadas: $rounds"
        activitySettingsBinding.opRoundsSB.progress = rounds!!

        activitySettingsBinding.txtDifficulty.text = "Nível Dificuldade: $difficulty"
        activitySettingsBinding.opDifficultySB.progress = difficulty!!

    }
}