package com.example.shoppingwithfriends.features.login

import app.cash.turbine.test
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val auth: FirebaseAuth = mockk()
    private val webClientId = "test_web_client_id"
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(GoogleAuthProvider::class)
        viewModel = LoginViewModel(webClientId, auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(GoogleAuthProvider::class)
    }

    @Test
    fun `firebaseAuthWithGoogle updates state to SignedIn on success`() = runTest {
        val idToken = "token"
        val mockCredential = mockk<AuthCredential>()
        val mockUser = mockk<FirebaseUser>()
        val mockTask = mockk<Task<AuthResult>>()

        every { GoogleAuthProvider.getCredential(idToken, null) } returns mockCredential
        every { auth.signInWithCredential(mockCredential) } returns mockTask
        every { mockTask.isComplete } returns true
        every { mockTask.exception } returns null
        every { mockTask.isCanceled } returns false
        every { mockTask.result } returns mockk()
        every { auth.currentUser } returns mockUser

        viewModel.uiState.test {
            assertEquals(LoginState.Idle, awaitItem())
            
            viewModel.firebaseAuthWithGoogle(idToken)
            
            assertEquals(LoginState.Loading, awaitItem())
            assertEquals(LoginState.SignedIn(mockUser), awaitItem())
        }
    }

    @Test
    fun `firebaseAuthWithGoogle updates state to Error on failure`() = runTest {
        val idToken = "token"
        val exception = RuntimeException("Auth failed")
        
        every { GoogleAuthProvider.getCredential(idToken, null) } throws exception

        viewModel.uiState.test {
            assertEquals(LoginState.Idle, awaitItem())
            
            viewModel.firebaseAuthWithGoogle(idToken)
            
            assertEquals(LoginState.Loading, awaitItem())
            val errorState = awaitItem()
            assertTrue(errorState is LoginState.Error)
            assertEquals(exception, (errorState as LoginState.Error).throwable)
        }
    }
}
