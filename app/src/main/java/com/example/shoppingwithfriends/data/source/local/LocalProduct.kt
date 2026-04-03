package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(
    tableName = "product"
)

@Serializable
data class LocalProduct (
    @PrimaryKey
    val id: String,
    var content: String,
    var parent: String,
    @get:PropertyName("isChecked")
    @set:PropertyName("isChecked")
    @SerialName("isChecked")
    var isChecked: Boolean,
    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    @SerialName("isDeleted")
    var isDeleted: Boolean = false,
)
