package com.example.cookit.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String = "",
    val title: String = "",
    val title_keywords: List<String> = emptyList(),
    val ingredients: List<String> = emptyList(),
    val ingredients_keywords: List<String> = emptyList(),
    val estimatedTime: Int = 0,
    val serves: Int = 0,
    val steps: List<String> = emptyList(),
    val type: String = "",
) : Parcelable {
    companion object {
        fun create(
            title: String = "",
            ingredients: List<String> = emptyList(),
            estimatedTime: Int = 0,
            serves: Int = 0,
            steps: List<String> = emptyList(),
            type: String = ""
        ): Recipe {
            return Recipe(
                id = "",
                title = title,
                title_keywords = emptyList(),
                ingredients = ingredients,
                ingredients_keywords = emptyList(),
                estimatedTime = estimatedTime,
                serves = serves,
                steps = steps,
                type = type
            )
        }
    }
}


@Composable
fun RecipeView(recipe: Recipe) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Type: ${recipe.type}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Estimated Time: ${recipe.estimatedTime} minutes",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Serves: ${recipe.serves}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Ingredients:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn {
            items(recipe.ingredients) { ingredient ->
                Text(text = "- $ingredient", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Steps:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn {
            itemsIndexed(recipe.steps) { index, step ->
                Text(text = "${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecipeView() {
    RecipeView(
        recipe = Recipe(
            title = "Spaghetti Bolognese",
            estimatedTime = 30,
            ingredients = listOf("Spaghetti", "Ground beef", "Tomato sauce", "Onion", "Garlic"),
            serves = 4,
            steps = listOf("Boil pasta", "Cook beef", "Add sauce", "Mix together"),
            type = "Quick"
        )
    )
}

@Composable
fun RecipeListView(recipes: List<Recipe>) {
    LazyColumn {
        items(recipes) { recipe ->
            RecipeView(recipe = recipe)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecipeListView() {
    RecipeListView(
        recipes = listOf(
            Recipe(
                title = "Spaghetti Bolognese",
                estimatedTime = 30,
                ingredients = listOf("Spaghetti", "Ground beef", "Tomato sauce", "Onion", "Garlic"),
                serves = 4,
                steps = listOf("Boil pasta", "Cook beef", "Add sauce", "Mix together"),
                type = "Quick"
            ),
            Recipe(
                title = "Chicken Curry",
                estimatedTime = 45,
                ingredients = listOf("Chicken", "Curry powder", "Coconut milk", "Onion", "Garlic"),
                serves = 4,
                steps = listOf("Cook chicken", "Add spices", "Add coconut milk", "Simmer"),
                type = "Quick"
            )
        )
    )
}