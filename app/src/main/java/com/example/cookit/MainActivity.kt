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
//import com.example.cookit.screens.listRecipes.Recipe as ListRecipe
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


//val recipes = listOf(
//    ListRecipe(
//        id = "1",
//        name = "Spaghetti Carbonara",
//        //imageUrl = "https://via.placeholder.com/150",
//        description = "A creamy Italian pasta dish."
//    ),
//    ListRecipe(
//        id = "2",
//        name = "Chicken Alfredo",
//        //imageUrl = "https://via.placeholder.com/150",
//        description = "Rich and creamy pasta with chicken."
//    ),
//    ListRecipe(
//        id = "3",
//        name = "Taco Salad",
//        //imageUrl = "https://via.placeholder.com/150",
//        description = "A refreshing salad with taco flavors."
//    )
//)

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "logIn",
        //startDestination = "listRecipes"
    ) {
        composable("logIn") { LogInScreen(navController) }
        composable("signUp") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("createRecipe") { CreateRecipeScreen(navController) }
//        composable(
//            route = "showRecipe/{recipe}",
//            arguments = listOf(navArgument("recipe") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val recipeJson = backStackEntry.arguments?.getString("recipe")
//            val recipe = Gson().fromJson(recipeJson, Recipe::class.java)
//            RecipeScreen(navController, recipe)
//        }
        composable("shoppingList") { ShoppingListScreen(navController) }
        composable("listRecipes") { ListRecipesScreen(navController) }
        composable(
            route = "recipe/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeScreen(navController, recipeId)
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
