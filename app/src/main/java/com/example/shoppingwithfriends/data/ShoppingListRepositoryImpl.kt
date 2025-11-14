package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.models.ShoppingList
import com.example.shoppingwithfriends.models.ShoppingListItem
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor() : ShoppingListRepository {
    override suspend fun getListsForUser(userId: String): List<ShoppingList> {
        //For now this is just getting all the shoppingLists

    }

    override suspend fun addNewShoppingList(
        shoppingList: ShoppingList,
        userId: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun addFriendToShoppingList(shoppingListId: Int, friendId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getShoppingList(shoppingListId: Int): ShoppingList {
        TODO("Not yet implemented")
    }

    override suspend fun setShoppingListItemCheck(
        shoppingListItem: ShoppingListItem,
        isChecked: Boolean
    ) {
        TODO("Not yet implemented")
    }

    fun unPackProduct(){

    }

    fun unPackShoppingList(){

    }
}