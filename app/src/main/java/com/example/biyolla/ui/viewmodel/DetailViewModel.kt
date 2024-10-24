package com.example.biyolla.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biyolla.Event
import com.example.biyolla.data.AuthRepository
import com.example.biyolla.data.FavoriteRepository
import com.example.biyolla.data.Repository
import com.example.biyolla.model.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.E

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    private val authRepository: AuthRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private var cartList: List<Cart> = emptyList()
    val userid = MutableLiveData<String?>()
    val favResult = MutableLiveData<Event<Result<String>?>>()
    val error = MutableLiveData<Event<String>>()
    val favorites = MutableLiveData<Set<String>>()

    fun getCart(userName: String) {
        viewModelScope.launch {
            try {
                cartList = emptyList()
                val cartResponse = repository.getCart(userName)
                Log.e("API Detail Cart Response", "${cartResponse}")
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
            } catch (e: Exception) {
                Log.e("API Add Cart Error", e.message ?: "Unknown error")
            }
        }
    }

    fun getUid() {
        val user = authRepository.getCurrentUser()
        val uuid = user?.uid
        userid.value = uuid
    }

    private fun addFavorite(userId: String, foodId: String) {
        viewModelScope.launch {
            val result = favoriteRepository.addFavorite(userId, foodId)
            if (result.isSuccess) {
                favResult.value = Event(result)
            } else {
                error.value = Event(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    private fun removeFavorite(userId: String, foodId: String) {
        viewModelScope.launch {
            val result = favoriteRepository.removeFavorite(userId, foodId)
            if (result.isSuccess) {
                favResult.value = Event(result)
            } else {
                error.value = Event(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun toogleFavorite(userId: String, foodId: String) {
        viewModelScope.launch {
            val isFavorite = favoriteRepository.isFavorite(userId, foodId)
            if (isFavorite) {
                removeFavorite(userId, foodId)
            } else {
                addFavorite(userId, foodId)
            }
            getFavoriteIds(userId)
        }
    }

    fun getFavoriteIds(userId: String) {
        viewModelScope.launch {
            val result = favoriteRepository.getFavoriteIds(userId)
            favorites.value = result
        }
    }
}