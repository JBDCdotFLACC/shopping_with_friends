package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.models.ShoppingList

interface ShoppingListRepository {
    suspend fun getListsForUser(userId : Int) : List<ShoppingList>
    suspend fun addNewShoppingList(shoppingList : ShoppingList, userId : Int)
    suspend fun addFriendToShoppingList(shoppingListId : Int, friendId : Int)
}