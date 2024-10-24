package com.example.biyolla.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart(
    @SerializedName("sepet_yemek_id") val cartFoodId: String?,
    @SerializedName("yemek_adi") val foodName: String?,
    @SerializedName("yemek_resim_adi") val foodImage: String?,
    @SerializedName("yemek_fiyat") val foodPrice: String?,
    @SerializedName("yemek_siparis_adet") val foodOrderQuantity: String?,
    @SerializedName("kullanici_adi") val userName: String?
) : Parcelable