package com.example.cookit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cookit.screens.listRecipes.ListRecipesScreen
import com.example.cookit.screens.components.Recipe
import com.example.cookit.screens.editRecipe.EditRecipeScreen
import com.google.gson.Gson

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
        startDestination = "logIn",
    ) {
        composable("logIn") { LogInScreen(navController) }
        composable("signUp") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("createRecipe") { CreateRecipeScreen(navController) }
        composable(
            route = "showRecipe/{recipe}",
            arguments = listOf(navArgument("recipe") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeJson = backStackEntry.arguments?.getString("recipe")
            val recipe = Gson().fromJson(recipeJson, Recipe::class.java)
            RecipeScreen(navController, recipe)
        }
        composable("shoppingList") { ShoppingListScreen(navController) }

        composable(
            route = "listRecipes?ids={ids}",
            arguments = listOf(navArgument("ids") { type = NavType.StringType })
        ) { backStackEntry ->
            val ids = backStackEntry.arguments?.getString("ids")
            val recipeIds = ids?.split(",") ?: emptyList()
            ListRecipesScreen(navController, recipeIds)
        }

        composable("editRecipe/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            recipeId?.let {
                EditRecipeScreen(navController, recipeId)
            }
        }
    }
}

// when navigating to showRecipe, the call must look like this:

// val recipeJson = Uri.encode(Gson().toJson(testRecipe))
// navController.navigate("showRecipe/$recipeJson")

// ... where testRecipe is your recipe object
