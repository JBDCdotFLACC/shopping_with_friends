package com.example.shoppingwithfriends.data

import com.example.shoppingwithfriends.auth.AuthRepository
import com.example.shoppingwithfriends.data.source.local.*
import com.example.shoppingwithfriends.data.sync.SyncWorkManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FriendRepositoryTest {

    private val localDataSource: ShoppingDao = mockk(relaxed = true)
    private val firestore: FirebaseFirestore = mockk()
    private val authRepository: AuthRepository = mockk()
    private val syncWorkManager: SyncWorkManager = mockk(relaxed = true)

    private lateinit var repository: FriendRepositoryImpl

    @Before
    fun setup() {
        repository = FriendRepositoryImpl(localDataSource, firestore, authRepository, syncWorkManager)
        every { authRepository.getUserId() } returns "my_user_id"
    }

    @Test
    fun `searchForFriend returns local result if available`() = runTest {
        val email = "test@example.com"
        val mockUser = User(id = "user123", email = email)
        coEvery { localDataSource.getUserByEmail(email) } returns mockUser

        val result = repository.searchForFriend(email, ContactType.EMAIL)

        assertEquals(mockUser, result)
    }

    @Test
    fun `sendFriendRequest returns ALREADY_FRIEND if friendship exists locally`() = runTest {
        val targetId = "friend_123"
        coEvery { localDataSource.getFriendship("my_user_id", targetId) } returns mockk()

        val result = repository.sendFriendRequest(targetId)

        assertEquals(FriendRepository.FriendRequestResponse.ALREADY_FRIEND, result)
    }

    @Test
    fun `sendFriendRequest returns SUCCESS and schedules sync when no prior request exists`() = runTest {
        val targetId = "new_friend_123"
        
        // Mocking no existing friendship or requests
        coEvery { localDataSource.getFriendship(any(), any()) } returns null
        coEvery { localDataSource.getFriendRequest(any(), any()) } returns null
        
        val mockCollection: CollectionReference = mockk()
        val mockQuery: Query = mockk()
        val mockTask: Task<QuerySnapshot> = mockk()
        val mockSnapshot: QuerySnapshot = mockk()
        
        every { firestore.collection(any<String>()) } returns mockCollection
        every { mockCollection.whereEqualTo(any<String>(), any()) } returns mockQuery
        every { mockQuery.whereEqualTo(any<String>(), any()) } returns mockQuery
        every { mockQuery.get() } returns mockTask
        
        // Mock Task behavior for compatibility with .await()
        every { mockTask.isComplete } returns true
        every { mockTask.exception } returns null
        every { mockTask.isCanceled } returns false
        every { mockTask.result } returns mockSnapshot
        
        // Mocking an empty result set
        every { mockSnapshot.isEmpty } returns true
        every { mockSnapshot.iterator() } returns mutableListOf<QueryDocumentSnapshot>().iterator()

        val result = repository.sendFriendRequest(targetId)

        assertEquals(FriendRepository.FriendRequestResponse.SUCCESS, result)
        coVerify { 
            localDataSource.insertFriendRequest(any())
            localDataSource.insertPendingOp(any())
            syncWorkManager.scheduleSync()
        }
    }
}
