package com.example.shoppingwithfriends.data.source.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromOpState(value: SyncState): String {
        return value.name
    }

    @TypeConverter
    fun toOpState(value: String): SyncState {
        return SyncState.valueOf(value)
    }

    @TypeConverter
    fun fromFriendRequestStatus(status: FriendRequestStatus): String = status.name

    @TypeConverter
    fun toFriendRequestStatus(value: String): FriendRequestStatus = FriendRequestStatus.valueOf(value)
}