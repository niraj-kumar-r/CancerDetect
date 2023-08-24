package com.example.trial2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trial2.databinding.ActivityMainBinding
import com.example.trial2.ml.CancerDetectAccurate
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST = 101
    private val GALLERY_PERMISSION_REQUEST = 102
    private val CAMERA_CAPTURE_REQUEST = 103
    private var capturedImageBitmap: Bitmap? = null
    private var imageUri: Uri? = null
    private lateinit var predictBtn: Button

    private lateinit var binding: ActivityMainBinding

    @Deprecated("This is deprecated")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CAMERA_CAPTURE_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    capturedImageBitmap = data?.extras?.get("data") as? Bitmap
                    displayImage(capturedImageBitmap)
                }
            }

            GALLERY_PERMISSION_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    imageUri = data?.data // Uri of the selected image
                    capturedImageBitmap = getBitmapFromUri(imageUri)
                    displayImage(capturedImageBitmap)
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    result.uri?.let {
                        imageUri = it
                        capturedImageBitmap = getBitmapFromUri(imageUri)
                        displayImage(capturedImageBitmap)
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }
        }
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                capturedImageBitmap = data?.extras?.get("data") as? Bitmap
                displayImage(capturedImageBitmap)

            }
        }


    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri: Uri? ->
                imageUri = uri
                capturedImageBitmap = getBitmapFromUri(imageUri)
                displayImage(capturedImageBitmap)
                launchImageCrop(imageUri!!)
            })

    private val galleryActivityResultLauncherOld =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data // Uri of the selected image
                capturedImageBitmap = getBitmapFromUri(imageUri)
                displayImage(capturedImageBitmap)
                launchImageCrop(imageUri!!)
            }
        }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private fun convertToRGB(bitmap: Bitmap): Bitmap {
        val rgbBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(rgbBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return rgbBitmap
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val imageProcessor = ImageProcessor.Builder()
            .add(NormalizeOp(0f, 255f))
            .build()


        binding.btnProceed.setOnClickListener() {
            if (capturedImageBitmap == null) {
                Toast.makeText(this, "Please upload an image!", Toast.LENGTH_SHORT).show()
            } else {
                var tensorImage = TensorImage(DataType.FLOAT32)
                val postProcessedBitmap =
                    Bitmap.createScaledBitmap(convertToRGB(capturedImageBitmap!!), 28, 28, true)
                tensorImage.load(postProcessedBitmap)
//
                tensorImage = imageProcessor.process(tensorImage)

                val model = CancerDetectAccurate.newInstance(this)
//
                val inputfeature0 =
                    TensorBuffer.createFixedSize(intArrayOf(1, 28, 28, 3), DataType.FLOAT32)
                val byteBuffer: ByteBuffer = tensorImage.buffer

                inputfeature0.loadBuffer(byteBuffer)
//
                val outputs = model.process(inputfeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                val intent = Intent(this, ResultActivity::class.java)

                intent.putExtra("confidenceKeratosisMalignant", outputFeature0.floatArray[0])
                intent.putExtra("confidenceKeratosisBenign", outputFeature0.floatArray[1])
                intent.putExtra("confidenceMelanocyticBenign", outputFeature0.floatArray[2])
                intent.putExtra("confidenceMelanomaMalignant", outputFeature0.floatArray[3])
//
                startActivity(intent) // Move to the main activity
                //finish() // Close this intro activity...
//
                model.close()
            }

        }

        binding.btnCapture.setOnClickListener { checkCameraPermission() }
        binding.btnUpload.setOnClickListener { checkGalleryPermission() }

    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            openCamera()
        }
    }

    private fun checkGalleryPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    /* context = */ this,
                    /* permission = */ Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    GALLERY_PERMISSION_REQUEST
                )
            } else {
                openGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    /* context = */ this,
                    /* permission = */ Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_PERMISSION_REQUEST
                )
            } else {
                openGalleryOld()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        galleryActivityResultLauncher.launch("image/*")
    }

    private fun openGalleryOld() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncherOld.launch(galleryIntent)
    }

    private fun getBitmapFromUri(uri: Uri?): Bitmap? {
        uri?.let {
            val inputStream = contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        }
        return null
    }

    private fun displayImage(bitmap: Bitmap?) {
        if (bitmap != null) {
            val resizedBitmap = resizeBitmap(bitmap, 400, 300)
            binding.imageView.setImageBitmap(resizedBitmap)
        } else {
            // Display a placeholder image if bitmap is null
            binding.imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleFactor = if (width > height) {
            maxWidth.toFloat() / width
        } else {
            maxHeight.toFloat() / height
        }

        val newWidth = (width * scaleFactor).toInt()
        val newHeight = (height * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }
    }

}
