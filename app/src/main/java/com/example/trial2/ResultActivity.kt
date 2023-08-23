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

    private fun isKeratosisMalignant(): Boolean {
        val confidence = intent.getFloatExtra("confidenceKeratosisMalignant", 0.0f)
        return confidence >= 0.69
    }

    private fun isKeratosisBenign(): Boolean {
        val confidence = intent.getFloatExtra("confidenceKeratosisBenign", 0.0f)
        return confidence >= 0.69
    }

    private fun isMelanocyticBenign(): Boolean {
        val confidence = intent.getFloatExtra("confidenceMelanocyticBenign", 0.0f)
        return confidence >= 0.69
    }

    private fun isMelanomaMalignant(): Boolean {
        val confidence = intent.getFloatExtra("confidenceMelanomaMalignant", 0.0f)
        return confidence >= 0.69
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        val resultTextView: TextView = findViewById(R.id.resultTextView)
        val resultImageView: ImageView = findViewById(R.id.resultImageView)
        val potentialCancerTextView: TextView = findViewById(R.id.descriptionTextView)
        val backButton: Button = findViewById(R.id.backButton)

        //val hasSkinCancer = isKeratosisBenign()||isMelanocyticBenign()||isMelanomaMalignant()||isKeratosisMalignant()

        // Retrieve the result from your ML model (true for skin cancer, false for no cancer)

//        if (hasSkinCancer) {
            resultTextView.text = "Result:"
            resultImageView.setImageResource(R.drawable.skin_cancer_image)

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

            if (isKeratosisMalignant()) {
                potentialCancerTextView.text = "Based on the image, you might have: Keratosis skin cancer, referral to the doctor is strongly advised"
            }
            if (isMelanomaMalignant()) {
                potentialCancerTextView.text = "Based on the image, you might have: Melanoma skin cancer, referral to the doctor is strongly advised"
            }
//        } else {
            resultTextView.text = "Result: No Skin Cancer Detected"
            if (isKeratosisBenign()) {
                potentialCancerTextView.text = "Based on the image, you might not have Keratosis skin cancer but you can refer to the doctor for further confirmations"
            }
            if (isMelanocyticBenign()) {
                potentialCancerTextView.text = "Based on the image, you might not have Melanoma skin cancer but you can refer to the doctor for further confirmations"
            }
            resultImageView.setImageResource(R.drawable.no_skin_cancer_image)
            potentialCancerTextView.text = "Based on the image, you might not have any skin cancer"
        //}


        //The above code says that if the image is not a skin cancer image, then it will display the no skin cancer image and the text "Based on the image, you might not have any skin cancer"


        backButton.setOnClickListener {
            finish() // Go back to the previous activity
        }
    }
}
