package com.example.biyolla.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biyolla.Event
import com.example.biyolla.data.AuthRepository
import com.example.biyolla.data.Repository
import com.example.biyolla.model.Cart
import com.example.biyolla.model.Food
import com.example.biyolla.model.User
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val foodList = MutableLiveData<List<Food>>()
    val authResult = MutableLiveData<Event<Result<String>?>>()
    val error = MutableLiveData<Event<String>>()
    private var cartList: List<Cart> = emptyList()
    val username = MutableLiveData<String>()
    val userid = MutableLiveData<String?>()
    val filteredItems = MutableLiveData<List<Food>?>()

    init {
        getFood()
    }

    private fun getFood() {
        viewModelScope.launch {
            try {
                val foodResponse = repository.getFood()
                foodList.value = foodResponse
            } catch (e: Exception) {
                Log.e("API Error", e.message ?: "Unknown error")
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

    fun signOut() {
        viewModelScope.launch {
            val result = authRepository.signOut()
            if (result.isSuccess) {
                authResult.value = Event(result)
            } else {
                error.value = Event(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun getUid () {
        val user = authRepository.getCurrentUser()
        val uuid = user?.uid
        userid.value = uuid
    }

    fun loadUserName() {
        viewModelScope.launch {
            username.value = authRepository.getUserName()
        }
    }

    fun searchItems(query: String) {
        val filteredList = foodList.value?.filter {
            it.foodName?.contains(query, ignoreCase = true) ?: false
        }
        filteredItems.value = filteredList
    }
}