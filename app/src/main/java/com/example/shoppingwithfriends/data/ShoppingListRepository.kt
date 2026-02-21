package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getAllListsForUser() : Flow<List<LocalShoppingList>>
    suspend fun addNewShoppingList(date : Long, listName : String) : String
    suspend fun addFriendToShoppingList(shoppingListId : String, friendId : String)
    suspend fun getShoppingList(shoppingListId : String) : LocalShoppingList
    suspend fun setProductCheck(productId: String, isChecked: Boolean)
    fun getProductList(shoppingListId : String) : Flow<List<LocalProduct>>
    suspend fun updateListName(shoppingListId: String, newName: String)
    suspend fun deleteProduct(productId: String)
    suspend fun addProduct(product : LocalProduct)
    suspend fun updateProductName(productId: String, newName : String)
    suspend fun deleteList(listId: String)
}