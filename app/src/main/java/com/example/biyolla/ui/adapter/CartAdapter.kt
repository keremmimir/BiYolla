package com.example.biyolla.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.biyolla.databinding.CartRowBinding
import com.example.biyolla.model.Cart

class CartAdapter(
    private val context: Context,
    private val  userName: String,
    private val onDeleteToggle: (cartFoodId: Int, userName: String) -> Unit
) : ListAdapter<Cart, CartAdapter.Holder>(DiffCallback()) {

    inner class Holder(val binding: CartRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            with(binding) {
                val imageURL = "http://kasimadalan.pe.hu/yemekler/resimler/${cart.foodImage}"
                Glide.with(context)
                    .load(imageURL)
                    .override(300, 300)
                    .into(imageProduct)

                textProductName.text = cart.foodName
                textPrice.text = "${cart.foodPrice} ₺"
                textPiece.text = cart.foodOrderQuantity
                val totalProductPrice =
                    cart.foodPrice.toString().toInt() * cart.foodOrderQuantity.toString().toInt()
                textTotalProductPrice.text = "$totalProductPrice ₺"

                iconDelete.setOnClickListener {
                    onDeleteToggle(cart.cartFoodId.toString().toInt(), userName)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = CartRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.cartFoodId == newItem.cartFoodId
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }
}
