package com.example.cookit.screens.listRecipes

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.screens.components.Recipe
import com.example.cookit.ui.theme.darkOrange
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ListRecipesScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CookItTheme() {
                ListRecipesScreen()
            }
        }
    }
}

@Composable
fun ListRecipesScreen(
    navController: NavHostController,
    recipeIds: List<String>,
    isFavorites: Boolean,
    type: String,
    viewModel: ListRecipesViewModel
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
                                    color = darkOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Your ")
                            }
                            append("favorites")
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 25.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = darkOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Your ")
                            }
                            append("results")
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 25.sp),
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
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.width(60.dp))

                    Text(
                        text = "Estimated time: ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    Text(
                        text = String.format(recipe.estimatedTime.toString()),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }

            }
            Column {
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

