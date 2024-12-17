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
import com.example.cookit.screens.createRecipe.components.Recipe

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
        composable(
            route = "recipe",
            arguments = listOf(navArgument("recipe") { type = NavType.ParcelableType(Recipe::class.java) })
        ) { backStackEntry ->
            val recipe = backStackEntry.arguments?.getParcelable<Recipe>("recipe")
            if (recipe != null) {
                RecipeScreen(navController = navController, recipe = recipe)
            }
        }
        composable("shoppingList") { ShoppingListScreen(navController) }
    }

    // When navigating to the RecipeScreen, adapt this code:
    // (you need to change "searchScreen" to the screen name you are in and your Recipe object
    // needs to be called *recipe* (without the *) for the code to work)

    // navController.navigate("recipe") {
    //    popUpTo("searchScreen") { inclusive = false }
    //    arguments = Bundle().apply {
    //        putParcelable("recipe", recipe)
    //    }
    // }
}