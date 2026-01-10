package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao) : ShoppingListRepository {
    override suspend fun getListsForUser(userId: String): List<LocalShoppingList> {
        val x = localDataSource.getAllShoppingLists()
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
        return localDataSource.getShoppingList(shoppingListId)
    }

    override suspend fun setProductCheck(
        productId: String,
        isChecked: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productId: String) {
        localDataSource.deleteById(productId)
    }

    override suspend fun updateListName(shoppingListId: String, newName: String) {
        localDataSource.updateListName(shoppingListId, newName)
    }

    override suspend fun getProdcut(productId: String): LocalProduct {
        TODO("Not yet implemented")
    }

    override suspend fun getProductList(shoppingListId: String): List<LocalProduct> {
        return localDataSource.getProductList(shoppingListId)
    }
}