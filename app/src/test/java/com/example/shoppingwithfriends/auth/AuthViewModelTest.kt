package com.example.shoppingwithfriends.auth

import app.cash.turbine.test
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val authRepository: AuthRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    private val currentUserFlow = MutableStateFlow<FirebaseUser?>(null)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.currentUser } returns currentUserFlow
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `currentUser reflects repository flow`() = runTest {
        val mockUser: FirebaseUser = mockk()
        
        viewModel.currentUser.test {
            assertEquals(null, awaitItem()) // Initial value
            
            currentUserFlow.value = mockUser
            assertEquals(mockUser, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout calls repository logout`() = runTest {
        coEvery { authRepository.logout() } returns Unit

        viewModel.logout()
        advanceUntilIdle()

        coVerify { authRepository.logout() }
    }
}
