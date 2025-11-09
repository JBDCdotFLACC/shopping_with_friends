package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao) : ShoppingListRepository {
    override suspend fun getListsForUser(userId: Int): List<LocalShoppingList> {
        return localDataSource.getShoppingLists()
    }

    override suspend fun addNewShoppingList(
        shoppingList: LocalShoppingList,
        userId: Int
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun addFriendToShoppingList(shoppingListId: Int, friendId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getShoppingList(shoppingListId: Int): LocalShoppingList {
        TODO("Not yet implemented")
    }

    override suspend fun setShoppingListItemCheck(
        shoppingListItem: String,
        isChecked: Boolean
    ) {
        TODO("Not yet implemented")
    }
}