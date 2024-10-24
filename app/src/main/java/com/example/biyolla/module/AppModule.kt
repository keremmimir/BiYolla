package com.example.biyolla.module

import com.example.biyolla.data.AuthRepository
import com.example.biyolla.data.DataSource
import com.example.biyolla.data.FavoriteRepository
import com.example.biyolla.data.Repository
import com.example.biyolla.network.FoodAPI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "http://kasimadalan.pe.hu/"

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): FoodAPI = retrofit.create(FoodAPI::class.java)

    @Singleton
    @Provides
    fun providesDataSource(foodAPI: FoodAPI) = DataSource(foodAPI)

    @Singleton
    @Provides
    fun providesRepository(dataSource: DataSource) = Repository(dataSource)

    @Singleton
    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun providesFirebase(auth: FirebaseAuth, firestore: FirebaseFirestore) =
        AuthRepository(auth, firestore)

    @Singleton
    @Provides
    fun providesFavorite(auth: FirebaseAuth, firestore: FirebaseFirestore) =
        FavoriteRepository(auth, firestore)
}