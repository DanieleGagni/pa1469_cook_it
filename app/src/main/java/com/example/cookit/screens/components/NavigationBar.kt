package com.example.cookit.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavHostController
import com.example.cookit.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookit.ui.theme.lightGrey


@Composable
fun NavigationBar(navController: NavHostController) {
    val viewModel: NavigationBarViewModel = viewModel()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(lightGrey)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Home
        IconButton(onClick = { navController.navigate("home") }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home Icon",
                tint = Color.Unspecified
            )
        }

        // Shopping list
        IconButton(onClick = { navController.navigate("shoppingList") }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stats),
                contentDescription = "Shopping List Icon",
                tint = Color.Unspecified
            )
        }

        // Create new recipe
        IconButton(onClick = { navController.navigate("createRecipe") }) {
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
                viewModel.retrieveFavoriteRecipes { favoriteRecipeIds ->
                    navController.navigate("listRecipes/$favoriteRecipeIds?isFavorites=true")
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
            IconButton(onClick = { viewModel.toggleMenu() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings Icon",
                    tint = Color.Unspecified
                )
            }

            DropdownMenu(
                expanded = viewModel.isMenuExpanded.value,
                onDismissRequest = { viewModel.toggleMenu() },
                modifier = Modifier.background(lightGrey)
            ) {

                DropdownMenuItem(
                    text = { Text("My recipes") },
                    onClick = {
                        viewModel.toggleMenu()
                        viewModel.fetchUserCreatedRecipes { recipeIds ->
                            navController.navigate("listRecipes/$recipeIds")
                        }
                    }
                )

                DropdownMenuItem(
                    text = { Text("Log Out") },
                    onClick = {
                        viewModel.toggleMenu()
                        viewModel.logOut(navController)
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete account") },
                    onClick = {
                        viewModel.toggleMenu()
                        viewModel.showDeleteAccountConfirmationDialog()
                    }
                )
            }
        }
    }

    // Confirmation Dialog
    if (viewModel.showDeleteAccountConfirmation.value) {
        ConfirmationDialog(
            onDismiss = { viewModel.hideDeleteAccountConfirmationDialog() },
            onConfirm = {
                viewModel.hideDeleteAccountConfirmationDialog()
                viewModel.showPasswordPromptDialog()
            }
        )
    }

    // Show Password Input Dialog
    if (viewModel.showPasswordPrompt.value) {
        PasswordInputDialog(
            password = viewModel.password.value,
            onPasswordChange = { viewModel.onPasswordChange(it) },
            onDismissRequest = { viewModel.hidePasswordPromptDialog() },
            onConfirm = {
                viewModel.hidePasswordPromptDialog()
                viewModel.deleteAccount(navController)
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
        containerColor = lightGrey
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
                    shape = RoundedCornerShape(25.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        containerColor = lightGrey
    )
}
