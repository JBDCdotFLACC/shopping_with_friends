package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.ContactType
import com.example.shoppingwithfriends.data.source.local.User
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    suspend fun sendFriendRequest(requestedId: String) : FriendRequestResponse
    suspend fun removeFriend(friendId: String)
    suspend fun getFriends(): Flow<List<String>>
    suspend fun getFriendRequests(): Flow<List<String>>
    suspend fun acceptFriendRequest(requestId: String)
    suspend fun declineFriendRequest(requestId: String)
    suspend fun searchForFriend(searchTerm: String, contactType: ContactType) : User?

    enum class FriendRequestResponse{
        SUCCESS,
        PENDING_SENT,
        PENDING_RECEIVED,
        ALREADY_FRIEND
    }
}