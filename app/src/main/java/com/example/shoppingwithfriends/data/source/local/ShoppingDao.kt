package com.example.shoppingwithfriends.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM product WHERE parent = :listId")
    fun getProductList(listId: String): Flow<List<LocalProduct>>

    @Query("UPDATE product SET isChecked = :checked WHERE id = :productId")
    suspend fun updateCompleted(productId: String, checked: Boolean)

    @Query("UPDATE shopping_list SET name = :newName WHERE id = :listId")
    suspend fun updateListName(listId: String, newName: String)

    @Query("UPDATE product SET content = :newName WHERE id = :productId")
    suspend fun updateProductName(productId: String, newName: String)

    @Query("DELETE FROM product WHERE id = :productId")
    suspend fun deleteById(productId: String): Int

    @Query(value = "SELECT * FROM shopping_list")
    fun getAllShoppingLists(): Flow<List<LocalShoppingList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(vararg shoppingList: LocalShoppingList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(vararg product: LocalProduct)

    @Query(value = "SELECT * FROM shopping_list WHERE id = :shoppingListId")
    suspend fun getShoppingList(shoppingListId : String) : LocalShoppingList

}