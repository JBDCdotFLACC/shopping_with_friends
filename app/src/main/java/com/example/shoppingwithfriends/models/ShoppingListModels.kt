package com.example.shoppingwithfriends.models

import java.util.Date

data class ShoppingList(val id: Int, val date : Date, val name : String,
                        val list : List<ShoppingListItem>, val owner : String)
data class ShoppingListItem(val id: Int, val content : String, val isChecked : Boolean)