package com.example.shoppingwithfriends.features.homescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repo: ShoppingListRepository): ViewModel() {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _events = MutableSharedFlow<AddListEvent>()
    val events = _events.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val shoppingLists: StateFlow<List<LocalShoppingList>> =
        repo.getAllListsForUser().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state: StateFlow<UiState> = _state

    init {
        Log.d("HomeVM", "init VM ${this.hashCode()}")
        refresh()
    }

    fun submit(listName : String) {
        viewModelScope.launch {
                val newId = repo.addNewShoppingList(Date().time, listName)
                _events.emit(AddListEvent.Success(newId))
        }
    }

    fun deleteShoppingList(listId : String){
        viewModelScope.launch {
            repo.deleteList(listId)
        }
    }

    fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.getAllListsForUser() }
            .onSuccess { list -> _state.update { val newState = it.copy(isLoading = false)
                newState }
            }
            .onFailure { e -> _state.update { val newState = it.copy(isLoading = false, error = e.message)
                newState }
            }
    }
}

sealed interface AddListEvent {
    data class Success(val id: String) : AddListEvent
    data class Error(val message: String) : AddListEvent
}