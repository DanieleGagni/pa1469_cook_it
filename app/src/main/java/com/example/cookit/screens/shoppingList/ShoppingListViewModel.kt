package com.example.cookit.screens.shoppingList

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class ShoppingItem(
    val entry: String,
    val done: Boolean
)

class ShoppingListViewModel(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    private val _shoppingList = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val shoppingList: StateFlow<List<ShoppingItem>> = _shoppingList

    fun fetchShoppingList(navController: NavHostController) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
        } else {
            db.collection("shoppingLists")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        createEmptyShoppingList(userId)
                    } else {
                        val rawItems = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                        val items = rawItems.map {
                            ShoppingItem(
                                entry = it["entry"] as String,
                                done = it["done"] as Boolean
                            )
                        }
                        _shoppingList.value = items
                    }
                }
                .addOnFailureListener { e ->
                    println("Error fetching shopping list: $e")
                }
        }
    }

    private fun createEmptyShoppingList(userId: String) {
        val newShoppingList = hashMapOf(
            "items" to emptyList<Map<String, Any>>() // No items initially
        )

        db.collection("shoppingLists")
            .document(userId)
            .set(newShoppingList)
            .addOnSuccessListener {
                println("Created empty shopping list for user: $userId")
                _shoppingList.value = emptyList() // Reflect empty list in UI
            }
            .addOnFailureListener { e ->
                println("Error creating empty shopping list: $e")
            }
    }

    fun addShoppingItem(navController: NavHostController, itemText: String) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        val newItem = ShoppingItem(entry = itemText, done = false)
        _shoppingList.value += newItem

        db.collection("shoppingLists")
            .document(userId)
            .update(
                "items",
                _shoppingList.value.map { mapOf("entry" to it.entry, "done" to it.done) }
            )
            .addOnFailureListener { e ->
                println("Error updating Firestore: $e")
            }
    }

    fun toggleItemStatus(navController: NavHostController, index: Int) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        val updatedList = _shoppingList.value.toMutableList().apply {
            this[index] = this[index].copy(done = !this[index].done)
        }
        _shoppingList.value = updatedList

        db.collection("shoppingLists")
            .document(userId)
            .update(
                "items",
                updatedList.map { mapOf("entry" to it.entry, "done" to it.done) }
            )
            .addOnFailureListener { e ->
                println("Error updating item status in Firestore: $e")
            }
    }

    fun removeShoppingItem(navController: NavHostController, index: Int) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        val updatedList = _shoppingList.value.toMutableList().apply {
            removeAt(index)
        }
        _shoppingList.value = updatedList

        db.collection("shoppingLists")
            .document(userId)
            .update(
                "items",
                updatedList.map { mapOf("entry" to it.entry, "done" to it.done) }
            )
            .addOnFailureListener { e ->
                println("Error removing item from Firestore: $e")
            }
    }

    fun removeAllItems(navController: NavHostController) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        _shoppingList.value = emptyList()

        db.collection("shoppingLists")
            .document(userId)
            .update("items", emptyList<Map<String, Any>>())
            .addOnFailureListener { e ->
                println("Error clearing shopping list in Firestore: $e")
            }
    }
}