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
    override val id: String = "",
    var content: String = "",
    var parent: String = "",
    @get:PropertyName("isChecked")
    @set:PropertyName("isChecked")
    @SerialName("isChecked")
    var isChecked: Boolean = false,
    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    @SerialName("isDeleted")
    var isDeleted: Boolean = false,
    @get:PropertyName("versionId")
    @set:PropertyName("versionId")
    var versionId: String = ""
) : FireBaseModel
