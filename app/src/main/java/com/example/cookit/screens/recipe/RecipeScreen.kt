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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

        // Query the shoppingLists collection to find the document for the user
        db.collection("shoppingLists")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents.firstOrNull()
                if (document != null) {
                    val shoppingListRef = document.reference

                    shoppingListRef.get().addOnSuccessListener { documentSnapshot ->
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
                    }.addOnFailureListener { exception ->
                        onFailure(exception)
                    }
                } else {
                    // No document found for the user
                    onFailure(Exception("No shopping list found for userId: $userId"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
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
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(recipe.id) {
        viewModel.loadFavoriteStatus(recipe.id)
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    // Recipe title
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFF58D1E),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(recipe.title)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 30.sp),
                        color = Color.Black,
                    )

                    // Recipe content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.75f)
                            .padding(top = 16.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Green,
                                shape = RoundedCornerShape(8.dp)
                            ),
                    ) {
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

                    // Actions Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FavoriteButton(
                            isFavorite = isFavorite,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(recipe)
                            }
                        )

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
                    }
                }
            }
        }
    )
}
