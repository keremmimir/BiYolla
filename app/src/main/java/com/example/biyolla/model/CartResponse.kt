package com.example.biyolla.model

import com.google.gson.annotations.SerializedName

data class CartResponse(@SerializedName("sepet_yemekler") val cart: List<Cart>, val success: Int)
