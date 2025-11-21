package com.example.shoppingwithfriends.data

import android.util.Log
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao) : ShoppingListRepository {
    override suspend fun getListsForUser(userId: String): List<LocalShoppingList> {
        val x = localDataSource.getShoppingLists()
        return x
    }

    override suspend fun addNewShoppingList(
        shoppingList: LocalShoppingList
    ) {
        localDataSource.insertShoppingList(shoppingList)
    }

    override suspend fun addFriendToShoppingList(shoppingListId: String, friendId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getShoppingList(shoppingListId: String): LocalShoppingList {
        TODO("Not yet implemented")
    }

    override suspend fun setProductCheck(
        productId: String,
        isChecked: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getProdcut(productId: String): LocalProduct {
        TODO("Not yet implemented")
    }

    override suspend fun getProductList(shoppingListId: String): List<LocalProduct> {
        return localDataSource.getProductList(shoppingListId)
    }
}