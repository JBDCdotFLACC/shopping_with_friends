package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.models.ShoppingList

interface ShoppingListRepository {
    fun getListsForUser(userId : Int) : List<ShoppingList>
    fun addNewShoppingList(shoppingList : ShoppingList, userId : Int)
    fun addFriendToShoppingList(shoppingListId : Int, friendId : Int)
}