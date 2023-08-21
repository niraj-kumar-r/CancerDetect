package com.example.trial2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.trial2.databinding.ActivityMainBinding
import com.example.trial2.ml.CancerDetect
import org.tensorflow.lite.DataType
import org.tensorflow.lite.schema.ResizeBilinearOptions
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST = 101
    private val GALLERY_PERMISSION_REQUEST = 102
    private val CAMERA_CAPTURE_REQUEST = 103
    private var selectedImageBitmap: Bitmap? = null
    private var capturedImageBitmap: Bitmap? = null
    private lateinit var predictBtn: Button

    private lateinit var binding: ActivityMainBinding

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                capturedImageBitmap = data?.extras?.get("data") as? Bitmap
                displayImage(capturedImageBitmap)
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CAPTURE_REQUEST || requestCode == GALLERY_PERMISSION_REQUEST) {
            if (resultCode == RESULT_OK) {
                capturedImageBitmap = data?.extras?.get("data") as? Bitmap
                displayImage(capturedImageBitmap)
            }
        }
    }


    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImage = data?.data // Uri of the selected image
                selectedImageBitmap = getBitmapFromUri(selectedImage)
                displayImage(selectedImageBitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()


        binding.btnProceed.setOnClickListener(){
            var tensorImage = TensorImage(DataType.UINT8)
            tensorImage.load(capturedImageBitmap)

            tensorImage = imageProcessor.process(tensorImage)

            val model = CancerDetect.newInstance(this)

            val inputfeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            inputfeature0.loadBuffer(tensorImage.buffer)

            val outputs = model.process(inputfeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            model.close()

        }

        binding.btnCapture.setOnClickListener { checkCameraPermission() }
        binding.btnUpload.setOnClickListener { checkGalleryPermission() }

        binding.btnEdit.setOnClickListener {
            capturedImageBitmap?.let { bitmap ->
                // Convert capturedImageBitmap to a Uri
                val capturedImageUri = saveBitmapToTempUri(bitmap)
                // Launch built-in image crop activity
                val cropIntent = getCropImageIntent(capturedImageUri)
                cropActivityResultLauncher.launch(cropIntent)
            }
        }

    }

    private fun getCropImageIntent(sourceUri: Uri): Intent {
        val outputUri = getTempUri() // Temporary Uri for the cropped image
        val cropIntent = Intent("com.android.camera.action.CROP")
        cropIntent.setDataAndType(sourceUri, "image/*")
        cropIntent.putExtra("crop", "true")
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        cropIntent.putExtra("outputX", 256) // Desired output width
        cropIntent.putExtra("outputY", 256) // Desired output height
        cropIntent.putExtra("return-data", true)
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri) // Set the output Uri
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission for Uri
        return cropIntent
    }

    private fun getTempUri(): Uri {
        val cacheDir = externalCacheDir ?: cacheDir
        val tempFile = File.createTempFile("crop_temp", ".jpg", cacheDir)
        return FileProvider.getUriForFile(this, "${packageName}.provider", tempFile)
    }



    private val cropActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val croppedImageUri: Uri? = data?.data
                if (croppedImageUri != null) {
                    // Load the cropped image and display it
                    val croppedBitmap = getBitmapFromUri(croppedImageUri)
                    displayImage(croppedBitmap)
                }
            }
        }


    private fun saveBitmapToTempUri(bitmap: Bitmap?): Uri {
        val cacheDir = externalCacheDir ?: cacheDir
        val tempFile = File.createTempFile("capture_temp", ".jpg", cacheDir)
        val outputStream = FileOutputStream(tempFile)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
        return FileProvider.getUriForFile(this, "${packageName}.provider", tempFile)
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
            openGallery()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(intent)
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
