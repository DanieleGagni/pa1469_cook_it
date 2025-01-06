package com.example.cookit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavHostController
import app.cash.turbine.test
import com.example.cookit.screens.shoppingList.ShoppingListViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*
import org.mockito.kotlin.*
import org.mockito.kotlin.any as kotlinAny

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var documentSnapshot: DocumentSnapshot
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var viewModel: ShoppingListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        firebaseAuth = mock(FirebaseAuth::class.java)
        firestore = mock(FirebaseFirestore::class.java)
        documentReference = mock(DocumentReference::class.java)
        documentSnapshot = mock(DocumentSnapshot::class.java)
        firebaseUser = mock(FirebaseUser::class.java)

        // Mock FirebaseAuth behavior
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // Create a mock CollectionReference
        val collectionReference = mock<CollectionReference>()

        // Stub collection() to return the mock CollectionReference
        `when`(firestore.collection(any())).thenReturn(collectionReference)

        // Stub document() to return the mock DocumentReference
        `when`(collectionReference.document(any())).thenReturn(documentReference)

        // Initialize ViewModel with the mocked Firestore and FirebaseAuth
        viewModel = ShoppingListViewModel(firestore, firebaseAuth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchShoppingList should populate shoppingList when document exists`() = runTest {
        // Mock user authentication
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.uid).thenReturn("mockUserId")

        // Mock Firestore DocumentReference and Task<DocumentSnapshot>
        val mockTask = mock<Task<DocumentSnapshot>>()
        `when`(documentReference.get()).thenReturn(mockTask)

        // Mock the success listener to trigger success callback
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
            successListener.onSuccess(documentSnapshot)
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        // Mock the failure listener to do nothing (no exception thrown)
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

        // Mock the document snapshot to return a valid list
        `when`(documentSnapshot.exists()).thenReturn(true)
        `when`(documentSnapshot.get(kotlinAny<String>())).thenReturn(
            listOf(
                mapOf("entry" to "Milk", "done" to false),
                mapOf("entry" to "Eggs", "done" to true)
            )
        )

        // Mock NavController
        val navController = mock<NavHostController>()

        // Call the method to test
        viewModel.fetchShoppingList(navController)

        // Verify the shopping list is populated
        viewModel.shoppingList.test {
            val list = awaitItem()
            Assert.assertEquals(2, list.size)
            Assert.assertEquals("Milk", list[0].entry)
            Assert.assertFalse(list[0].done)
        }
    }

    @Test
    fun `addShoppingItem should add item to the list`() = runTest {
        // Mock user authentication
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.uid).thenReturn("mockUserId")

        val navController = mock<NavHostController>()

        // Mock Firestore DocumentReference and Task<DocumentSnapshot>
        val mockTask = mock<Task<DocumentSnapshot>>()
        `when`(documentReference.get()).thenReturn(mockTask)

        // Mock the success listener to trigger success callback for get()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
            successListener.onSuccess(documentSnapshot)  // Mocking success callback
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        // Mock Firestore DocumentReference update method to return a Task<Void>
        val mockUpdateTask: Task<Void> = mock()
        `when`(documentReference.update(anyString(), any(), any())).thenReturn(mockUpdateTask)

        // Mock the success listener to trigger success callback for update()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<Void>>(0)
            successListener.onSuccess(null)  // Mocking success callback for update()
            mockUpdateTask
        }.`when`(mockUpdateTask).addOnSuccessListener(any())

        // Mock failure listener to do nothing (no exception thrown)
        `when`(mockUpdateTask.addOnFailureListener(any())).thenReturn(mockUpdateTask)
        `when`(mockTask.addOnFailureListener(any())).thenReturn(mockTask)

        viewModel.addShoppingItem(navController, "Bread")

        viewModel.shoppingList.test {
            val list = awaitItem()
            Assert.assertEquals(1, list.size)
            Assert.assertEquals("Bread", list[0].entry)
            Assert.assertFalse(list[0].done)
        }
    }

    @Test
    fun `toggleItemStatus should change the item's done status`() = runTest {
        // Mock user authentication
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.uid).thenReturn("mockUserId")

        val navController = mock<NavHostController>()

        // Mock Firestore DocumentReference and Task<DocumentSnapshot>
        val mockTask: Task<DocumentSnapshot> = mock()
        `when`(documentReference.get()).thenReturn(mockTask)

        // Mock the success listener to trigger success callback for get()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
            successListener.onSuccess(documentSnapshot)  // Mocking success callback
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        // Mock Firestore DocumentReference update method to return a Task<Void>
        val mockUpdateTask: Task<Void> = mock()
        `when`(documentReference.update(anyString(), any(), any())).thenReturn(mockUpdateTask)

        // Mock the success listener to trigger success callback for update()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<Void>>(0)
            successListener.onSuccess(null)  // Mocking success callback for update()
            mockUpdateTask
        }.`when`(mockUpdateTask).addOnSuccessListener(any())

        // Mock failure listener to do nothing (no exception thrown)
        `when`(mockUpdateTask.addOnFailureListener(any())).thenReturn(mockUpdateTask)

        // Add a sample item
        viewModel.addShoppingItem(navController, "Milk")
        advanceUntilIdle()

        // Toggle the status (this is the method under test)
        viewModel.toggleItemStatus(navController, 0)
        advanceUntilIdle()

        // Verify the item status is updated correctly
        viewModel.shoppingList.test {
            val list = awaitItem()
            Assert.assertEquals(1, list.size)
            Assert.assertEquals("Milk", list[0].entry)
            Assert.assertTrue(list[0].done)  // Ensure the item is marked as done
        }
    }

    @Test
    fun `removeShoppingItem should remove item from the list`() = runTest {
        // Mock user authentication
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.uid).thenReturn("mockUserId")

        val navController = mock<NavHostController>()

        // Mock Firestore DocumentReference and Task<DocumentSnapshot>
        val mockTask: Task<DocumentSnapshot> = mock()
        `when`(documentReference.get()).thenReturn(mockTask)

        // Mock the success listener to trigger success callback for get()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
            successListener.onSuccess(documentSnapshot)  // Mocking success callback
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        // Mock Firestore DocumentReference update method to return a Task<Void> for item removal
        val mockRemoveTask: Task<Void> = mock()
        `when`(documentReference.update(anyString(), any(), any())).thenReturn(mockRemoveTask)

        // Mock the success listener to trigger success callback for remove()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<Void>>(0)
            successListener.onSuccess(null)  // Simulate success callback for remove
            mockRemoveTask
        }.`when`(mockRemoveTask).addOnSuccessListener(any())

        // Mock failure listener to do nothing (no exception thrown)
        `when`(mockRemoveTask.addOnFailureListener(any())).thenReturn(mockRemoveTask)

        // Add sample items to the list
        viewModel.addShoppingItem(navController, "Milk")
        viewModel.addShoppingItem(navController, "Bread")
        advanceUntilIdle()

        // Remove the first item ("Milk")
        viewModel.removeShoppingItem(navController, 0)
        advanceUntilIdle()

        // Verify that "Milk" has been removed and only "Bread" remains
        viewModel.shoppingList.test {
            val list = awaitItem()
            Assert.assertEquals(1, list.size)
            Assert.assertEquals("Bread", list[0].entry)
        }
    }

    @Test
    fun `removeAllItems should clear the list`() = runTest {
        // Mock user authentication
        `when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        `when`(firebaseUser.uid).thenReturn("mockUserId")

        val navController = mock<NavHostController>()

        // Mock Firestore DocumentReference and Task<DocumentSnapshot>
        val mockTask: Task<DocumentSnapshot> = mock()
        `when`(documentReference.get()).thenReturn(mockTask)

        // Mock the success listener to trigger success callback for get()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<DocumentSnapshot>>(0)
            successListener.onSuccess(documentSnapshot)  // Mocking success callback
            mockTask
        }.`when`(mockTask).addOnSuccessListener(any())

        // Mock Firestore DocumentReference update method to return a Task<Void> for clearing the list
        val mockClearTask: Task<Void> = mock()
        `when`(documentReference.update(anyString(), any(), any())).thenReturn(mockClearTask)

        // Mock the success listener to trigger success callback for removeAllItems()
        doAnswer { invocation ->
            val successListener = invocation.getArgument<OnSuccessListener<Void>>(0)
            successListener.onSuccess(null)  // Simulate success callback for clearing the list
            mockClearTask
        }.`when`(mockClearTask).addOnSuccessListener(any())

        // Mock failure listener to do nothing (no exception thrown)
        `when`(mockClearTask.addOnFailureListener(any())).thenReturn(mockClearTask)

        // Add sample items to the list
        viewModel.addShoppingItem(navController, "Milk")
        viewModel.addShoppingItem(navController, "Bread")
        advanceUntilIdle()

        // Clear the entire list
        viewModel.removeAllItems(navController)
        advanceUntilIdle()

        // Verify that the list is now empty
        viewModel.shoppingList.test {
            val list = awaitItem()
            Assert.assertTrue(list.isEmpty())  // Ensure the list is cleared
        }
    }
}