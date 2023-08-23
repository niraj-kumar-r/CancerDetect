package com.example.trial2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trial2.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private var isDisclaimerAccepted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkBoxAgree.setOnCheckedChangeListener { _, isChecked ->
            isDisclaimerAccepted = isChecked
            updateNextButtonColor()
        }

        binding.btnStart.setOnClickListener {
            if (isDisclaimerAccepted) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent) // Move to the main activity
                finish() // Close this intro activity
            } else {
                // Show a warning message
                Toast.makeText(this, "Please accept the disclaimer before proceeding.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateNextButtonColor() {
        val colorResId = if (isDisclaimerAccepted) R.color.black else R.color.orangered
        binding.btnStart.setBackgroundColor(resources.getColor(colorResId, theme))
    }
}
