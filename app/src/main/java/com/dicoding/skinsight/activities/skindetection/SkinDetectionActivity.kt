package com.dicoding.skinsight.activities.skindetection

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.dicoding.skinsight.databinding.ActivitySkinDetectionBinding
import com.dicoding.skinsight.models.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class SkinDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySkinDetectionBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private var getFile: File? = null
    private lateinit var fileFinal: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySkinDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListener()
    }


    private fun setUpListener() {
        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
    }


    private var anyPhoto = false
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            val resizedBitmap = Bitmap.createScaledBitmap(result, 350, 500, true)
            val roundedBitmap = getRoundedCornerBitmap(resizedBitmap, 20)
            anyPhoto = true
            with(binding) {
                ivSkinDetection.apply {
                    setImageBitmap(roundedBitmap)
                    visibility = View.VISIBLE
                }
                btnCamera.visibility = View.GONE
                btnGallery.visibility = View.GONE
                tvCamera.visibility = View.GONE
                tvGalery.visibility = View.GONE
                btnCheckSkin.visibility = View.VISIBLE
            }
            binding.btnCheckSkin.setOnClickListener {
                mainViewModel.predictionResult.observe(this) {
                    val top2 = it.top_2
                    val top2String = top2.entries.firstOrNull()
                    val probability = top2String?.value ?: 0.0
                    val formattedProbability = "${(probability * 100).toInt()}%"
                    Toast.makeText(this, buildString {
                        append(top2String?.key)
                        append("With ")
                        append(formattedProbability)
                        append(" Probability")
                    }, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SkinDetectionActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FILE, myFile.absolutePath)
                    intent.putExtra(ResultActivity.EXTRA_PREDICTION,top2String?.key)
                    startActivity(intent)
                }

            }

        }
    }

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@SkinDetectionActivity,
                "com.dicoding.skinsight.FileProvider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@SkinDetectionActivity)
            getFile = myFile
            val inputStream = contentResolver.openInputStream(selectedImg)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 350, 500, true)
            val roundedBitmap = getRoundedCornerBitmap(resizedBitmap, 20)

            with(binding) {
                ivSkinDetection.apply {
                    setImageBitmap(roundedBitmap)
                    visibility = View.VISIBLE
                }
                btnCamera.visibility = View.GONE
                btnGallery.visibility = View.GONE
                tvCamera.visibility = View.GONE
                tvGalery.visibility = View.GONE
                btnCheckSkin.visibility = View.VISIBLE
            }
            binding.btnCheckSkin.setOnClickListener {
                mainViewModel.getPrediction(myFile)
                mainViewModel.predictionResult.observe(this) {
                    val top2 = it.top_2
                    val top2String = top2.entries.firstOrNull()
                    val probability = top2String?.value ?: 0.0
                    val formattedProbability = "${(probability * 100).toInt()}%"
                    Toast.makeText(this, buildString {
                        append(top2String?.key)
                        append("With ")
                        append(formattedProbability)
                        append(" Probability")
                    }, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@SkinDetectionActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FILE, myFile.absolutePath)
                    intent.putExtra(ResultActivity.EXTRA_PREDICTION,top2String?.key)
                    startActivity(intent)
                }

            }

        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Int): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        val color = -0xbdbdbe
        val paint = android.graphics.Paint()
        val rect = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = android.graphics.RectF(rect)
        val roundPx = radius.toFloat()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.xfermode =
            android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    companion object {
        const val FILENAME_FORMAT = "MMddyyyy"
    }
}