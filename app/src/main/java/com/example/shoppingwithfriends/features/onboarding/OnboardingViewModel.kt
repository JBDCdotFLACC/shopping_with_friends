package com.example.shoppingwithfriends.features.onboarding

import android.util.Log
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


    init {
        refresh()
    }

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

    fun clearError(){
        Log.i("wxyz", "clearing the error")
        _state.update { it.copy(error = null) }
    }

    fun refresh(){
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: run {
                returnToLogin()
                return@launch
            }
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

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phoneNumber).matches()
    }

    fun onSubmitUser(){
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: run {
                //TODO actually make us return to login.  We should never hit this but I think it is important to check anyways.
                returnToLogin()
                return@launch
            }
            val phoneNumber = state.value.phoneNumber
            if(!isPhoneNumberValid(phoneNumber) && phoneNumber.isNotBlank()){
                Log.i("wxyz", "Submitting a bad phone number....")
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
