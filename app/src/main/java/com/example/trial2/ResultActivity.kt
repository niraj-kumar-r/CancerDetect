package com.example.trial2
//
//class MainActivity1 {
//}
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        val resultTextView: TextView = findViewById(R.id.resultTextView)
        val resultImageView: ImageView = findViewById(R.id.resultImageView)
        val potentialCancerTextView: TextView = findViewById(R.id.descriptionTextView)
        val backButton: Button = findViewById(R.id.backButton)

        // Retrieve the result from your ML model (true for skin cancer, false for no cancer)
        val hasSkinCancer = intent.getBooleanExtra("hasSkinCancer", false)

        if (hasSkinCancer) {
            resultTextView.text = "Result: Skin Cancer Detected"
            resultImageView.setImageResource(R.drawable.skin_cancer_image)
            potentialCancerTextView.text = "Based on the image, you might have: // Skin Cancer"

            val confidenceKeratosisMalignant = intent.getFloatExtra("confidenceKeratosisMalignant", 0.0f)
            val confidenceKeratosisBenign = intent.getFloatExtra("confidenceKeratosisBenign", 0.0f)
            val confidenceMelanocyticBenign = intent.getFloatExtra("confidenceMelanocyticBenign", 0.0f)
            val confidenceMelanomaMalignant = intent.getFloatExtra("confidenceMelanomaMalignant", 0.0f)

            val confidenceKeratosisMalignantTextView: TextView = findViewById(R.id.confidenceKeratosisMalignant)
            confidenceKeratosisMalignantTextView.text = "Keratosis (Malignant) Confidence: $confidenceKeratosisMalignant"

            val confidenceKeratosisBenignTextView: TextView = findViewById(R.id.confidenceKeratosisBenign)
            confidenceKeratosisBenignTextView.text = "Keratosis (Benign) Confidence: $confidenceKeratosisBenign"

            val confidenceMelanocyticBenignTextView: TextView = findViewById(R.id.confidenceMelanocyticBenign)
            confidenceMelanocyticBenignTextView.text = "Melanocytic Nevi (Benign) Confidence: $confidenceMelanocyticBenign"

            val confidenceMelanomaMalignantTextView: TextView = findViewById(R.id.confidenceMelanomaMalignant)
            confidenceMelanomaMalignantTextView.text = "Melanoma (Malignant) Confidence: $confidenceMelanomaMalignant"
        } else {
            resultTextView.text = "Result: No Skin Cancer Detected"
            resultImageView.setImageResource(R.drawable.no_skin_cancer_image)
            potentialCancerTextView.text = "Based on the image, you might have: // No Cancer"
        }



        backButton.setOnClickListener {
            finish() // Go back to the previous activity
        }
    }
}
