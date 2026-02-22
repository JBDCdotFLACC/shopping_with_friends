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
    val parentId: String?, // e.g., listId for items
    val payloadJson: String?,
    val createdAt: Long,
    val retryCount: Int,
    val state: SyncState
)

enum class OpType {
    CREATE_ITEM,
    UPDATE_ITEM_CHECKED,
    UPDATE_ITEM_NAME,
    DELETE_ITEM,
    CREATE_LIST,
    ADD_MEMBER
}

enum class SyncState {
    PENDING,
    SYNCED,
    FAILED
}