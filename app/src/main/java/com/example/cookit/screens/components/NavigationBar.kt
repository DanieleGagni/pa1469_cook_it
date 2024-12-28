package com.example.cookit.screens.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*


@Composable
fun NavigationBar(navController: NavHostController) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var showDeleteAccountConfirmation by remember { mutableStateOf(false) }
    var showPasswordPrompt by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Home
        IconButton(
            onClick = {
                navController.navigate("home")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home Icon",
                tint = Color.Unspecified
            )
        }

        // Shopping list
        IconButton(
            onClick = {
                navController.navigate("shoppingList")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stats),
                contentDescription = "Shopping List Icon",
                tint = Color.Unspecified
            )
        }

        // Create new recipe
        IconButton(
            onClick = {
                navController.navigate("createRecipe")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Icon",
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
        }

        // List favorite recipes
        IconButton(
            onClick = {
                retrieveFavoriteRecipes { favoriteRecipeIds ->
                    navController.navigate("listRecipes?ids=${favoriteRecipeIds.joinToString(",")}")
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Favorite Icon",
                tint = Color.Unspecified
            )
        }

        // Settings Icon with Dropdown Menu
        Box {
            IconButton(
                onClick = { isMenuExpanded = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings Icon",
                    tint = Color.Unspecified
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.background(Color(0xFFF5F5F5))
            ) {

                DropdownMenuItem(
                    text = { Text("My recipes") },
                    onClick = {
                        isMenuExpanded = false
                        fetchUserCreatedRecipes { recipeIds ->
                            navController.navigate("listRecipes?ids=${recipeIds.joinToString(",")}")
                        }
                    }
                )

                DropdownMenuItem(
                    text = { Text("Log Out") },
                    onClick = {
                        isMenuExpanded = false
                        logOut(navController)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete account") },
                    onClick = {
                        isMenuExpanded = false
                        showDeleteAccountConfirmation = true
                    }
                )
            }
        }
    }

    // Confirmation Dialog
    if (showDeleteAccountConfirmation) {
        ConfirmationDialog(
            onDismiss = { showDeleteAccountConfirmation = false },
            onConfirm = {
                showDeleteAccountConfirmation = false
                showPasswordPrompt = true
            }
        )
    }

    // Show Password Input Dialog
    if (showPasswordPrompt) {
        PasswordInputDialog(
            password = password,
            onPasswordChange = { password = it },
            onDismissRequest = { showPasswordPrompt = false },
            onConfirm = {
                showPasswordPrompt = false
                deleteAccount(navController, password)
            }
        )
    }
}

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    LaunchedEffect(Unit) {
        // Ensures dialog remains open despite dropdown dismissal side effects
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Delete Account") },
        text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFFF5F5F5)
    )
}

@Composable
fun PasswordInputDialog(
    password: String,
    onPasswordChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Re-enter Password") },
        text = {
            Column {
                Text("Please enter your password to confirm account deletion.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
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

fun logOut(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    auth.signOut()
    navController.navigate("logIn") {
        popUpTo(navController.graph.startDestinationId) { inclusive = true }
    }
}

fun deleteAccount(navController: NavHostController, password: String) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    if (user != null && user.email != null) {
        val credential = EmailAuthProvider.getCredential(user.email!!, password)

        // Re-authenticate the user
        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    val userId = user.uid

                    // Delete the user's account
                    user.delete()
                        .addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                // Delete shopping list and favorites
                                deleteUserData(userId, db) {
                                    navController.navigate("signUp") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
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

// Function to delete user's shopping list and favorites
fun deleteUserData(userId: String, db: FirebaseFirestore, onComplete: () -> Unit) {
    val shoppingListRef = db.collection("shoppingLists").document(userId)
    val favoritesRef = db.collection("favorites").document(userId)

    // Use a batch to delete both documents atomically
    val batch = db.batch()
    batch.delete(shoppingListRef)
    batch.delete(favoritesRef)

    batch.commit()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DeleteUserData", "User data successfully deleted")
            } else {
                Log.e("DeleteUserData", "Failed to delete user data", task.exception)
            }
            onComplete()
        }
}
