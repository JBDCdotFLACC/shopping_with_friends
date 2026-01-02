package com.example.shoppingwithfriends.features.edit_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditListViewModel @Inject constructor(private val repo: ShoppingListRepository): ViewModel() {
    data class UiState(
        val isLoading: Boolean = false,
        val listId: String = "",
        val items: List<LocalProduct> = emptyList(),
        val error: String? = null,
        val listName: String = ""
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state: StateFlow<UiState> = _state

    fun onListNameChanged(newValue: String) {
        _state.update { it.copy(listName = newValue) }
    }


    fun refresh(shoppingListId : String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        Log.i("wxyz", "We are refreshing....")
        val result = runCatching {
            coroutineScope {
                val listDeferred = async { repo.getShoppingList(shoppingListId) }
                val productsDeferred = async { repo.getProductList(shoppingListId) }

                val shoppingList = listDeferred.await()
                val products = productsDeferred.await()

                shoppingList to products
            }
        }

        result
            .onSuccess { (shoppingList, products) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        listName = shoppingList.name,
                        items = products,
                        listId = shoppingListId,
                        error = null
                    )
                }
            }
            .onFailure { e ->
                _state.update {
                    Log.i("wxyz", e.toString())
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
    }
}