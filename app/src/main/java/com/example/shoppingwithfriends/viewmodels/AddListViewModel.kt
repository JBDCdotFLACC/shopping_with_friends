package com.example.shoppingwithfriends.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.shoppingwithfriends.data.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AddListViewModel @Inject constructor(private val repo: ShoppingListRepository): ViewModel() {
    var listName by mutableStateOf("")
        private set

    fun onListNameChanged(newValue: String) {
        listName = newValue
    }
}