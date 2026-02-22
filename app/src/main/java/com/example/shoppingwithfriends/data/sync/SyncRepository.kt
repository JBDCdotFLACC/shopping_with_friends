package com.example.shoppingwithfriends.data.sync

import com.example.shoppingwithfriends.data.source.local.PendingOp

interface SyncRepository {
    suspend fun getNextPendingOp() : PendingOp?
    suspend fun markDone(id : String)
    suspend fun markFailure(id : String)
}