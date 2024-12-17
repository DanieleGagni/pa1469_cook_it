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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.cookit.screens.createRecipe.components.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class RecipeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _isFavorited = MutableStateFlow(false)
    val isFavorited: StateFlow<Boolean> = _isFavorited

    //TODO fix this
    fun toggleFavorite(recipe: Recipe) {
        val newFavoriteState = !_isFavorited.value
        _isFavorited.value = newFavoriteState

        // Update favorite state in Firestore or local database
        db.collection("favorites").document(recipe.title)
            .set(mapOf("isFavorited" to newFavoriteState))
    }

    fun addIngredientsToShoppingList(ingredients: List<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val shoppingListRef = db.collection("shoppingLists").document(userId)

        //TODO fix this
        shoppingListRef.update("items", FieldValue.arrayUnion(*ingredients.toTypedArray()))
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}


@Composable
fun FavoriteButton(
    isFavorited: Boolean,
    onFavoriteClick: (Boolean) -> Unit
) {
    var isFavoritedState by remember { mutableStateOf(isFavorited) }

    IconButton(
        onClick = {
            isFavoritedState = !isFavoritedState
            onFavoriteClick(isFavoritedState)
        },
        modifier = Modifier.size(60.dp)
    ) {
        Icon(
            painter = if (isFavoritedState) painterResource(id = R.drawable.ic_favorite) else painterResource(id = R.drawable.ic_favourite_outlined),
            contentDescription = if (isFavoritedState) "Remove from favorites" else "Add to favorites",
            tint = Color.Unspecified,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
fun RecipeScreen(
    navController: NavHostController,
    recipe: Recipe,
    viewModel: RecipeViewModel = viewModel()
) {
    val isFavorited by viewModel.isFavorited.collectAsState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(navController)
        },
        content = { innerPadding ->

            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        //.padding(top = 30.dp)
                    //horizontalAlignment = Alignment.CenterHorizontally
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
                            .verticalScroll(scrollState)
                            .border(
                                width = 2.dp,                // Border thickness
                                color = Color.Green,         // Border color
                                shape = RoundedCornerShape(8.dp) // Optional: Rounded corners
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

                        //SPACER TO CHECK IF SCROLLABLE WORKS
                        //SPOILER ALERT IT DOESN'T WORK
                        //Spacer(modifier = Modifier.height(250.dp))

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
                            isFavorited = isFavorited,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(recipe)
                            }
                        )

                        IconButton(
                            onClick = { viewModel.addIngredientsToShoppingList(recipe.ingredients) },
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
