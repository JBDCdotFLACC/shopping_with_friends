package com.example.shoppingwithfriends.data

import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun sendFriendRequest(friendId: String)
    suspend fun removeFriend(friendId: String)
    suspend fun getFriends(): Flow<List<String>>
    suspend fun getFriendRequests(): Flow<List<String>>
    suspend fun acceptFriendRequest(requestId: String)
    suspend fun declineFriendRequest(requestId: String)
    suspend fun searchForFriend(string: String)
}