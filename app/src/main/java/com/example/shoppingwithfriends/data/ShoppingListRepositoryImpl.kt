package com.example.shoppingwithfriends.data

import android.util.Log
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import kotlinx.coroutines.flow.Flow
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
        localDataSource.updateCompleted(productId, isChecked)
    }

    override suspend fun deleteProduct(productId: String) {
        localDataSource.deleteById(productId)
    }

    override suspend fun updateListName(shoppingListId: String, newName: String) {
        localDataSource.updateListName(shoppingListId, newName)
    }

    override suspend fun updateProductName(productId: String, newName: String) {
        localDataSource.updateProductName(productId, newName)
    }

    override fun getProductList(shoppingListId: String): Flow<List<LocalProduct>> {
        return localDataSource.getProductList(shoppingListId)
    }

    override suspend fun addProduct(product: LocalProduct) {
        localDataSource.insertProduct(product)
    }
}