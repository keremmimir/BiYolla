package com.example.biyolla.ui.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biyolla.Event
import com.example.biyolla.data.AuthRepository
import com.example.biyolla.data.FavoriteRepository
import com.example.biyolla.data.Repository
import com.example.biyolla.model.Cart
import com.example.biyolla.model.Food
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: Repository,
    private val authRepository: AuthRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val userId = authRepository.getCurrentUser()?.uid
    val favoriteFoods = MutableLiveData<List<Food>>()
    val error = MutableLiveData<Event<String>>()
    private var cartList: List<Cart> = emptyList()
    val userid = MutableLiveData<String?>()
    val filteredItems = MutableLiveData<List<Food>?>()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val foodResult = repository.getFood()

                val favoriteIds =
                    userId?.let { favoriteRepository.getFavoriteIds(it) } ?: emptyList()

                val favorites = foodResult.filter { food ->
                    food.foodId in favoriteIds
                }
                favoriteFoods.value = favorites
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error loading favorites: ${e.message}")
            }
        }
    }

    fun getCart(userName: String) {
        viewModelScope.launch {
            try {
                cartList = emptyList()
                val cartResponse = repository.getCart(userName)
                Log.e("API Detail CArt Response", "${cartResponse}")
                cartList = cartResponse
            } catch (e: Exception) {
                Log.e("API Cart Error", e.message ?: "Unknown error")
            }
        }
    }

    fun addCart(
        foodName: String,
        foodImageName: String,
        foodPrice: Int,
        foodOrderQuantity: Int,
        userName: String
    ) {
        viewModelScope.launch {
            try {
                getCart(userName)
                val productCheck = cartList.find { it.foodName == foodName }
                if (productCheck != null) {
                    val newQuantity =
                        (productCheck.foodOrderQuantity?.toInt() ?: 0) + foodOrderQuantity
                    productCheck.cartFoodId?.let {
                        repository.deleteCart(
                            it.toInt(),
                            userName
                        )
                    }
                    repository.addCart(foodName, foodImageName, foodPrice, newQuantity, userName)
                } else {
                    repository.addCart(
                        foodName,
                        foodImageName,
                        foodPrice,
                        foodOrderQuantity,
                        userName
                    )
                }
                getCart(userName)
            } catch (e: Exception) {
                Log.e("API ADD CART Error", e.message ?: "Unknown error")
            }
        }
    }

    fun getUid() {
        val user = authRepository.getCurrentUser()
        val uuid = user?.uid
        userid.value = uuid
    }

    fun searchItems(query: String) {
        val filteredList = favoriteFoods.value?.filter {
            it.foodName?.contains(query, ignoreCase = true) ?: false
        }
        filteredItems.value = filteredList
    }
}