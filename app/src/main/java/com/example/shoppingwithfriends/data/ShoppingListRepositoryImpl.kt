package com.example.shoppingwithfriends.data

import android.util.Log
import androidx.paging.DiffingChangePayload
import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.OpType
import com.example.shoppingwithfriends.data.source.local.PendingOp
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.SyncState
import com.example.shoppingwithfriends.data.source.local.SyncUpdate
import com.example.shoppingwithfriends.data.sync.SyncWorkManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(private val localDataSource: ShoppingDao,
                                                     private val authRepository: AuthRepository,
                                                     private val syncWorkManager: SyncWorkManager,
                                                     private val fireBaseFireStore : FirebaseFirestore
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
        val versionId = UUID.randomUUID().toString()
        val newShoppingList = LocalShoppingList(
            id = shoppingListId,
            name = listName,
            date = date,
            owner = user.uid,
            isDeleted = false,
            versionId = versionId
        )
        localDataSource.insertShoppingList(newShoppingList)
        val json = Json.encodeToString(newShoppingList)
        val pendingOp = createPendingOp(opType = OpType.CREATE_LIST,
            entityId = shoppingListId,
            payload = json)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
        return shoppingListId
    }

    override suspend fun getShoppingList(shoppingListId: String): LocalShoppingList {
        return localDataSource.getShoppingList(shoppingListId)
    }

    override suspend fun setProductCheck(
        productId: String,
        isChecked: Boolean
    ) {
        val versionId = UUID.randomUUID().toString()
        val syncUpdate = SyncUpdate(id = productId, isChecked = isChecked, versionId = versionId)
        val json = Json.encodeToString(syncUpdate)
        val pendingOp = createPendingOp(opType = OpType.UPDATE_PRODUCT_CHECKED,
            entityId = productId,
            payload = json)
        localDataSource.updateCompleted(productId, isChecked)
        localDataSource.updateProductVersionId(productId, versionId)
        localDataSource.insertPendingOp(pendingOp)
    }

    override suspend fun deleteProduct(productId: String) {
        val versionId = UUID.randomUUID().toString()
        val syncUpdate = SyncUpdate(id = productId, versionId = versionId)
        val json = Json.encodeToString(syncUpdate)
        val pendingOp = createPendingOp(opType = OpType.DELETE_PRODUCT, entityId = productId, payload = json)
        localDataSource.deleteById(productId)
        localDataSource.updateProductVersionId(productId, versionId)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
    }

    override suspend fun updateListName(shoppingListId: String, newName: String) {
        val user = authRepository.currentUser.first()
            ?: throw IllegalStateException("User not signed in")
        val versionId = UUID.randomUUID().toString()
        val syncUpdate = SyncUpdate(id = shoppingListId, content = newName, versionId = versionId)
        val json = Json.encodeToString(syncUpdate)
        val pendingOp = createPendingOp(opType = OpType.UPDATE_LIST_NAME,
            entityId = shoppingListId,
            payload = json)
        localDataSource.updateListName(shoppingListId, newName)
        localDataSource.updateShoppingListVersionId(shoppingListId, versionId)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
    }

    override suspend fun updateProductName(productId: String, newName: String) {
        val versionId = UUID.randomUUID().toString()
        val syncUpdate = SyncUpdate(id = productId, content = newName, versionId = versionId)
        val json = Json.encodeToString(syncUpdate)
        val pendingOp = createPendingOp(opType = OpType.UPDATE_PRODUCT_NAME,
            entityId = productId,
            payload = json)
        localDataSource.updateProductName(productId, newName)
        localDataSource.updateProductVersionId(productId, versionId)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
    }

    override fun getProductList(shoppingListId: String): Flow<List<LocalProduct>> {
        return localDataSource.getProductList(shoppingListId)
    }

    override suspend fun addProduct(product: LocalProduct) {
        localDataSource.insertProduct(product)
        val json = Json.encodeToString(product)
        val pendingOp = createPendingOp(opType = OpType.CREATE_PRODUCT,
            entityId = product.id,
            payload = json)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()
    }

    override suspend fun deleteList(listId: String) {
        val versionId = UUID.randomUUID().toString()
        val syncUpdate = SyncUpdate(id = listId, versionId = versionId)
        val json = Json.encodeToString(syncUpdate)
        val pendingOp = createPendingOp(opType = OpType.DELETE_LIST, entityId = listId, payload = json)
        localDataSource.deleteShoppingList(listId)
        localDataSource.deleteProductsFromShoppingList(listId)
        localDataSource.updateShoppingListVersionId(listId, versionId)
        localDataSource.insertPendingOp(pendingOp)
        syncWorkManager.scheduleSync()

    }

    fun createPendingOp(opType : OpType, entityId : String, payload: String?) : PendingOp{
        val opId = UUID.randomUUID().toString()
        return PendingOp(id = opId,
            type = opType,
            entityId = entityId,
            payloadJson = payload,
            createdAt = Date().time,
            retryCount = 0,
            state = SyncState.PENDING
        )
    }

    override suspend fun pullRemoteDataForUser(){
        val user = authRepository.currentUser.first()
            ?: throw IllegalStateException("User not signed in")
        val userId = user.uid

        val pendingSet = localDataSource.getOpsByState(SyncState.PENDING).map{it.id}.toSet()
        //TODO filter out syncs
        //TODO collaboration
        //For our own list
        val shoppingListDocuments = fireBaseFireStore.collection("lists").whereEqualTo("owner", userId).whereEqualTo("isDeleted", false).get().await()
        val shoppingLists = shoppingListDocuments.toObjects(LocalShoppingList::class.java)
        Log.i("wxyz", shoppingLists.size.toString())
        localDataSource.insertAllShoppingLists(shoppingLists)
        val productDocuments = fireBaseFireStore.collection("products").whereEqualTo("isDeleted", false).get().await()
        val products = productDocuments.toObjects(LocalProduct::class.java)
        Log.i("wxyz", products.size.toString())
        localDataSource.insertAllProducts(products)
    }
}