package com.example.biyolla.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Food(
    @SerializedName("yemek_id") val foodId: String?,
    @SerializedName("yemek_adi") val foodName: String?,
    @SerializedName("yemek_resim_adi") val foodImage: String?,
    @SerializedName("yemek_fiyat") val foodPrice: String?,
) : Parcelable
