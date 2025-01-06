package com.example.cookit

import android.content.Context
import androidx.navigation.NavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cookit.screens.shoppingList.ShoppingListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ShoppingListViewModelInstrumentationTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var firebaseUser: FirebaseUser

    @Before
    fun setup() {
        // Initialize FirebaseAuth and Firestore instances
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize FirebaseUser (mocked user for testing)
        firebaseUser = mock()

        // Assuming the FirebaseAuth user is logged in with mock credentials
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn("mockUserId")

        // Initialize your ViewModel, passing Firebase dependencies
        viewModel = ShoppingListViewModel(firestore, firebaseAuth)
    }

    @Test
    fun `addShoppingItem should add item to the list`() = runTest {
        // Add an item to Firestore through the ViewModel
        val navController = mock<NavHostController>()
        viewModel.addShoppingItem(navController, "Milk")

        // Check that the item has been added (viewModel should update accordingly)
        val shoppingList = viewModel.shoppingList.value
        assertNotNull(shoppingList)
        assertEquals(1, shoppingList.size)
        assertEquals("Milk", shoppingList[0].entry)
        assertFalse(shoppingList[0].done)
    }

    @After
    fun tearDown() {
        // Cleanup if necessary (e.g., remove test data from Firestore)
    }
}
