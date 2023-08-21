// IntroActivity.kt
package com.example.trial2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trial2.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNextIntro.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Move to the main activity
            finish() // Close this intro activity
        }
    }
}
