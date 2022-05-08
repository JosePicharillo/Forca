package com.example.forca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.forca.databinding.ActivityMainBinding
import com.example.forca.view.GamePlayActivity
import com.example.forca.view.GameSettingsActivity

class MainActivity : AppCompatActivity() {

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.btnPlayGame?.setOnClickListener {
            val intent: Intent = Intent(this, GamePlayActivity::class.java)
            startActivity(intent)
        }

        activityMainBinding.btnSettings?.setOnClickListener {
            val intent: Intent = Intent(this, GameSettingsActivity::class.java)
            startActivity(intent)
        }
    }
}