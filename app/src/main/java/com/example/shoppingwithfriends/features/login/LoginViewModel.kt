package com.example.shoppingwithfriends.features.login


import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingwithfriends.features.homescreen.AddListEvent
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@HiltViewModel
class LoginViewModel
@Inject constructor(@Named("webClientId") private val webClientId: String,
                    private val auth: FirebaseAuth) : ViewModel() {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(webClientId)
        .setFilterByAuthorizedAccounts(true)
        .build()


    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun firebaseAuthWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _uiState.value = LoginState.SignedIn(auth.currentUser)
            } catch (e: Exception) {
                _uiState.value = LoginState.Error(e)
            }
        }
    }
}


sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data class SignedIn(val user: FirebaseUser?) : LoginState
    data class Error(val throwable: Throwable) : LoginState
}