package com.example.biyolla.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.biyolla.databinding.ListRowBinding
import com.example.biyolla.model.Food

class ProductAdapter(
    private val context: Context,
    private val onDetailToggle: (Food) -> Unit,
    private val onCartToggle: (
        foodName: String,
        foodImageName: String,
        foodPrice: Int,
        foodOrderQuantity: Int,
        userName: String
    ) -> Unit,
    private val userName: String
) : ListAdapter<Food, ProductAdapter.Holder>(FoodDiffCallback()) {

    inner class Holder(val binding: ListRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Food) {
            with(binding) {
                val imageURL = "http://kasimadalan.pe.hu/yemekler/resimler/${product.foodImage}"
                Glide.with(context)
                    .load(imageURL)
                    .override(350, 400)
                    .into(imageProduct)

                textProductName.text = product.foodName
                textProductPrice.text = "${product.foodPrice} ₺"

                itemView.setOnClickListener {
                    onDetailToggle(product)
                }
                buttonAdd.setOnClickListener {
                    onCartToggle(
                        product.foodName!!,
                        product.foodImage!!,
                        product.foodPrice?.toInt()!!,
                        1,
                        userName
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

}

class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
    override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem.foodId == newItem.foodId
    }

    override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem == newItem
    }
}