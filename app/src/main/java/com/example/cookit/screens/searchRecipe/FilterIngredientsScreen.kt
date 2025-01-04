
package com.example.cookit.screens.searchRecipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.cookit.ui.theme.CookItTheme
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.ui.theme.darkOrange
import com.example.cookit.ui.theme.lightOrange

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

@Composable
fun FilterIngredientsScreen(navController: NavHostController, viewModel: SearchRecipeViewModel = viewModel()) {

    val ingredients by viewModel.ingredients.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }
    var currentIngredient by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = darkOrange,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Add")
                        }
                        append(" your ingredients\n")

                        withStyle(
                            style = SpanStyle(
                                color = darkOrange,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Find")
                        }
                        append(" your recipe!")
                    },
                    /*
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 25.sp,
                        lineHeight = 32.sp
                    ),

                     */
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 25.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        bottomBar = {
            NavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isDialogOpen = true },
                containerColor = lightOrange,
                contentColor = Color.Black,
                content = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add Ingredient") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            if(ingredients.isEmpty()) {
                Text(
                    text = "No ingredients added",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
            }

            Button(
                onClick = {
                    if (ingredients.isNotEmpty()) {
                        viewModel.searchByIngredients(navController, ingredients)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF58D1E))
            ) {
                Text("Search")
            }
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
            modifier = Modifier.padding(horizontal = 32.dp),
            properties = DialogProperties(usePlatformDefaultWidth = false),
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


