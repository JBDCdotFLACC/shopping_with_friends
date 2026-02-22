package com.example.shoppingwithfriends.auth

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    suspend fun signInWithGoogleIdToken(idToken: String)
    suspend fun signOut()
}