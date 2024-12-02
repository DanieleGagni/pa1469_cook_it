package com.example.cookit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Stable
data class Recipe(
    val nameDish: String,
    val ingredients: List<String>,
    val steps: List<String>
)

@Composable
fun RecipeView(recipe: Recipe) {
    Column {
        Box (
            Modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ){
            Text(
                text = recipe.nameDish
            )
        }

        Box(
            modifier = Modifier
                .background(color = Color.LightGray, shape = MaterialTheme.shapes.extraLarge)
                .padding(8.dp)
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Ingredients"
                )
                LazyColumn {
                    items(recipe.ingredients) { ingredient ->
                        Text(text = "・$ingredient", Modifier.padding(4.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .background(color = Color.LightGray, shape = MaterialTheme.shapes.extraLarge)
                .padding(8.dp)
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                Text(text = "Steps")
                LazyColumn {
                    itemsIndexed(recipe.steps) { index, step ->
                        Text(
                            text = "${index+1}. $step",
                            Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RecipeViewPreview() {
    RecipeView(
        Recipe(
            nameDish = "Meat Ball",
            ingredients = listOf("Meat", "Onion", "Potatoes"),
            steps = listOf("Cut onions.", "Heat meat.")
        )
    )
}