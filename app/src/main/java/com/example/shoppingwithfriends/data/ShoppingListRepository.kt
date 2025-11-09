package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.models.ShoppingList
import com.example.shoppingwithfriends.models.ShoppingListItem

interface ShoppingListRepository {
    suspend fun getListsForUser(userId : Int) : List<ShoppingList>
    suspend fun addNewShoppingList(shoppingList : ShoppingList, userId : Int)
    suspend fun addFriendToShoppingList(shoppingListId : Int, friendId : Int)
    suspend fun getShoppingList(shoppingListId : Int) : ShoppingList
    suspend fun setShoppingListItemCheck(shoppingListItem: ShoppingListItem, isChecked: Boolean)
}