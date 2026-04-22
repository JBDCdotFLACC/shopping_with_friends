package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.Utils.createPendingOp
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.User
import com.example.shoppingwithfriends.data.sync.SyncWorkManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao,
                                             private val authRepository: AuthRepository,
                                             private val syncWorkManager: SyncWorkManager,
                                             private val fireBaseFireStore : FirebaseFirestore
)
                                       : UserRepository {
    override suspend fun addUser(newUser : User) {
        val user = authRepository.currentUser.first()
            ?: throw IllegalStateException("User not signed in")
        localDataSource.insertUser(newUser)
        val json = Json.encodeToString(newUser)
        val pendingOp = createPendingOp(opType = OpType.ADD_USER,
            entityId = newUser.id,
            payload = json)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
    }

    override suspend fun getUser(userId: String) : User?{
        val localUser = localDataSource.getUser(userId)
        if(localUser != null) return localUser
        val document = fireBaseFireStore.collection("users").whereEqualTo("id", userId).get().await().firstOrNull()
        return document?.toObject(User::class.java)
    }

    override suspend fun addEmail() {
        TODO("Not yet implemented")
    }

    override suspend fun removeEmail() {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserDisplayName() {
        TODO("Not yet implemented")
    }

}