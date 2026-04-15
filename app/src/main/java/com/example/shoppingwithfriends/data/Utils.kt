package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.PendingOp
import com.example.shoppingwithfriends.data.source.local.SyncState
import java.util.Date
import java.util.UUID

object Utils {
    fun createPendingOp(opType : OpType, entityId : String, payload: String?) : PendingOp{
        val opId = UUID.randomUUID().toString()
        return PendingOp(id = opId,
            type = opType,
            entityId = entityId,
            payloadJson = payload,
            createdAt = Date().time,
            retryCount = 0,
            state = SyncState.PENDING
        )
    }
}