package com.example.biyolla.data

import com.example.biyolla.model.Cart
import com.example.biyolla.model.Food
import com.example.biyolla.network.FoodAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSource(private val foodAPI: FoodAPI) {

    suspend fun getFood(): List<Food> =
        withContext(Dispatchers.IO) {
            return@withContext foodAPI.getFood().food
        }

    suspend fun addFood(
        foodName: String,
        foodImageName: String,
        foodPrice: Int,
        foodOrderQuantity: Int,
        userName: String
    ) {
        foodAPI.addCart(foodName, foodImageName, foodPrice, foodOrderQuantity, userName)
    }

    suspend fun getCart(userName: String): List<Cart> =
        withContext(Dispatchers.IO) {
            return@withContext foodAPI.getCart(userName).cart
        }

    suspend fun deleteCart(cartFoodId: Int,userName: String){
        foodAPI.deleteCart(cartFoodId,userName)
    }
}