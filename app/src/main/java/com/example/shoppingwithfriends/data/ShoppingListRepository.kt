package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList

interface ShoppingListRepository {
    suspend fun getListsForUser(userId : String) : List<LocalShoppingList>
    suspend fun addNewShoppingList(shoppingList : LocalShoppingList)
    suspend fun addFriendToShoppingList(shoppingListId : String, friendId : String)
    suspend fun getShoppingList(shoppingListId : String) : LocalShoppingList
    suspend fun setProductCheck(productId: String, isChecked: Boolean)
    suspend fun getProdcut(productId: String) : LocalProduct
}