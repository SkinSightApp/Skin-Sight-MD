package com.dicoding.skinsight.models.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.skinsight.databinding.ItemDrugBinding
import com.dicoding.skinsight.networking.response.CatalogProduct
import kotlin.random.Random

class DrugAdapter : ListAdapter<CatalogProduct, DrugAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDrugBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class MyViewHolder(private val binding: ItemDrugBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: CatalogProduct) {
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(story)
            }

            val maxLength = 25
            val truncatedName = if (story.name.length > maxLength) {
                story.name.substring(0, maxLength) + "..."
            } else {
                story.name
            }

            val color = generateRandomPastelColor()
            binding.circleView.background.setTint(color)

            binding.tvName.text = truncatedName
        }
    }
    private fun generateRandomPastelColor(): Int {
        val random = Random.Default
        val baseColor = 150 // Base color value to ensure brightness

        val red = baseColor + random.nextInt(106) // 150 to 255
        val green = baseColor + random.nextInt(106) // 150 to 255
        val blue = baseColor + random.nextInt(106) // 150 to 255

        return Color.rgb(red, green, blue)
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CatalogProduct>() {
            override fun areItemsTheSame(oldItem: CatalogProduct, newItem: CatalogProduct): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CatalogProduct, newItem: CatalogProduct): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: CatalogProduct)
    }

}
