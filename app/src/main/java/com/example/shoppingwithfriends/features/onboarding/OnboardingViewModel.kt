package com.example.shoppingwithfriends.features.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.UserRepository
import com.example.shoppingwithfriends.data.source.local.User
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
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



    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUser.collect { firebaseUser ->
                refresh()
            }
        }
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        observeAuthState()
    }

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

    fun clearSubmitted(){
        _state.update { it.copy(isSubmitted = false) }
    }

    fun clearError(){
        _state.update { it.copy(error = null) }
    }

    fun refresh(){
        _state.update { it.copy(isLoading = true) }
        Log.i("wxyz", "in refresh!")
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: run {
                clearSubmitted()
                return@launch
            }
            if(userRepository.getUser(user.uid) != null)
            {
                Log.i("wxyz", user.email.toString())
                submitted()
            }
            else{
                _state.update { it.copy(hint = user.displayName ?: user.email ?: "", isLoading = false) }
            }
        }
    }

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phoneNumber).matches()
    }

    fun onSubmitUser(){
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: run {
                //TODO actually make us return to login.  We should never hit this but I think it is important to check anyways.
                returnToLogin()
                return@launch
            }
            val phoneNumber = state.value.phoneNumber
            if(!isPhoneNumberValid(phoneNumber) && phoneNumber.isNotBlank()){
                _state.update {it.copy(error = "Invalid Phone Number", isLoading = false)}
            }
            else{
                val newUser = User(id = user.uid,
                    displayName = state.value.displayName.ifEmpty { user.email.toString() },
                    email = user.email.toString(),
                    phoneNumber = phoneNumber.ifBlank { "" })
                userRepository.addUser(newUser)
                submitted()
            }

        }
    }
}
