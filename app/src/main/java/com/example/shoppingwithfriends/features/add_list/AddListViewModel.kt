package com.example.shoppingwithfriends.features.add_list

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

@HiltViewModel
class AddListViewModel @Inject constructor(private val repo: ShoppingListRepository): ViewModel() {
    var listName by mutableStateOf("")
        private set

    fun onListNameChanged(newValue: String) {
        listName = newValue
    }

    var isSubmitting by mutableStateOf(false)
        private set

    private val _events = MutableSharedFlow<FormEvent>()
    val events = _events.asSharedFlow()

    fun submit() {
        viewModelScope.launch {
            isSubmitting = true
            try {
                val newId = UUID.randomUUID().toString()
                repo.addNewShoppingList(LocalShoppingList(
                    newId, listName,
                    Date().time,
                    "1"))
                _events.emit(FormEvent.Success(newId))
            } catch (e: Exception) {
                _events.emit(FormEvent.Error("Failed to save"))
            } finally {
                listName = ""
                isSubmitting = false
            }
        }
    }
}
sealed interface FormEvent {
    data class Success(val id: String) : FormEvent
    data class Error(val message: String) : FormEvent
}
