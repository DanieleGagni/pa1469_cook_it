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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*
import org.mockito.kotlin.*
import org.mockito.kotlin.any as kotlinAny

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingListViewModelIntegrationTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentReference: DocumentReference
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var viewModel: ShoppingListViewModel

    @Before
    fun setup() = runBlocking {
        Dispatchers.setMain(testDispatcher)

        // Initialize FirebaseAuth and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Sign in with the real user credentials
        val authResult = firebaseAuth.signInWithEmailAndPassword("phil@gamil.com", "12345678").await()

        // The signed-in user will now be available
        firebaseUser = authResult.user ?: throw IllegalStateException("User sign-in failed")

        // Create a reference to the user's shopping list document
        documentReference = firestore.collection("shoppingLists").document(firebaseUser.uid)

        // Initialize ViewModel with the FirebaseAuth and Firestore
        viewModel = ShoppingListViewModel(firestore, firebaseAuth)
    }

    @After
    fun tearDown() = runBlocking {
        // Clean up Firestore test data
        // In this case, we assume you're using a test document reference (mockUserId)
        documentReference.delete().await()

        // Sign out the test user
        firebaseAuth.signOut()

        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `addShoppingItem should add item to the list`() = runTest {
        // Mock NavController
        val navController = mock<NavHostController>()

        // Add shopping item
        viewModel.addShoppingItem(navController, "Bread")

        // Verify the shopping list is updated
        viewModel.shoppingList.test {
            val list = awaitItem()
            Assert.assertEquals(1, list.size)
            Assert.assertEquals("Bread", list[0].entry)
            Assert.assertFalse(list[0].done)
        }
    }

    @Test
    fun `fetchShoppingList should populate shoppingList when document exists in Firestore`() = runTest {
        // Set up a Firestore document with some data
        val shoppingListData = mapOf(
            "items" to listOf(
                mapOf("entry" to "Milk", "done" to false),
                mapOf("entry" to "Eggs", "done" to true)
            )
        )

        // Store the shopping list in Firestore (emulator or actual Firestore)
        documentReference.set(shoppingListData).await()

        // Call fetchShoppingList to trigger fetching data from Firestore
        val navController = mock<NavHostController>()
        viewModel.fetchShoppingList(navController)

        // Verify that the ViewModel's shopping list is populated
        viewModel.shoppingList.test {
            val list = awaitItem()
            Assert.assertEquals(2, list.size)
            Assert.assertEquals("Milk", list[0].entry)
            Assert.assertFalse(list[0].done)
        }
    }
}