package com.example.shoppingwithfriends.data

import app.cash.turbine.test
import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.source.local.LocalShoppingList
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.sync.SyncWorkManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ShoppingListRepositoryTest {

    private val localDataSource: ShoppingDao = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk()
    private val syncWorkManager: SyncWorkManager = mockk(relaxed = true)
    private val fireBaseFireStore: FirebaseFirestore = mockk()
    
    private lateinit var repository: ShoppingListRepositoryImpl
    private val currentUserFlow = MutableStateFlow<FirebaseUser?>(null)

    @Before
    fun setup() {
        every { authRepository.currentUser } returns currentUserFlow
        repository = ShoppingListRepositoryImpl(
            localDataSource,
            authRepository,
            syncWorkManager,
            fireBaseFireStore
        )
    }

    @Test
    fun `getAllListsForUser returns empty list when user is null`() = runTest {
        currentUserFlow.value = null

        repository.getAllListsForUser().test {
            assertEquals(emptyList<LocalShoppingList>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllListsForUser returns data from local source when user is logged in`() = runTest {
        val mockUser: FirebaseUser = mockk {
            every { uid } returns "test_user_id"
        }
        val mockLists = listOf(
            LocalShoppingList("1", "Groceries", 0L, "test_user_id", false, "v1")
        )
        
        currentUserFlow.value = mockUser
        every { localDataSource.getAllShoppingListsForUser("test_user_id") } returns flowOf(mockLists)

        repository.getAllListsForUser().test {
            assertEquals(mockLists, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addNewShoppingList inserts into local DB and schedules sync`() = runTest {
        val mockUser: FirebaseUser = mockk {
            every { uid } returns "test_user_id"
        }
        currentUserFlow.value = mockUser
        
        val listName = "New List"
        val date = 123456789L
        
        repository.addNewShoppingList(date, listName)

        coVerify { 
            localDataSource.insertShoppingList(any())
            localDataSource.insertPendingOp(any())
            syncWorkManager.scheduleSync()
        }
    }
}
