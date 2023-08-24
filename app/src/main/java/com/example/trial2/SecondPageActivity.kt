package com.example.trial2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trial2.databinding.ActivitySecondPageBinding

class SecondPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Move to the MainActivity
            finish() // Close this second page activity
        }



        binding.SkinCancerwikiLinkTextView.setOnClickListener {
            val wikipediaUrl = "https://en.wikipedia.org/wiki/Skin_cancer"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
            startActivity(intent)
        }
        binding.keratosisWikiLinkTextView.setOnClickListener {
            val wikipediaUrl = "https://en.wikipedia.org/wiki/Keratosis"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
            startActivity(intent)
        }

        binding.melanomaWikiLinkTextView.setOnClickListener {
            val wikipediaUrl = "https://en.wikipedia.org/wiki/Melanoma"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
            startActivity(intent)
        }

        binding.lesionWikiLinkTextView.setOnClickListener {
            val wikipediaUrl = "https://en.wikipedia.org/wiki/Basal-cell_carcinoma"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(wikipediaUrl))
            startActivity(intent)
        }

    }
}
