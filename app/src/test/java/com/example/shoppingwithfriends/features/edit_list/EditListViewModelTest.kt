package com.example.shoppingwithfriends.features.edit_list

import app.cash.turbine.test
import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.source.local.LocalProduct
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditListViewModelTest {

    private val repo: ShoppingListRepository = mockk()
    private val testDispatcher = kotlinx.coroutines.test.UnconfinedTestDispatcher()
    private lateinit var viewModel: EditListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EditListViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh updates state with list details`() = runTest {
        val listId = "list_123"
        val mockList = LocalShoppingList(id = listId, name = "My List", date = 0L, owner = "user1")

        // 2. To test the loading state, we need the repo to "hang" or delay
        // Create a trigger to control when the repo returns
        val loadDeferred = kotlinx.coroutines.CompletableDeferred<LocalShoppingList>()
        coEvery { repo.getShoppingList(listId) } coAnswers { loadDeferred.await() }
        every { repo.getProductList(listId) } returns flowOf(emptyList())

        // 3. Start the refresh
        viewModel.refresh(listId)

        // With UnconfinedTestDispatcher, the coroutine starts immediately
        // and stops at 'loadDeferred.await()'
        assertTrue("Should be loading while repo is working", viewModel.state.value.isLoading)

        // 4. Complete the repo call
        loadDeferred.complete(mockList)

        // Now it should be finished
        assertFalse("Should stop loading after repo returns", viewModel.state.value.isLoading)
        assertEquals("My List", viewModel.state.value.listName)
    }

    @Test
    fun `products flow reflects repo data`() = runTest {
        val listId = "list_123"
        val mockProducts = listOf(
            LocalProduct(id = "p1", content = "Apples", parent = listId),
            LocalProduct(id = "p2", content = "Milk", parent = listId, isChecked = true)
        )

        every { repo.getProductList(listId) } returns flowOf(mockProducts)
        coEvery { repo.getShoppingList(listId) } returns mockk(relaxed = true)


        viewModel.products.test {
            assertEquals(emptyList<LocalProduct>(), awaitItem()) // Initial value
            viewModel.refresh(listId)
            assertEquals(mockProducts, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCheckChanged calls repo and updates focus`() = runTest {
        val productId = "p1"
        coEvery { repo.setProductCheck(productId, false) } returns Unit

        viewModel.onCheckChanged(productId, false)
        advanceUntilIdle()

        coVerify { repo.setProductCheck(productId, false) }
        assertEquals(productId, viewModel.focusProductId.value)
    }

    @Test
    fun `addItem calls repo addProduct`() = runTest {
        val listId = "list_123"
        viewModel.refresh(listId)
        advanceUntilIdle()

        coEvery { repo.addProduct(any()) } returns Unit
        
        viewModel.addItem()
        advanceUntilIdle()

        coVerify { repo.addProduct(match { it.parent == listId }) }
        assertTrue(viewModel.focusProductId.value != null)
    }
}
