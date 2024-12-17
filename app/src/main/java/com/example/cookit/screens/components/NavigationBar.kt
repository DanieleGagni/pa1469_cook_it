package com.example.cookit.screens.components

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
        IconButton(
            onClick = {
                //SHOULD GET LIST OF USER'S FAVOURITE RECIPES
                navController.navigate("listRecipes")
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
                    text = { Text("Log Out") },
                    onClick = {
                        isMenuExpanded = false
                        // Handle logout action
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}
