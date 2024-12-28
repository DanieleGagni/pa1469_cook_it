
package com.example.cookit.screens.searchRecipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cookit.ui.theme.CookItTheme

class FilterIngredientsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CookItTheme {
                FilterIngredientsScreen()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterIngredientsScreen(navHostController: NavHostController, viewModel: SearchRecipeViewModel = viewModel()) {

    val ingredients by viewModel.ingredients.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }
    var currentIngredient by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter Ingredients") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isDialogOpen = true },
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add Ingredient") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ingredients) { ingredient ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = ingredient)
                            IconButton(onClick = {
                                viewModel.removeIngredient(ingredient)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Ingredient"
                                )
                            }
                        }
                    }
                }
            }

            // Launch Search
            Button(
                onClick = {
                    if(ingredients.isNotEmpty()) {
                        viewModel.searchByIngredients(navHostController, ingredients)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Search")
            }
        }

        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text("Add Ingredient") },
                text = {
                    OutlinedTextField(
                        value = currentIngredient,
                        onValueChange = { currentIngredient = it },
                        label = { Text("Ingredient") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (currentIngredient.isNotBlank()) {
                                viewModel.addIngredient(currentIngredient)
                                currentIngredient = ""
                                isDialogOpen = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isDialogOpen = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

