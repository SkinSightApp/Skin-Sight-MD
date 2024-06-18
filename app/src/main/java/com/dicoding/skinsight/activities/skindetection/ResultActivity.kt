package com.dicoding.skinsight.activities.skindetection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.skinsight.R
import com.dicoding.skinsight.databinding.ActivityResultBinding
import com.dicoding.skinsight.models.adapter.DrugAdapter
import com.dicoding.skinsight.models.adapter.DrugAdapterDummyData
import com.dicoding.skinsight.networking.response.Product

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setIntentImage()
        setupRecyclerView()
        setupPrediction()

    }
    private fun setupPrediction(){
        val prediction = intent.getStringExtra(EXTRA_PREDICTION)
        val probability = intent.getStringExtra(EXTRA_PROBABILITY)
        binding.tvPrediction.text = prediction
    }
    private fun setupRecyclerView() {
        // Get dummy data
        val dummyData = DrugAdapterDummyData().getDummyData()

        // Initialize RecyclerView
        val layoutManager = LinearLayoutManager(this)
        binding.rvProducts.layoutManager = layoutManager

        // Initialize adapter with dummy data
        val adapter = DrugAdapter()
        adapter.submitList(dummyData)

        // Set adapter to RecyclerView
        binding.rvProducts.adapter = adapter

        // Handle item click
        adapter.setOnItemClickCallback(object : DrugAdapter.OnItemClickCallback {
            override fun onItemClicked(product: Product) {
                // Handle item click action here
                Toast.makeText(this@ResultActivity, "Clicked: ${product.name}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun setIntentImage() {
        val imagePath = intent.getStringExtra(EXTRA_FILE)
        val result = BitmapFactory.decodeFile(imagePath)
        val resizedBitmap = Bitmap.createScaledBitmap(result, 350, 500, true)
        val roundedBitmap = getRoundedCornerBitmap(resizedBitmap, 20)
        binding.ivSkinDetection.setImageBitmap(roundedBitmap)
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

    companion object {
        const val EXTRA_FILE = "extra_file"
        const val EXTRA_PREDICTION = "extra_prediction"
        const val EXTRA_PROBABILITY = "extra_probability"
    }
}