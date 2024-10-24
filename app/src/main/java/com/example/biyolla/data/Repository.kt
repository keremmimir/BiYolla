package com.example.biyolla.data

import com.example.biyolla.model.Cart
import com.example.biyolla.model.Food

class Repository(private val dataSource: DataSource) {

    suspend fun getFood(): List<Food> = dataSource.getFood()

    suspend fun addCart(
        foodName: String,
        foodImageName: String,
        foodPrice: Int,
        foodOrderQuantity: Int,
        userName: String
    ) = dataSource.addFood(foodName, foodImageName, foodPrice, foodOrderQuantity, userName)

    suspend fun getCart(userName: String): List<Cart> = dataSource.getCart(userName)

    suspend fun deleteCart(cartFoodId: Int,userName: String) = dataSource.deleteCart(cartFoodId, userName)
}