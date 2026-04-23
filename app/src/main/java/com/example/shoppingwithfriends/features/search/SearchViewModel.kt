package com.example.shoppingwithfriends.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.data.FriendRepository
import com.example.shoppingwithfriends.data.source.local.ContactType
import com.example.shoppingwithfriends.data.source.local.User
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(private val friendRepository: FriendRepository) : ViewModel() {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val result: User? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state


    fun searchForFriend(searchTerm : String){
        _state.update { it.copy(isLoading = true) }
        when{
            android.util.Patterns.PHONE.matcher(searchTerm).matches() -> {
                viewModelScope.launch {
                    val result = friendRepository.searchForFriend(searchTerm, ContactType.PHONE)
                    if(result == null){
                        _state.update { it.copy(error = "No user found with this phone number."
                            , isLoading = false) }
                    }
                    else{
                        _state.update { it.copy(result = result, isLoading = false) }
                    }
                }
            }
            android.util.Patterns.EMAIL_ADDRESS.matcher(searchTerm).matches() -> {
                viewModelScope.launch {
                    val result = friendRepository.searchForFriend(searchTerm, ContactType.EMAIL)
                    if(result == null){
                        _state.update { it.copy(error = "No user found with this email address."
                            , isLoading = false) }
                    }
                    else{
                        _state.update { it.copy(result = result, isLoading = false) }
                    }
                }
            }
            else ->{
                _state.update { it.copy(error = "Please enter a valid phone number or email address to search"
                    , isLoading = false) }
            }
        }
    }
}