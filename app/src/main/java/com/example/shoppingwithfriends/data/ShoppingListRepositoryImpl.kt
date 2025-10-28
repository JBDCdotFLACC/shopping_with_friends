package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.models.ShoppingList
import com.example.shoppingwithfriends.models.ShoppingListItem
import java.util.Date
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor() : ShoppingListRepository {
    override suspend fun getListsForUser(userId: Int): List<ShoppingList> {
        return fakeShoppingLists
    }

    override suspend fun addNewShoppingList(
        shoppingList: com.example.shoppingwithfriends.models.ShoppingList,
        userId: Int
    ) {

    }


    override suspend fun addFriendToShoppingList(shoppingListId: Int, friendId: Int) {

    }
    val shoppingList1 = ShoppingList(1, Date(), "First List",
        listOf<ShoppingListItem>(ShoppingListItem(1, "Bread", false),
            ShoppingListItem(2, "fruit", true),
            ShoppingListItem(1, "cereal", false)))

    val shoppingList2 = ShoppingList(2, Date(), "Second List",
        listOf<ShoppingListItem>(ShoppingListItem(2, "apple", false),
            ShoppingListItem(2, "banjo", true),
            ShoppingListItem(1, "cygnus", true)))

    val shoppingList3 = ShoppingList(3, Date(), "Third List",
        listOf<ShoppingListItem>(ShoppingListItem(3, "elephant", false),
            ShoppingListItem(2, "giraffe", false),
            ShoppingListItem(1, "zebra", false)))

    val fakeShoppingLists = listOf<ShoppingList>(shoppingList1, shoppingList2, shoppingList3)

}