package com.example.cookit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CookItApp()
        }
    }
}

@Composable
fun CookItApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "listRecipes"
    ) {
        composable("logIn") { LogInScreen(navController) }
        composable("signUp") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("createRecipe") { CreateRecipeScreen(navController) }
        composable("listRecipes") {
            val recipeViewModel: ListRecipeViewModel = viewModel()
            SearchRecipeScreen(recipeViewModel)
        }
    }
}