package com.example.biyolla.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biyolla.data.AuthRepository
import com.example.biyolla.data.Repository
import com.example.biyolla.model.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val cartList = MutableLiveData<List<Cart>>()
    val totalPrice = MutableLiveData<Int>()
    private var total: Int = 0
    val userid = MutableLiveData<String?>()

     fun getCart(userName: String) {
        viewModelScope.launch {
            try {
                val cartResponse = repository.getCart(userName)
                cartList.value = cartResponse
            } catch (e: Exception) {
                Log.e("API Cart Error", e.message ?: "Unknown error")
            }
            totalPrice()
        }
    }

    fun deleteCart(cartFoodId: Int, userName: String) {
        viewModelScope.launch {
            try {
                repository.deleteCart(cartFoodId, userName)
                getCart(userName)
                if (cartList.value?.size == 1) {
                    cartList.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("API Delete Cart Error", e.message ?: "Unknown error")
            }
        }
    }

    private fun totalPrice() {
        val list = cartList.value
        if (list != null) {
            for (i in list) {
                total += i.foodPrice?.toInt()?.times(i.foodOrderQuantity?.toInt()!!)!!
            }
        } else {
            totalPrice.value = 0
        }
        totalPrice.value = total
        total = 0
    }

    fun getUid () {
        val user = authRepository.getCurrentUser()
        val uuid = user?.uid
        userid.value = uuid
    }
}
