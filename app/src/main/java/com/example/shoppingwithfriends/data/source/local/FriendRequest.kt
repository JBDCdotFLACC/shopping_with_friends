package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "friendRequest"
)

class FriendRequest(
    @PrimaryKey
    override val id: String = "",
    var userId : String = "",
    var requestedId : String = "",
    var status : FriendRequestStatus = FriendRequestStatus.PENDING
) : FireBaseModel

enum class FriendRequestStatus{
    ACCEPTED,
    PENDING,
    DECLINED
}