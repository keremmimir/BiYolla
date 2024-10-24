package com.example.biyolla.data

import android.util.Log
import com.example.biyolla.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signUp(
        name: String,
        surname: String,
        username: String,
        email: String,
        password: String
    ): Result<String> {
        return try {
            val usernameExistsResult = checkUsernameExists(username)
            if (usernameExistsResult.isSuccess && usernameExistsResult.getOrNull() == true) {
                return Result.failure(Exception("Bu kullanıcı adı zaten kullanılıyor."))
            }

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID is null")
            val user =
                User(
                    uid = uid,
                    name = name,
                    surname = surname,
                    username = username,
                    email = email,
                    password = password
                )

            firestore.collection("users").document(uid).set(user).await()
            Result.success("Üyelik başarıyla tamamlandı! Aramıza hoş geldin!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success("Hoş geldin!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut(): Result<String> {
        return try {
            auth.signOut()
            Result.success("Tekrar görüşmek üzere!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkUsernameExists(username: String): Result<Boolean> {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserName(): String? {
        val userId = getCurrentUser()?.uid ?: return null
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.getString("username")
        } catch (exception: Exception) {
            Log.w("UserRepository", "Error getting document: ", exception)
            null
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}