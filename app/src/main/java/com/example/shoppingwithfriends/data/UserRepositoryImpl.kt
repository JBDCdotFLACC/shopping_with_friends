package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.Utils.createPendingOp
import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.User
import com.example.shoppingwithfriends.data.sync.SyncWorkManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
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
        val pendingOp = createPendingOp(opType = OpType.CREATE_LIST,
            entityId = newUser.id,
            payload = json)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
    }

    override suspend fun getUser(id: String) : User?{
       return localDataSource.getUser(id)
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