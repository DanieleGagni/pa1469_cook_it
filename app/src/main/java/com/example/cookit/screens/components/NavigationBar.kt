package com.example.cookit.screens.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.Recipe
import com.google.gson.Gson


// Barra inferior de navegación
@Composable
fun NavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .navigationBarsPadding(), // Reducir padding vertical
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
                //---SHOULD GO TO FAVORITES SCREEN BUT FOR NOW IT GOES TO RECIPE SCREEN TO SEE WHAT HAPPENS
//                val testRecipe = Recipe(
//                    title = "Test Recipe",
//                    estimatedTime = 30,
//                    ingredients = listOf("1 cup flour", "2 eggs", "1 cup milk"),
//                    serves = 4,
//                    steps = listOf("Mix ingredients", "Pour into pan", "Bake at 180°C for 25 minutes"),
//                    type = "Dessert",
//                )

                navController.navigate("listRecipes")

                //val recipeJson = Uri.encode(Gson().toJson(testRecipe))
                //navController.navigate("showRecipe/$recipeJson")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Favorite Icon",
                tint = Color.Unspecified
            )
        }

        // Settings Icon with Dropdown Menu
        var isMenuExpanded by remember { mutableStateOf(false) }
        var showDeleteAccountConfirmation by remember { mutableStateOf(false) }

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
                    text = { Text("Recipes created") },
                    onClick = {
                        isMenuExpanded = false
                        // Handle recipes created action
                        navController.navigate("listRecipes")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Log Out") },
                    onClick = {
                        isMenuExpanded = false
                        // Handle logout action
                        navController.navigate("login")
                    }
                )


                DropdownMenuItem(
                    text = { Text("Delete account") },
                    onClick = {
                        isMenuExpanded = true
                        showDeleteAccountConfirmation = true
                    }
                )
                // Confirmation Dialog
                if (showDeleteAccountConfirmation) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showDeleteAccountConfirmation = false },
                        title = {
                            Text(text = "Delete Account")
                        },
                        text = {
                            Text("Are you sure you want to delete your account? This action cannot be undone.")
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    isMenuExpanded = false
                                    showDeleteAccountConfirmation = false
                                    // Navigate to the sign-up screen or handle delete logic here
                                    navController.navigate("signUp")
                                }
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    isMenuExpanded = false
                                    showDeleteAccountConfirmation = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        },

                        containerColor = Color(0xFFF5F5F5)
                    )
                }
            }
        }
    }
}
