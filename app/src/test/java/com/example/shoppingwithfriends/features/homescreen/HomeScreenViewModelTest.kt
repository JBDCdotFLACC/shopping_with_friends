package com.example.shoppingwithfriends.features.homescreen

import app.cash.turbine.test
import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private val repo: ShoppingListRepository = mockk(relaxed = true)
    private val testDispatcher = kotlinx.coroutines.test.UnconfinedTestDispatcher()
    private lateinit var viewModel: HomeScreenViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repo.pullRemoteDataForUser() } returns Unit
        every { repo.getAllListsForUser() } returns flowOf(emptyList())
        
        viewModel = HomeScreenViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `shoppingLists flow reflects repository data`() = runTest {
        val mockLists = listOf(
            LocalShoppingList(id = "1", name = "List 1", date = 100L, owner = "user1")
        )
        every { repo.getAllListsForUser() } returns flowOf(mockLists)

        viewModel = HomeScreenViewModel(repo)

        viewModel.shoppingLists.test {
            assertEquals(mockLists, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `submit calls repo and emits Success event`() = runTest {
        val listName = "New Party List"
        val generatedId = "new_id_123"
        coEvery { repo.addNewShoppingList(any(), listName) } returns generatedId

        viewModel.events.test {
            viewModel.submit(listName)
            assertEquals(AddListEvent.Success(generatedId), awaitItem())
        }
    }

    @Test
    fun `deleteShoppingList calls repo deleteList`() = runTest {
        val listId = "delete_me"
        viewModel.deleteShoppingList(listId)
        advanceUntilIdle()
        coVerify { repo.deleteList(listId) }
    }

}
