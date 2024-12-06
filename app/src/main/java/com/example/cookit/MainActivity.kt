package com.example.cookit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cookit.screens.createRecipe.CreateRecipeScreen
import com.example.cookit.screens.home.HomeScreen
import com.example.cookit.screens.logIn.LogInScreen
import com.example.cookit.screens.recipe.RecipeScreen
import com.example.cookit.screens.shoppingList.ShoppingListScreen
import com.example.cookit.screens.signUp.SignUpScreen
import com.example.cookit.ui.theme.CookItTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CookItTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "logIn"
    ) {
        composable("logIn") { LogInScreen(navController) }
        composable("signUp") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("createRecipe") { CreateRecipeScreen(navController) }
        composable("recipe") { RecipeScreen(navController) }
        composable("shoppingList") { ShoppingListScreen(navController) }
    }
}