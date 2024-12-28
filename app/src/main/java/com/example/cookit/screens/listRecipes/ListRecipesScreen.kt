package com.example.cookit.screens.listRecipes

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.screens.components.Recipe
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// ViewModel for the ListRecipes screen
class ListRecipesViewModel : ViewModel() {

    private val db = com.google.firebase.ktx.Firebase.firestore
    private val recipesCollection = db.collection("recipes")

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    fun fetchRecipesByIds(ids: List<String>) {

        if (!ids.isEmpty()) {

            ids.forEach { recipeId ->
                Log.d("[------------------------- DEBUG]", "Recipe ID in ViewModel: $recipeId")
            }

            val idChunks =
                ids.chunked(10)  // Firebase "whereIn" can receive in input a list of maximum 10 elements

            viewModelScope.launch {
                val allRecipes = mutableListOf<Recipe>()

                for (chunk in idChunks) {
                    recipesCollection
                        .whereIn("id", chunk)
                        .get()
                        .addOnSuccessListener { documents ->
                            val chunkRecipes = documents.map { document ->
                                val recipe = document.toObject(Recipe::class.java)
                                recipe.copy(id = document.id)
                            }

                            // Add the chunk's results to the complete list
                            allRecipes.addAll(chunkRecipes)

                            // Update the Flow with the complete list
                            _recipes.value = allRecipes
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ListRecipesViewModel", "Error fetching recipes", exception)
                        }
                }
            }
        }
    }

}

@Composable
fun ListRecipesScreen(
    navController: NavHostController,
    recipeIds: List<String>,
    viewModel: ListRecipesViewModel = viewModel()
) {

    val recipes by viewModel.recipes.collectAsState()

    LaunchedEffect(recipeIds) {
        viewModel.fetchRecipesByIds(recipeIds)
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // TODO
            // NavigationBar(navController) // Barra de navegación inferior
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (recipes.isEmpty()) {
                    Text(
                        text = "No recipes found. ",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recipes.size) { index ->
                            RecipeItem(
                                recipe = recipes[index],
                                onClick = {
                                    // Navigate to the recipe details screen
                                    val recipeJson = Gson().toJson(recipes[index])
                                    navController.navigate("showRecipe/$recipeJson")
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

// TODO recipe should display title, type and estimated time
// TODO onClick -> ShowRecipeScreen
@Composable
fun RecipeItem(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = recipe.type,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    maxLines = 2
                )
            }
        }
    }
}