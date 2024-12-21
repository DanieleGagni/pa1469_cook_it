package com.example.cookit.screens.editRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.cookit.screens.components.Recipe
import com.example.cookit.screens.createRecipe.AddEditIngredients
import com.example.cookit.screens.createRecipe.AddEditStepsScreen
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

    fun updateRecipe(
        recipeId: String,
        updatedRecipe: Recipe,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        recipesCollection.document(recipeId).set(updatedRecipe)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
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

//    recipe?.let {
//        loadedRecipe ->
//        var title by remember { mutableStateOf(loadedRecipe.title) }
//        var estimatedTime by remember { mutableStateOf(loadedRecipe.estimatedTime.toString()) }
//        var type by remember { mutableStateOf(loadedRecipe.type) }
//        var serves by remember { mutableStateOf(loadedRecipe.serves.toString()) }
//        val ingredients = remember { mutableStateListOf(*loadedRecipe.ingredients.toTypedArray()) }
//        val steps = remember { mutableStateListOf(*loadedRecipe.steps.toTypedArray()) }
//    }

    if (recipe == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }
    } else {
        var title by remember { mutableStateOf(recipe!!.title) }
        var estimatedTime by remember { mutableStateOf(recipe!!.estimatedTime.toString()) }
        var type by remember { mutableStateOf(recipe!!.type) }
        var serves by remember { mutableStateOf(recipe!!.serves.toString()) }
        val ingredients = remember { mutableStateListOf(*recipe!!.ingredients.toTypedArray()) }
        val steps = remember { mutableStateListOf(*recipe!!.steps.toTypedArray()) }

        val keyboardController = LocalSoftwareKeyboardController.current

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            item{
                Box {
                    Text(
                        text = buildAnnotatedString {
                            append("Edit")
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFF58D1E),
                                    fontWeight = FontWeight.Bold)
                            ) {
                                append(" Recipe")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box {
                    Text(
                        text = "TITLE",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(text = "Enter Menu Name") },
                        maxLines = 1,
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box {
                    Text(
                        text = "TYPE",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        value = type,
                        onValueChange = { type = it },
                        label = { Text(text = "Enter Type") },
                        maxLines = 1,
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box {
                    Text(
                        text = "TIME",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        value = estimatedTime,
                        onValueChange = {
                                newValue ->
                            if(newValue.all { it.isDigit() }) {
                                estimatedTime = newValue
                            } },
                        label = { Text(text = "Enter Estimated Time (minutes)") },
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box {
                    Text(
                        text = "SERVES",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        value = serves,
                        onValueChange = {
                                newValue ->
                            if(newValue.all { it.isDigit() }) {
                                serves = newValue
                            } },
                        label = { Text(text = "Enter Servings") },
                        maxLines = 1,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            Color.Black,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "INGREDIENTS",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    AddEditIngredients(ingredients)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            Color.Black,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                ) {
                    Text(
                        text = "STEPS",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    AddEditStepsScreen(steps)
                }
            }

            item {
                val isEnabled = title.isNotBlank() &&
                        estimatedTime.isNotBlank() &&
                        estimatedTime.all { it.isDigit() } &&
                        serves.isNotBlank() &&
                        serves.all { it.isDigit() } &&
                        type.isNotBlank() &&
                        ingredients.isNotEmpty() &&
                        steps.isNotEmpty()

                Button(
                    onClick = {
                        val updatedRecipe = recipe!!.copy(
                            title = title,
                            estimatedTime = estimatedTime.toIntOrNull() ?: 0,
                            type = type,
                            serves = serves.toIntOrNull() ?: 0,
                            ingredients = ingredients.toList(),
                            steps = steps.toList()
                        )
                        viewModel.updateRecipe(
                            recipeId = recipeId,
                            updatedRecipe = updatedRecipe,
                            onSuccess = { navController.navigate("listRecipes") },
                            onFailure = { e -> println("Error updating recipe: ${e.message}") }
                        )
                    },
                    enabled = isEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEnabled) {
                            Color(0xFFF58D1E)
                        } else {
                            Color.Gray
                        },
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text ="Upadate Recipe")
                }
            }
        }
    }
}