package com.example.cookit.screens.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


class NavigationBarViewModel : ViewModel() {

    private val _isMenuExpanded = mutableStateOf(false)
    val isMenuExpanded: State<Boolean> = _isMenuExpanded

    private val _showDeleteAccountConfirmation = mutableStateOf(false)
    val showDeleteAccountConfirmation: State<Boolean> = _showDeleteAccountConfirmation

    private val _showPasswordPrompt = mutableStateOf(false)
    val showPasswordPrompt: State<Boolean> = _showPasswordPrompt

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    fun toggleMenu() {
        _isMenuExpanded.value = !_isMenuExpanded.value
    }

    fun showDeleteAccountConfirmationDialog() {
        _showDeleteAccountConfirmation.value = true
    }

    fun hideDeleteAccountConfirmationDialog() {
        _showDeleteAccountConfirmation.value = false
    }

    fun showPasswordPromptDialog() {
        _showPasswordPrompt.value = true
    }

    fun hidePasswordPromptDialog() {
        _showPasswordPrompt.value = false
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun logOut(navController: NavHostController) {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        navController.navigate("logIn") {
            popUpTo(0) { inclusive = true }
        }
    }

    fun deleteAccount(navController: NavHostController) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, _password.value)

            // Re-authenticate the user
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    val userId = user.uid

                    // Delete the user's account
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            // Delete shopping list and favorites
                            deleteUserData(userId, db) {
                                navController.navigate("signUp") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            Log.e("DeleteAccount", "Failed to delete account", deleteTask.exception)
                        }
                    }
                } else {
                    Log.e("Reauthenticate", "Re-authentication failed", reauthTask.exception)
                }
            }
        }
    }

    private fun deleteUserData(userId: String, db: FirebaseFirestore, onComplete: () -> Unit) {
        val shoppingListRef = db.collection("shoppingLists").document(userId)
        val favoritesRef = db.collection("favorites").document(userId)

        // Use a batch to delete both documents atomically
        val batch = db.batch()
        batch.delete(shoppingListRef)
        batch.delete(favoritesRef)

        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DeleteUserData", "User data successfully deleted")
            } else {
                Log.e("DeleteUserData", "Failed to delete user data", task.exception)
            }
            onComplete()
        }
    }

    fun retrieveFavoriteRecipes(onResult: (List<String>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("favorites").document(userId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val recipeIds = document.get("recipes") as? List<String> ?: emptyList()
                        onResult(recipeIds)
                    } else {
                        onResult(emptyList())
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    onResult(emptyList())
                }
        } else {
            // Handle case where user is not authenticated
            onResult(emptyList())
        }
    }

    fun fetchUserCreatedRecipes(onResult: (List<String>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("recipes")
                .whereEqualTo("createdBy", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val recipeIds = querySnapshot.documents.mapNotNull { it.id }
                    onResult(recipeIds)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    onResult(emptyList())
                }
        } else {
            // Handle case where user is not authenticated
            onResult(emptyList())
        }
    }
}

