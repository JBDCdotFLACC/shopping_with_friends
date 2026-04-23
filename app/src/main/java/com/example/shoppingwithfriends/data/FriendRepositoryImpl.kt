package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.ContactType
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao,
                                               private val fireBaseFireStore : FirebaseFirestore) : FriendRepository {
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

    override suspend fun searchForFriend(searchTerm: String, contactType : ContactType) : User? {
        when(contactType){
            ContactType.EMAIL -> {
                val localResult = localDataSource.getUserByEmail(searchTerm)
                if(localResult != null) return localResult
                else {
                    val document = fireBaseFireStore.collection("users")
                        .whereEqualTo("email", searchTerm)
                        .get()
                        .await()
                        .firstOrNull()
                    return document?.toObject(User::class.java)
                }
            }
            ContactType.PHONE -> {
                val localResult = localDataSource.getUserByPhoneNumber(searchTerm)
                if(localResult != null) return localResult
                else {
                    val document = fireBaseFireStore.collection("users")
                        .whereEqualTo("phoneNumber", searchTerm)
                        .get()
                        .await()
                        .firstOrNull()
                    return document?.toObject(User::class.java)
                }
            }
        }
    }
}