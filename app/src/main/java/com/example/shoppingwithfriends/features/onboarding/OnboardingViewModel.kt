package com.example.shoppingwithfriends.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.UserRepository
import com.example.shoppingwithfriends.data.source.local.User
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val authRepository: AuthRepository,
    private val userRepository : UserRepository) : ViewModel() {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val displayName: String = "",
        val isSubmitted: Boolean = false,
        val returnToLogin : Boolean = false,
        val phoneNumber : String = "",
        val hint: String = ""
    )



    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun onDisplayNameChanged(newValue: String) {
        _state.update { it.copy(displayName = newValue) }
    }

    fun onPhoneNumberChanged(newValue : String) {
        _state.update {it.copy(phoneNumber = newValue)}
    }

    fun returnToLogin(){
        _state.update { it.copy(returnToLogin = true, error = "Error with Login") }
    }

    fun submitted(){
        _state.update {it.copy(isSubmitted = true, isLoading = false)}

    }

    fun refresh(){
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            if(user == null){
                returnToLogin()
                return@launch
            }
            else{
                if(userRepository.getUser(user.uid) != null)
                {
                    submitted()
                }
                else{
                    //TODO check firebase for user
                    _state.update { it.copy(hint = user.displayName ?: user.email ?: "") }
                }
            }
        }
    }

    fun onSubmitUser(){
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            if(user == null){
                returnToLogin()
                return@launch
            }
              else{
                val newUser = User(id = user.uid,
                    displayName = state.value.displayName.ifEmpty { user.email.toString() },
                    email = user.email.toString(),
                    phoneNumber = state.value.phoneNumber)
                submitted()
                userRepository.addUser(newUser)
            }
        }
    }
}