package com.example.shoppingwithfriends.data

import android.util.Log
import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.PendingOp
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.SyncState
import com.example.shoppingwithfriends.data.sync.SyncWorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao,
                                                     private val authRepository: AuthRepository,
                                                     private val syncWorkManager: SyncWorkManager
) : ShoppingListRepository {
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

        val shoppingListId = UUID.randomUUID().toString()
        val newShoppingList = LocalShoppingList(
            id = shoppingListId,
            name = listName,
            date = date,
            owner = user.uid
        )

        localDataSource.insertShoppingList(newShoppingList)
        val json = Json.encodeToString(newShoppingList)
        val opId = UUID.randomUUID().toString()
        val pendingOp = PendingOp(id = opId,
            type = OpType.CREATE_LIST,
            entityId = shoppingListId,
            parentId = null,
            payloadJson = json,
            createdAt = Date().time,
            retryCount = 0,
            state = SyncState.PENDING
        )
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
        Log.i("wxyz", "in shoppinglist repo")
        return shoppingListId
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