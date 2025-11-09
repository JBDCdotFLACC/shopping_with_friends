package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.LocalShoppingList

interface ShoppingListRepository {
    suspend fun getListsForUser(userId : Int) : List<LocalShoppingList>
    suspend fun addNewShoppingList(shoppingList : LocalShoppingList, userId : Int)
    suspend fun addFriendToShoppingList(shoppingListId : Int, friendId : Int)
    suspend fun getShoppingList(shoppingListId : Int) : LocalShoppingList
    suspend fun setShoppingListItemCheck(shoppingListItem: String, isChecked: Boolean)
}