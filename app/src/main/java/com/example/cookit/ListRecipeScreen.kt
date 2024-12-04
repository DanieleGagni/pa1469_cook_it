package com.example.cookit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookit.ui.theme.CookItTheme

class ListRecipeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CookItTheme {
                SearchRecipeScreen()
            }
        }
    }
}

@Composable
fun SearchRecipeScreen(viewModel: ListRecipeViewModel = viewModel()) {
    val recipes by viewModel.recipes.observeAsState(emptyList())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { viewModel.searchRecipes() }) {
                Text(text = "Search Recipes", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            recipes.forEach { recipe: Recipe ->
                Text(text = recipe.title, fontSize = 16.sp)
            }
        }
    }
}