package com.example.shoppingwithfriends.data

import androidx.room.PrimaryKey
import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.Utils.createPendingOp
import com.example.shoppingwithfriends.data.source.local.ContactType
import com.example.shoppingwithfriends.data.source.local.FriendRequest
import com.example.shoppingwithfriends.data.source.local.FriendRequestStatus
import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.User
import com.example.shoppingwithfriends.data.sync.SyncWorkManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao,
                                               private val fireBaseFireStore : FirebaseFirestore,
                                               private val authRepository: AuthRepository,
                                               private val syncWorkManager: SyncWorkManager,) : FriendRepository {
    override suspend fun sendFriendRequest(requestedId: String) : FriendRepository.FriendRequestResponse {
        if(determineFriendship(requestedId)){
            return FriendRepository.FriendRequestResponse.ALREADY_FRIEND
        }
        val requestStatus = determinePendingFriendRequest(requestedId)
        if(requestStatus == PendingFriendshipDirection.OUTBOUND) return FriendRepository.FriendRequestResponse.PENDING_SENT
        if(requestStatus == PendingFriendshipDirection.INBOUND) return FriendRepository.FriendRequestResponse.PENDING_RECEIVED
        val primaryKey = UUID.randomUUID().toString() //request id and requested id are too close
        val friendRequest = FriendRequest(id = primaryKey, userId = authRepository.getUserId(),
            requestedId = requestedId, status = FriendRequestStatus.PENDING)
        localDataSource.insertFriendRequest(friendRequest)
        val json = Json.encodeToString(friendRequest)
        val pendingOp = createPendingOp(opType = OpType.SEND_FRIEND_REQUEST,
            entityId = primaryKey,
            payload = json)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
        return FriendRepository.FriendRequestResponse.SUCCESS
    }

    private suspend fun determineFriendship(requestedId : String) : Boolean {
        if(localDataSource.getFriendship(authRepository.getUserId(), requestedId) != null){
            return true
        }
        else{
            val document = fireBaseFireStore.collection("friendship")
                .whereEqualTo("userId", authRepository.getUserId())
                .whereEqualTo("friendId", requestedId)
                .get()
                .await()
                .firstOrNull()
            //if it is not null we should really do a pull to stay current
            return(document != null)
        }
    }

    private suspend fun determinePendingFriendRequest(requestedId : String) : PendingFriendshipDirection{
        //We have to look in both directions in case we have an inbound friend request
        val userId = authRepository.getUserId()
        val inboundRequest = localDataSource.getFriendRequest( userId = requestedId, requestedId = userId)
        val outboundRequest = localDataSource.getFriendRequest(userId = userId, requestedId = requestedId)
        if (inboundRequest?.status == FriendRequestStatus.PENDING) return PendingFriendshipDirection.INBOUND
        if (outboundRequest?.status == FriendRequestStatus.PENDING) return PendingFriendshipDirection.OUTBOUND
        val remoteOutboundRequest = fireBaseFireStore.collection("friendRequest")
            .whereEqualTo("userId", userId)
            .whereEqualTo("requestedId", requestedId)
            .whereEqualTo("status", FriendRequestStatus.PENDING.name)
            .get()
            .await()
            .firstOrNull()
        if(remoteOutboundRequest != null) return PendingFriendshipDirection.OUTBOUND

        val remoteInboundRequest = fireBaseFireStore.collection("friendRequest")
            .whereEqualTo("userId", requestedId)
            .whereEqualTo("requestedId", userId)
            .whereEqualTo("status", FriendRequestStatus.PENDING.name)
            .get()
            .await()
            .firstOrNull()
        if(remoteInboundRequest != null) return PendingFriendshipDirection.INBOUND
        return PendingFriendshipDirection.NONE
    }

    enum class PendingFriendshipDirection{
        INBOUND,
        OUTBOUND,
        NONE
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