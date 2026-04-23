package com.example.shoppingwithfriends.data

import kotlinx.coroutines.flow.Flow

class FriendRepositoryImpl : FriendRepository {
    override suspend fun sendFriendRequest(friendId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFriend(friendId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getFriends(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFriendRequests(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun acceptFriendRequest(requestId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun declineFriendRequest(requestId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun searchForFriend(string: String) {
        TODO("Not yet implemented")
    }

}