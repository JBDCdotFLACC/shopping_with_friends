package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(
    tableName = "friendRequest"
)

@Serializable
class FriendRequest(
    @PrimaryKey
    override val id: String = "",
    var userId : String = "",
    var requestedId : String = "",
    var status : FriendRequestStatus = FriendRequestStatus.PENDING
) : FireBaseModel

@Serializable
enum class FriendRequestStatus{
    ACCEPTED,
    PENDING,
    DECLINED
}