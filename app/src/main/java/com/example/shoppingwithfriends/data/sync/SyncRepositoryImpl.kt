package com.example.shoppingwithfriends.data.sync

import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.source.local.PendingOp
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.SyncState
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao) : SyncRepository {
    override suspend fun getNextPendingOp(): PendingOp? {
        return localDataSource.getOldestPending()
    }

    override suspend fun markDone(id: String) {
        localDataSource.updatePendingOp(id, SyncState.SYNCED)
    }

    override suspend fun markFailure(id: String) {
        localDataSource.updatePendingOp(id, SyncState.FAILED)
    }
}