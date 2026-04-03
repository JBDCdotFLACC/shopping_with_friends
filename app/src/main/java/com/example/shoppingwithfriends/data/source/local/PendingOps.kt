package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "pending_ops"
)

data class PendingOp(
    @PrimaryKey val id: String,
    val type: OpType,
    val entityId: String,
    val payloadJson: String?,
    val createdAt: Long,
    val retryCount: Int,
    val state: SyncState
)

enum class OpType {
    CREATE_PRODUCT,
    UPDATE_PRODUCT_CHECKED,
    UPDATE_PRODUCT_NAME,
    DELETE_PRODUCT,
    CREATE_LIST,
    DELETE_LIST,
    UPDATE_LIST_NAME
}

enum class SyncState {
    PENDING,
    SYNCED,
    FAILED
}