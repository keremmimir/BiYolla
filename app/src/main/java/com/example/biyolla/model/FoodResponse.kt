package com.example.biyolla.model

import com.google.gson.annotations.SerializedName

data class FoodResponse(@SerializedName("yemekler") val food: List<Food>, val success: Int) {
}