package com.example.cookit.screens.editRecipe

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.cookit.screens.components.Recipe
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditRecipeViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")

    private val _editingRecipe = MutableStateFlow<Recipe?>(null)
    val editingRecipe: StateFlow<Recipe?> = _editingRecipe

    fun loadRecipe(recipeId: String) {
        recipesCollection.document(recipeId).get()
            .addOnSuccessListener { documentSnapshot ->
                val recipe = documentSnapshot.toObject(Recipe::class.java)
                _editingRecipe.value = recipe
                println("Recipe loaded: $recipe")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                println("Error loading recipe: ${e.message}")
            }
    }
}

@Composable
fun EditRecipeScreen(
    navController: NavHostController,
    recipeId: String,
    viewModel: EditRecipeViewModel = viewModel()
) {
    val recipe by viewModel.editingRecipe.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    recipe?.let {
        loadedRecipe ->
        var title by remember { mutableStateOf(loadedRecipe.title) }
        var estimatedTime by remember { mutableStateOf(loadedRecipe.estimatedTime.toString()) }
        var type by remember { mutableStateOf(loadedRecipe.type) }
        var serves by remember { mutableStateOf(loadedRecipe.serves.toString()) }
        val ingredients = remember { mutableStateListOf(*loadedRecipe.ingredients.toTypedArray()) }
        val steps = remember { mutableStateListOf(*loadedRecipe.steps.toTypedArray()) }
    }

    val title = recipe?.title ?: "No Title" // nullチェック
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Edit Recipe",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                modifier = Modifier.padding(8.dp)
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = title,
                onValueChange = {  },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}