package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.Auth.AuthRepository
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao,
                                                     private val authRepository: AuthRepository) : ShoppingListRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllListsForUser(): Flow<List<LocalShoppingList>> {
        return authRepository.currentUser
            .flatMapLatest { user ->
                if (user == null) {
                    flowOf(emptyList())
                } else {
                    localDataSource.getAllShoppingListsForUser(user.uid)
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun addNewShoppingList(
        date: Long,
        listName: String
    ) : String {
        val user = authRepository.currentUser.first()
            ?: throw IllegalStateException("User not signed in")

        val newId = UUID.randomUUID().toString()
        val newShoppingList = LocalShoppingList(
            id = newId,
            name = listName,
            date = date,
            owner = user.uid
        )

        localDataSource.insertShoppingList(newShoppingList)
        return newId
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

    override suspend fun deleteList(listId: String) {
        localDataSource.deleteShoppingList(listId)
        localDataSource.deleteProductsFromShoppingList(listId)
    }
}