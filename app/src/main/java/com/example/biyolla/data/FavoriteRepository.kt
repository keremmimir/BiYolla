package com.example.biyolla.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoriteRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun addFavorite(userId: String, foodId: String): Result<String> {
        return try {
            val favoriteRef = firestore.collection("users").document(userId).collection("favorites")
                .document(foodId)
            favoriteRef.set(mapOf("foodId" to foodId)).await()
            Result.success("Favoriye eklendi.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(userId: String, foodId: String): Result<String> {
        return try {
            val favoriteRef = firestore.collection("users").document(userId).collection("favorites")
                .document(foodId)
            favoriteRef.delete().await()
            Result.success("Favoriden kaldırıldı.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorite(userId: String, foodId: String): Boolean {
        val favoriteRef = firestore.collection("users").document(userId)
            .collection("favorites").document(foodId)

        return try {
            val document = favoriteRef.get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFavoriteIds(userId: String): Set<String> {
        return try {
            val result = firestore.collection("users").document(userId)
                .collection("favorites").get().await()

            result.map { it.id }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }
}