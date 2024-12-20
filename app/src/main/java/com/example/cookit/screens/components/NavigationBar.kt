package com.example.cookit.screens.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
                //---SHOULD GO TO FAVORITES SCREEN BUT FOR NOW IT GOES TO RECIPE SCREEN TO SEE WHAT HAPPENS
                val testRecipe = Recipe(
                    title = "Test Recipe",
                    estimatedTime = 30,
                    ingredients = listOf("1 cup flour", "2 eggs", "1 cup milk"),
                    serves = 4,
                    steps = listOf("Mix ingredients", "Pour into pan", "Bake at 180°C for 25 minutes"),
                    type = "Dessert",
                )

                val recipeJson = Uri.encode(Gson().toJson(testRecipe))
                navController.navigate("showRecipe/$recipeJson")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Favorite Icon",
                tint = Color.Unspecified
            )
        }
        IconButton(onClick = { /* Navegar a Settings */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings Icon",
                tint = Color.Unspecified
            )
        }
    }
}