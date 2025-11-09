package com.example.shoppingwithfriends.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repo: ShoppingListRepository): ViewModel() {
    data class UiState(
        val isLoading: Boolean = false,
        val items: List<LocalShoppingList> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state: StateFlow<UiState> = _state

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        runCatching { repo.getListsForUser(1) }        // suspend fun; can be offline-first
            .onSuccess { list -> _state.update { it.copy(isLoading = false, items = list) } }
            .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
    }
}