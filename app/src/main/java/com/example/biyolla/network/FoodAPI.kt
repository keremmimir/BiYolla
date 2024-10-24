package com.example.biyolla.network

import com.example.biyolla.model.CRUDResponse
import com.example.biyolla.model.CartResponse
import com.example.biyolla.model.FoodResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FoodAPI {
    //http://kasimadalan.pe.hu/yemekler/tumYemekleriGetir.php
    @GET("yemekler/tumYemekleriGetir.php")
    suspend fun getFood(): FoodResponse

    //http://kasimadalan.pe.hu/yemekler/sepeteYemekEkle.php
    @POST("yemekler/sepeteYemekEkle.php")
    @FormUrlEncoded
    suspend fun addCart(
        @Field("yemek_adi") foodName: String,
        @Field("yemek_resim_adi") foodImageName: String,
        @Field("yemek_fiyat") foodPrice: Int,
        @Field("yemek_siparis_adet") foodOrderQuantity: Int,
        @Field("kullanici_adi") userName: String
    ): CRUDResponse

    //http://kasimadalan.pe.hu/yemekler/sepettekiYemekleriGetir.php
    @POST("yemekler/sepettekiYemekleriGetir.php")
    @FormUrlEncoded
    suspend fun getCart(@Field("kullanici_adi") userName: String): CartResponse

    //http://kasimadalan.pe.hu/yemekler/sepettenYemekSil.php
    @POST("yemekler/sepettenYemekSil.php")
    @FormUrlEncoded
    suspend fun deleteCart(
        @Field("sepet_yemek_id") cartFoodId: Int,
        @Field("kullanici_adi") userName: String
    ): CRUDResponse
}