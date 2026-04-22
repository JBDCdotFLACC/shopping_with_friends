package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.User

interface UserRepository {
    suspend fun addUser(newUser : User)
    suspend fun getUser(userId : String) : User?
    suspend fun addEmail()
    suspend fun removeEmail()
    suspend fun updateUserDisplayName()
}