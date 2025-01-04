package com.example.cookit.screens.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.screens.components.Recipe
import com.example.cookit.ui.theme.darkOrange
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class RecipeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun loadFavoriteStatus(recipeId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val favoritesRef = db.collection("favorites").document(userId)

        favoritesRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentFavorites = document.get("recipes") as? List<String> ?: listOf()
                    _isFavorite.value = recipeId in currentFavorites
                } else {
                    _isFavorite.value = false
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                _isFavorite.value = false
            }
    }

    fun toggleFavorite(recipe: Recipe) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val favoritesRef = db.collection("favorites").document(userId)

        // Check if the favorites document exists
        favoritesRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // If the document exists, retrieve the current list of favorite recipes
                    val currentFavorites = document.get("recipes") as? List<String> ?: listOf()

                    if (recipe.id in currentFavorites) {
                        // If the recipe is already a favorite, remove it
                        favoritesRef.update("recipes", FieldValue.arrayRemove(recipe.id))
                            .addOnSuccessListener { _isFavorite.value = false }
                            .addOnFailureListener { it.printStackTrace() }
                    } else {
                        // If the recipe is not a favorite, add it
                        favoritesRef.update("recipes", FieldValue.arrayUnion(recipe.id))
                            .addOnSuccessListener { _isFavorite.value = true }
                            .addOnFailureListener { it.printStackTrace() }
                    }
                } else {
                    // If no document exists, create a new one with the current recipe as the first favorite
                    favoritesRef.set(mapOf("recipes" to listOf(recipe.id)))
                        .addOnSuccessListener { _isFavorite.value = true }
                        .addOnFailureListener { it.printStackTrace() }
                }
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun addIngredientsToShoppingList(
        ingredients: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val shoppingListRef = db.collection("shoppingLists").document(userId)

        shoppingListRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentItems = documentSnapshot.get("items") as? List<Map<String, Any>> ?: emptyList()

                    // Add the new ingredients to the list
                    val updatedItems = currentItems.toMutableList()
                    ingredients.forEach { ingredient ->
                        val newItem = mapOf("done" to false, "entry" to ingredient)
                        updatedItems.add(newItem)
                    }

                    // Write the updated list back to Firestore
                    shoppingListRef.update("items", updatedItems)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                } else {
                    // No document exists for the user, create one with the ingredients
                    val initialItems = ingredients.map { ingredient ->
                        mapOf("done" to false, "entry" to ingredient)
                    }
                    shoppingListRef.set(mapOf("items" to initialItems))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteRecipe(recipeId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val recipeRef = db.collection("recipes").document(recipeId)
        recipeRef.delete()
            .addOnSuccessListener {
                println("Recipe deleted successfully")
            }
            .addOnFailureListener { exception ->
                println("Error deleting recipe: ${exception.message}")
                exception.printStackTrace()
            }
    }
}


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
            Text(
                text = "Are you sure you want to delete this recipe?",
                color = Color.Black
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onConfirm,
                modifier = Modifier
                    .background(Color(0xFFFFE4C4), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Delete", color = Color.Black)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .background(Color(0xFFFFE4C4), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Cancel", color = Color.Black)
            }
        },
        containerColor = Color.White
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

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

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

                val scrollState = rememberScrollState()

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
                                modifier = Modifier.wrapContentSize(Alignment.TopEnd) // 右上に固定
                            ) {
                                IconButton(
                                    onClick = {
                                        expanded = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Edit Recipe",
                                        tint = Color(0xFFF58D1E),
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .background(Color(0xFFFFF5DD))
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Edit", color = Color.Black) },
                                        onClick = {
                                            expanded = false
                                            navController.navigate("editRecipe/${recipe.id}") // 編集画面に遷移
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
                navController.navigate("home")
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}
