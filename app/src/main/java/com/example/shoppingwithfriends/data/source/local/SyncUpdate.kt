package com.example.shoppingwithfriends.data.source.local

import kotlinx.serialization.Serializable

@Serializable
data class SyncUpdate (val id : String, val content : String? = null, val isChecked : Boolean? = null)
