package com.example.shoppingwithfriends.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM product WHERE parent = :listId AND isDeleted = 0")
    fun getProductList(listId: String): Flow<List<LocalProduct>>

    @Query("UPDATE product SET isChecked = :checked WHERE id = :productId")
    suspend fun updateCompleted(productId: String, checked: Boolean)

    @Query("UPDATE shopping_list SET name = :newName WHERE id = :listId")
    suspend fun updateListName(listId: String, newName: String)

    @Query("UPDATE product SET content = :newName WHERE id = :productId")
    suspend fun updateProductName(productId: String, newName: String)

    @Query("UPDATE product SET isDeleted = 1 WHERE id = :productId")
    suspend fun deleteById(productId: String): Int

    @Query("UPDATE shopping_list SET isDeleted = 1 WHERE id = :shoppingListId")
    suspend fun deleteShoppingList(shoppingListId: String)

    @Query("UPDATE product SET versionId = :versionId WHERE id = :productId")
    suspend fun updateProductVersionId(productId : String, versionId: String)

    @Query("UPDATE shopping_list SET versionId = :versionId WHERE id = :shoppingListId")
    suspend fun updateShoppingListVersionId(shoppingListId : String, versionId: String)

    @Query("UPDATE product SET isDeleted = 1 WHERE parent = :shoppingListId ")
    suspend fun deleteProductsFromShoppingList(shoppingListId: String)


    @Query(value = "SELECT * FROM shopping_list WHERE owner = :userId AND isDeleted = 0")
    fun getAllShoppingListsForUser(vararg userId : String): Flow<List<LocalShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(vararg shoppingList: LocalShoppingList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(vararg product: LocalProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllShoppingLists(shoppingLists : List<LocalShoppingList>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(localProducts : List<LocalProduct>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: User)

    @Query(value = "SELECT * FROM shopping_list WHERE id = :shoppingListId")
    suspend fun getShoppingList(shoppingListId : String) : LocalShoppingList

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingOp(vararg pendingOp: PendingOp)

    @Query("UPDATE pending_ops SET state = :syncState WHERE id = :opId")
    suspend fun updatePendingOp(opId: String, syncState: SyncState)

    @Query("SELECT * FROM pending_ops WHERE state = :state")
    suspend fun getOpsByState(state: SyncState): List<PendingOp>

    @Query("""
  SELECT * FROM pending_ops
  WHERE state = 'PENDING'
  ORDER BY createdAt ASC
  LIMIT 1
""")
    suspend fun getOldestPending(): PendingOp?

}