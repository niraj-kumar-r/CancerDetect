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

        return (confidence) >= 0.31
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
        val adjustedConfidence = confidence*1000

        return (adjustedConfidence) >= 9.8
    }

    private fun calculateConfidencePercentage(currentConfidence: Float, threshold: Float, multiplier: Float): Float {
        val adjustedConfidence = currentConfidence*multiplier
        val actualFull = threshold/0.8f
        val actualConfidence = (adjustedConfidence/actualFull)*100f
        return actualConfidence
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        val resultTextView: TextView = findViewById(R.id.resultTextView)
        val resultImageView: ImageView = findViewById(R.id.resultImageView)
        val potentialCancerTextView: TextView = findViewById(R.id.descriptionTextView)
        val backButton: Button = findViewById(R.id.backButton)

        val hasSkinCancer = isMelanomaMalignant()


        // Retrieve the result from your ML model (true for skin cancer, false for no cancer)

        if (hasSkinCancer) {
            resultTextView.text = "Result:"
            resultImageView.setImageResource(R.drawable.skin_cancer_image)

            val confidenceKeratosisMalignant = intent.getFloatExtra("confidenceKeratosisMalignant", 0.0f)
            val confidenceKeratosisBenign = intent.getFloatExtra("confidenceKeratosisBenign", 0.0f)
            val confidenceMelanocyticBenign = intent.getFloatExtra("confidenceMelanocyticBenign", 0.0f)
            val confidenceMelanomaMalignant = intent.getFloatExtra("confidenceMelanomaMalignant", 0.0f)

            val confidenceKeratosisMalignantTextView: TextView = findViewById(R.id.confidenceKeratosisMalignant)
            val adjustedconfidenceKeratosisMalignant = confidenceKeratosisMalignant*10000
            confidenceKeratosisMalignantTextView.text = "Keratosis (Malignant) Confidence: ${calculateConfidencePercentage(confidenceKeratosisMalignant, 40f, 10000f)}%"

            val confidenceKeratosisBenignTextView: TextView = findViewById(R.id.confidenceKeratosisBenign)
            confidenceKeratosisBenignTextView.text = "Keratosis (Benign) Confidence: ${calculateConfidencePercentage(confidenceKeratosisBenign, 40f, 100f)}%"

            val confidenceMelanocyticBenignTextView: TextView = findViewById(R.id.confidenceMelanocyticBenign)
            confidenceMelanocyticBenignTextView.text = "Melanocytic Nevi (Benign) Confidence: ${calculateConfidencePercentage(confidenceMelanocyticBenign, 75f, 100f)}%"

            val confidenceMelanomaMalignantTextView: TextView = findViewById(R.id.confidenceMelanomaMalignant)
            val adjustedconfidenceMelanomaMalignant = confidenceMelanomaMalignant*1000
            confidenceMelanomaMalignantTextView.text = "Melanoma (Malignant) Confidence: ${calculateConfidencePercentage(confidenceMelanomaMalignant, 9.8f, 1000f)}%"

//
//            if (isMelanomaMalignant()) {
//                potentialCancerTextView.text = "Based on the image, you might have: Melanoma skin cancer, referral to the doctor is strongly advised"
//            }

            potentialCancerTextView.text = "Based on the image, you might have: Melanoma skin cancer, consultation to the doctor is strongly advised"

        } else {
            resultTextView.text = "Result:"

            val confidenceKeratosisMalignant = intent.getFloatExtra("confidenceKeratosisMalignant", 0.0f)
            val confidenceKeratosisBenign = intent.getFloatExtra("confidenceKeratosisBenign", 0.0f)
            val confidenceMelanocyticBenign = intent.getFloatExtra("confidenceMelanocyticBenign", 0.0f)
            val confidenceMelanomaMalignant = intent.getFloatExtra("confidenceMelanomaMalignant", 0.0f)

            val confidenceKeratosisMalignantTextView: TextView = findViewById(R.id.confidenceKeratosisMalignant)
            val adjustedconfidenceKeratosisMalignant = confidenceKeratosisMalignant*10000
            confidenceKeratosisMalignantTextView.text = "Keratosis (Malignant) Confidence: ${calculateConfidencePercentage(confidenceKeratosisMalignant, 9.5f, 10000f)}%"

            val confidenceKeratosisBenignTextView: TextView = findViewById(R.id.confidenceKeratosisBenign)
            confidenceKeratosisBenignTextView.text = "Keratosis (Benign) Confidence: ${calculateConfidencePercentage(confidenceKeratosisBenign, 35f, 100f)}%"

            val confidenceMelanocyticBenignTextView: TextView = findViewById(R.id.confidenceMelanocyticBenign)
            confidenceMelanocyticBenignTextView.text = "Melanocytic Nevi (Benign) Confidence: ${calculateConfidencePercentage(confidenceMelanocyticBenign, 95f, 100f)}%"

            val confidenceMelanomaMalignantTextView: TextView = findViewById(R.id.confidenceMelanomaMalignant)
            val adjustedconfidenceMelanomaMalignant = confidenceMelanomaMalignant*1000
            confidenceMelanomaMalignantTextView.text = "Melanoma (Malignant) Confidence: ${calculateConfidencePercentage(confidenceMelanomaMalignant, 15f, 1000f)}%"

//            if (isKeratosisBenign()) {
//                potentialCancerTextView.text = "Based on the image, you might not have Keratosis skin cancer but you can refer to the doctor for further confirmations"
//            }
//            if (isMelanocyticBenign()) {
//                potentialCancerTextView.text = "Based on the image, you might not have Melanoma skin cancer but you can refer to the doctor for further confirmations"
//            }

//            if (isKeratosisMalignant()) {
//                potentialCancerTextView.text = "Based on the image, you might have Keratosis, you can refer the doctor if you still want to"
//            }

            resultImageView.setImageResource(R.drawable.no_skin_cancer_image)
            potentialCancerTextView.text = "Based on the image, you might have Keratosis or other skin lesion, you can consult the doctor if you still want to."
        }


        //The above code says that if the image is not a skin cancer image, then it will display the no skin cancer image and the text "Based on the image, you might not have any skin cancer"


        backButton.setOnClickListener {
            finish() // Go back to the previous activity
        }
    }
}
