package com.example.cookit.screens.listRecipes

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.screens.components.Recipe
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// ViewModel for the ListRecipes screen
class ListRecipesViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")
    private val favoritesCollection = db.collection("favorites")

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _favoriteStatuses = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favoriteStatuses: StateFlow<Map<String, Boolean>> = _favoriteStatuses

    fun fetchRecipesByType(type: String) {

        val t = type.lowercase()

        if(t == "all") {
            viewModelScope.launch {
                recipesCollection
                    .get()
                    .addOnSuccessListener { documents ->
                        val recipeList = documents.map { document ->
                            val recipe = document.toObject(Recipe::class.java)
                            recipe.copy(id = document.id)  // assign recipe.id
                        }

                        _recipes.value = recipeList
                        _recipes.value.forEach { recipe ->
                            Log.d(
                                "[------------------------- DEBUG]",
                                "------------------------- ${recipe.title}"
                            )
                        }
                        fetchFavoriteStatuses()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ListRecipesViewModel", "Error fetching recipes", exception)
                    }
            }
        } else {

            viewModelScope.launch {
                recipesCollection
                    .whereEqualTo("type", t)
                    .get()
                    .addOnSuccessListener { documents ->
                        val recipeList = documents.map { document ->
                            val recipe = document.toObject(Recipe::class.java)
                            recipe.copy(id = document.id)  // assign recipe.id
                        }

                        _recipes.value = recipeList
                        _recipes.value.forEach { recipe ->
                            Log.d(
                                "[------------------------- DEBUG]",
                                "------------------------- ${recipe.title}"
                            )
                        }
                        fetchFavoriteStatuses()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ListRecipesViewModel", "Error fetching recipes", exception)
                    }
            }
        }
    }

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
                            fetchFavoriteStatuses()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ListRecipesViewModel", "Error fetching recipes", exception)
                        }
                }
            }
        }
    }

    fun fetchFavoriteStatuses() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        favoritesCollection.document(userId).get()
            .addOnSuccessListener { document ->
                val favoriteIds = document.get("recipes") as? List<String> ?: emptyList()
                val favoriteMap = favoriteIds.associateWith { true }
                _favoriteStatuses.value = favoriteMap
            }
            .addOnFailureListener { it.printStackTrace() }
    }

}

@Composable
fun ListRecipesScreen(
    navController: NavHostController,
    recipeIds: List<String>,
    isFavorites: Boolean,
    type: String,
    viewModel: ListRecipesViewModel = viewModel()
) {

    val recipes by viewModel.recipes.collectAsState()
    val favoriteStatuses by viewModel.favoriteStatuses.collectAsState()

    LaunchedEffect(recipeIds) {
        if(type.isNotEmpty()) {
            viewModel.fetchRecipesByType(type)
        } else {
            viewModel.fetchRecipesByIds(recipeIds)
        }
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(top = 8.dp)
            ) {
                if(isFavorites) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFF58D1E),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Your ")
                            }
                            append("favorites")
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFF58D1E),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Your ")
                            }
                            append("results")
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(navController)
        },

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (recipes.isEmpty()) {

                if(isFavorites) {
                    Text(
                        text = "Save recipes you love and find them here",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                } else {
                    Text(
                        text = "No recipes found.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes.size) { index ->
                        RecipeItem(
                            recipe = recipes[index],
                            isFavorite = favoriteStatuses[recipes[index].id] == true,
                            onClick = {
                                val recipeJson = Gson().toJson(recipes[index])
                                Log.d("-------------- RecipeItem", recipeJson)
                                navController.navigate("showRecipe/$recipeJson")
                            }
                        )
                    }
                }
            }
        }
    }
}


// TODO recipe should display title, type and estimated time
@Composable
fun RecipeItem(
    recipe: Recipe,
    isFavorite: Boolean,
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

                Row (
                    modifier = Modifier
                        .padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (recipe.type) {
                        "vegetarian" -> {
                            Icon(
                                painter = painterResource(id = R.drawable.vegetarian),
                                contentDescription = "Vegetarian",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        "quick" -> {
                            Icon(
                                painter = painterResource(id = R.drawable.quick),
                                contentDescription = "Vegetarian",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        "complex"-> {
                            Icon(
                                painter = painterResource(id = R.drawable.complex),
                                contentDescription = "Vegetarian",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        else -> {
                            Icon(
                                painter = painterResource(id = R.drawable.other),
                                contentDescription = "Vegetarian",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Text(
                        text = recipe.type,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                        maxLines = 2
                    )
                }

            }
            Column {
                //HERE SHOULD GO THE OPTION TO ADD TO FAVOURITES
                Icon(
                    painter =
                    if (isFavorite)
                        painterResource(id = R.drawable.ic_favorite)
                    else
                        painterResource(id = R.drawable.ic_favourite_outlined),
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}