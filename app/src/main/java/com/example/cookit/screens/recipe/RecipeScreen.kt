package com.example.cookit.screens.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.screens.components.Recipe
import com.example.cookit.ui.theme.darkOrange
import com.example.cookit.ui.theme.lightGrey
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    IconButton(
        onClick = onFavoriteClick,
        modifier = Modifier.size(60.dp)
    ) {
        Icon(
            painter = if (isFavorite) painterResource(id = R.drawable.ic_favorite) else painterResource(id = R.drawable.ic_favourite_outlined),
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = Color.Unspecified,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete Recipe")
        },
        text = {
            Text("Are you sure you want to delete this recipe?")
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = lightGrey
    )
}


// when navigating to the RecipeScreen, the call must look like this:

// val recipeJson = Uri.encode(Gson().toJson(testRecipe))
// navController.navigate("showRecipe/$recipeJson")

// ... where testRecipe is your recipe object

@Composable
fun RecipeScreen(
    navController: NavHostController,
    recipe: Recipe,
    viewModel: RecipeViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(recipe.id) {
        viewModel.loadFavoriteStatus(recipe.id)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(navController)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .weight(1f) // Use weight to allow the scrollable content to take available space
                            .verticalScroll(rememberScrollState())
                    ) {

                        // Category Icon Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Recipe title
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = darkOrange,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    ) {
                                        append(recipe.title)
                                    }
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 30.sp,
                                    lineHeight = 36.sp,
                                ),
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            when (recipe.type) {
                                "vegetarian" -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.vegetarian),
                                        contentDescription = "Vegetarian",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                "quick" -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.quick),
                                        contentDescription = "Vegetarian",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                "complex"-> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.complex),
                                        contentDescription = "Vegetarian",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(35.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                else -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.other),
                                        contentDescription = "Vegetarian",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(35.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        // Ingredients section
                        Text(
                            text = "INGREDIENTS",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        recipe.ingredients.forEach { ingredient ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 10.dp)
                            ) {
                                Text(text = ingredient)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Steps Section
                        Text(
                            text = "STEPS",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        recipe.steps.forEachIndexed { index, step ->
                            Text(
                                text = "${index + 1}. $step",
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Actions Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FavoriteButton(
                            isFavorite = isFavorite,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(recipe)
                            }
                        )

                        // Button to automatically transfer ingredients to shopping list
                        IconButton(
                            onClick = {
                                viewModel.addIngredientsToShoppingList(
                                    recipe.ingredients,
                                    onSuccess = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Ingredients added to shopping list!")
                                        }
                                    },
                                    onFailure = { exception ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Failed to add ingredients: ${exception.message}")
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_shopping_cart),
                                contentDescription = "Add to Shopping List",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        // Button to edit the recipe, appears only if current user is creator
                        if (currentUserId == recipe.createdBy) {
                            var expanded by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier.wrapContentSize(Alignment.TopEnd)
                            ) {
                                IconButton(
                                    onClick = {
                                        expanded = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Edit Recipe",
                                        tint = darkOrange,
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(lightGrey)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Edit", color = Color.Black) },
                                        onClick = {
                                            expanded = false
                                            navController.navigate("editRecipe/${recipe.id}")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete", color = Color.Black) },
                                        onClick = {
                                            expanded = false
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteRecipe(recipe.id)
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}
