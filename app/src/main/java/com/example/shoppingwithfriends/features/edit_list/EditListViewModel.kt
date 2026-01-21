package com.example.shoppingwithfriends.features.edit_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.source.local.LocalProduct
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

@HiltViewModel
class EditListViewModel @Inject constructor(private val repo: ShoppingListRepository): ViewModel() {

    private val _listId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<LocalProduct>> =
        _listId
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { id : String ->
                repo.getProductList(id)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    data class UiState(
        val isLoading: Boolean = false,
        val listId: String = "",
        val error: String? = null,
        val listName: String = ""
    )

    private val _state = MutableStateFlow(UiState(isLoading = true))
    val state: StateFlow<UiState> = _state

    fun onListNameChanged(newValue: String) {
        _state.update { it.copy(listName = newValue) }
    }

    fun onCheckChanged(productId : String, isChecked : Boolean){
        viewModelScope.launch {
            repo.setProductCheck(productId, isChecked)
        }
    }

    fun onProductNameChanged(productId: String, name : String){
        viewModelScope.launch {
            repo.updateProductName(productId, newName = name)
        }
    }

    fun deleteProduct(productId: String){
        viewModelScope.launch {
            repo.deleteProduct(productId)
        }
    }

    fun addItem(){
        viewModelScope.launch {
            val newId = UUID.randomUUID().toString()
            repo.addProduct(LocalProduct(
                id = newId,
                content = "",
                parent =_listId.value ?: return@launch,
                isChecked = false))
        }
    }

    fun onPause() {
        viewModelScope.launch {
            repo.updateListName(state.value.listId, state.value.listName)
        }
    }

    fun refresh(shoppingListId: String) = viewModelScope.launch {
        _listId.value = shoppingListId
        _state.update { it.copy(isLoading = true, error = null) }

        runCatching { repo.getShoppingList(shoppingListId) }
            .onSuccess { shoppingList ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        listName = shoppingList.name,
                        listId = shoppingListId,
                        error = null
                    )
                }
            }
            .onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
    }
}