package com.example.shoppingwithfriends.features.search

import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.FriendRepository
import com.example.shoppingwithfriends.data.source.local.ContactType
import com.example.shoppingwithfriends.data.source.local.User
import io.mockk.coEvery
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FriendRequestViewModelTest {

    private val friendRepository: FriendRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FriendRequestViewModel

    private val currentUserFlow = MutableStateFlow<com.google.firebase.auth.FirebaseUser?>(null)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { authRepository.currentUser } returns currentUserFlow
        every { friendRepository.getFriendRequests(any()) } returns MutableStateFlow(emptyList())
        
        viewModel = FriendRequestViewModel(friendRepository, authRepository)
        
        // Override the validator to avoid using android.util.Patterns in local JVM tests
        viewModel.validator = { term ->
            when {
                term.contains("@") -> ContactType.EMAIL
                term.all { it.isDigit() || "+-() ".contains(it) } -> ContactType.PHONE
                else -> null
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchForFriend with invalid input sets error`() = runTest {
        viewModel.searchForFriend("invalid-input")
        
        assertEquals("Please enter a valid phone number or email address to search", viewModel.state.value.error)
    }

    @Test
    fun `searchForFriend with valid email returns result`() = runTest {
        val email = "test@example.com"
        val mockUser = User(id = "user123", displayName = "Test User", email = email)
        
        coEvery { friendRepository.searchForFriend(email, any()) } returns mockUser
        every { authRepository.getUserId() } returns "other_user"

        viewModel.searchForFriend(email)
        advanceUntilIdle()

        assertEquals(mockUser, viewModel.state.value.searchResult)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `searchForFriend fails when searching for self`() = runTest {
        val email = "me@example.com"
        val mockUser = User(id = "my_id", displayName = "Me", email = email)
        
        coEvery { friendRepository.searchForFriend(email, any()) } returns mockUser
        every { authRepository.getUserId() } returns "my_id"

        viewModel.searchForFriend(email)
        advanceUntilIdle()

        assertEquals("You cannot add yourself as a friend.", viewModel.state.value.error)
        assertNull(viewModel.state.value.searchResult)
    }

    @Test
    fun `sendFriendRequest updates state with result`() = runTest {
        val targetId = "user123"
        val mockResponse = FriendRepository.FriendRequestResponse.SUCCESS
        
        coEvery { friendRepository.sendFriendRequest(targetId) } returns mockResponse

        viewModel.sendFriendRequest(targetId)
        advanceUntilIdle()

        assertEquals(mockResponse, viewModel.state.value.friendRequestResult)
    }
}
