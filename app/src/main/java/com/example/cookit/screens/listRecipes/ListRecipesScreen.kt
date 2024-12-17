package com.example.cookit.screens.listRecipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.cookit.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class Recipe(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String
)

// ViewModel for the ListRecipes screen
class ListRecipesViewModel : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        // Simulate fetching recipes from a database or API
        viewModelScope.launch {
            _recipes.value = listOf(
                Recipe(
                    id = "1",
                    name = "Spaghetti Carbonara",
                    imageUrl = "https://via.placeholder.com/150",
                    description = "A creamy Italian pasta dish."
                ),
                Recipe(
                    id = "2",
                    name = "Chicken Alfredo",
                    imageUrl = "https://via.placeholder.com/150",
                    description = "Rich and creamy pasta with chicken."
                ),
                Recipe(
                    id = "3",
                    name = "Taco Salad",
                    imageUrl = "https://via.placeholder.com/150",
                    description = "A refreshing salad with taco flavors."
                )
            )
        }
    }
}

@Composable
fun ListRecipesScreen(
    navController: NavHostController,
    recipes: List<Recipe>
) {
    Scaffold { innerPadding -> // Use the padding values provided by Scaffold
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Correctly apply innerPadding
        ) {
            if (recipes.isEmpty()) {
                Text(
                    text = "No recipes found. Start creating some!",
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
                                navController.navigate("recipe/${recipes[index].id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

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
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder image logic (replace with Coil for dynamic images)
            Image(
                painter = painterResource(id = R.drawable.ic_category_vegetarian),
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    maxLines = 2
                )
            }
        }
    }
}