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
        _state.update { it.copy(isLoading = true, error = null) }
        when{
            android.util.Patterns.PHONE.matcher(searchTerm).matches() -> {
                handleSearch(searchTerm, ContactType.PHONE)
            }
            android.util.Patterns.EMAIL_ADDRESS.matcher(searchTerm).matches() -> {
                handleSearch(searchTerm, ContactType.EMAIL)
            }
            else ->{
                _state.update { it.copy(error = "Please enter a valid phone number or email address to search"
                    , isLoading = false) }
            }
        }
    }

    private fun handleSearch(searchTerm : String, contactType: ContactType) {
        val message = if(contactType == ContactType.EMAIL) "No user found with this email address." else "No user found with this phone number."
        viewModelScope.launch {
            val result = friendRepository.searchForFriend(searchTerm, contactType)
            if(result == null){
                _state.update { it.copy(error = message
                    , isLoading = false) }
            }
            else{
                _state.update { it.copy(result = result, isLoading = false) }
            }
        }
    }

}