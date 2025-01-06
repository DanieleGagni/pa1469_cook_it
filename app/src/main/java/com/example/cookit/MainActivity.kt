package com.example.cookit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cookit.screens.createRecipe.CreateRecipeScreen
import com.example.cookit.screens.home.HomeScreen
import com.example.cookit.screens.auth.LogInScreen
import com.example.cookit.screens.recipe.RecipeScreen
import com.example.cookit.screens.shoppingList.ShoppingListScreen
import com.example.cookit.screens.auth.SignUpScreen
import com.example.cookit.ui.theme.CookItTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.cookit.screens.listRecipes.ListRecipesScreen
import com.example.cookit.screens.components.Recipe
import com.example.cookit.screens.createRecipe.CreateRecipeViewModel
import com.example.cookit.screens.editRecipe.EditRecipeScreen
import com.example.cookit.screens.editRecipe.EditRecipeViewModel
import com.example.cookit.screens.listRecipes.ListRecipesViewModel
import com.example.cookit.screens.searchRecipe.FilterIngredientsScreen
import com.example.cookit.screens.searchRecipe.SearchRecipeScreen
import com.example.cookit.screens.searchRecipe.SearchRecipeViewModel
import com.google.firebase.auth.FirebaseAuth
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
    val firebaseAuth = FirebaseAuth.getInstance()
    val gson = Gson()

    LaunchedEffect(Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            navController.navigate("home") {
                popUpTo("logIn") { inclusive = true }
            }
        } else {
            navController.navigate("logIn") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    val searchRecipeViewModel: SearchRecipeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "logIn",
    ) {
        composable("logIn") { LogInScreen(navController) }
        composable("signUp") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }

        composable("searchRecipes") {
            SearchRecipeScreen(navController, searchRecipeViewModel)
        }

        composable("filterIngredients") {
            FilterIngredientsScreen(navController, searchRecipeViewModel)
        }

        composable("createRecipe") {
            val createRecipeViewModel: CreateRecipeViewModel = viewModel()
            CreateRecipeScreen(navController, createRecipeViewModel)
        }

        composable(
            route = "showRecipe/{recipeJson}",
            arguments = listOf(navArgument("recipeJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeJson = backStackEntry.arguments?.getString("recipeJson")
            val recipe = Gson().fromJson(recipeJson, Recipe::class.java)
            RecipeScreen(navController, recipe)
        }

        composable("shoppingList") { ShoppingListScreen(navController) }

        composable(
            route = "listRecipes/{recipeIdList}?isFavorites={isFavorites}&type={type}",
            arguments = listOf(
                navArgument("recipeIdList") {type = NavType.StringType},
                navArgument("isFavorites") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("type") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->

            val listRecipesViewModel: ListRecipesViewModel = viewModel()
            
            val recipeIdListJson = backStackEntry.arguments?.getString("recipeIdList")
            val isFavorites = backStackEntry.arguments?.getBoolean("isFavorites") ?: false
            val type = backStackEntry.arguments?.getString("type") ?: ""

            val recipeIdList = recipeIdListJson?.let {
                gson.fromJson(it, Array<String>::class.java).toList()
            } ?: emptyList()

            ListRecipesScreen(
                viewModel = listRecipesViewModel,
                navController = navController,
                recipeIds = recipeIdList,
                isFavorites = isFavorites,
                type = type
            )
        }

        composable(
            route = "editRecipe/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            recipeId?.let {
                val editRecipeViewModel: EditRecipeViewModel = viewModel()
                EditRecipeScreen(navController, recipeId, editRecipeViewModel)
            }
        }
    }
}
