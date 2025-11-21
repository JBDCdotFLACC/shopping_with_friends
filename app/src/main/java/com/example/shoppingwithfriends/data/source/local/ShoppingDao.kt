package com.example.shoppingwithfriends.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM product WHERE id = :listId")
    suspend fun getProductList(listId: String): List<LocalProduct>

    @Query("UPDATE product SET isChecked = :checked WHERE id = :productId")
    suspend fun updateCompleted(productId: String, checked: Boolean)

    @Query("DELETE FROM product WHERE id = :productId")
    suspend fun deleteById(productId: String): Int

    @Query(value = "SELECT * FROM shopping_list")
    suspend fun getShoppingLists(): List<LocalShoppingList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(vararg shoppingList: LocalShoppingList)

}