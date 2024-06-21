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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.skinsight.R
import com.dicoding.skinsight.activities.login.dataStore
import com.dicoding.skinsight.databinding.ActivityResultBinding
import com.dicoding.skinsight.models.MainViewModel
import com.dicoding.skinsight.models.adapter.DrugAdapter
import com.dicoding.skinsight.models.login.LoginViewModel
import com.dicoding.skinsight.models.login.LoginViewModelFactory
import com.dicoding.skinsight.networking.response.CatalogProduct
import com.dicoding.skinsight.preferences.UserPreference
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.util.Linkify
import android.widget.LinearLayout
import android.widget.TextView

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private val preferences = UserPreference.getInstance(dataStore)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setIntentImage()
        setupPrediction()

    }

    private fun setupPrediction() {
        val prediction = intent.getStringExtra(EXTRA_PREDICTION)
        binding.tvPrediction.text = prediction

        val loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(preferences))[LoginViewModel::class.java]
        loginViewModel.getToken().observe(this) {
            mainViewModel.getProducts(it)
        }
        mainViewModel.isLoadingPredict.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        when (prediction) {
            "Redness" -> {
                mainViewModel.rednessCatalog.observe(this) {
                    setupRecyclerView(it)
                }
            }
            "Acne" -> {
                mainViewModel.acneCatalog.observe(this) {
                    setupRecyclerView(it)
                }
            }
            "Blackhead" -> {
                mainViewModel.blackheadCatalog.observe(this) {
                    setupRecyclerView(it)
                }
            }
        }
    }

    private fun setupRecyclerView(classes: List<CatalogProduct>) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvProducts.layoutManager = layoutManager
        val adapter = DrugAdapter()
        adapter.submitList(classes)
        binding.rvProducts.adapter = adapter

        adapter.setOnItemClickCallback(object : DrugAdapter.OnItemClickCallback {
            override fun onItemClicked(product: CatalogProduct) {
                showDialog(product)
            }
        })
    }

    private fun showDialog(product: CatalogProduct) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Product Details")

        val message = buildString {
            append("Product Name: ")
            append(product.name)
            append("\n\nBrand: ")
            append(product.brand)
            append("\n\nPrice: ")
            append(product.price)
            append("\n\nRating: ")
            append(product.rating)
            append("\n\nUrl: ")
        }

        val spannableMessage = SpannableString(message + "Click here")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(product.url))
                startActivity(browserIntent)
            }
        }
        spannableMessage.setSpan(
            clickableSpan,
            message.length,
            message.length + 10,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val textView = TextView(this).apply {
            text = spannableMessage
            movementMethod = LinkMovementMethod.getInstance()
            setPadding(20, 20, 20, 20)
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            addView(textView)
        }

        builder.setView(layout)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(getRoundedBackground())
        dialog.show()
    }

    private fun getRoundedBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 30f
            setColor(android.graphics.Color.WHITE)
        }
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