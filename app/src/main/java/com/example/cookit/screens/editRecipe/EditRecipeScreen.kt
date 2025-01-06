package com.example.cookit.screens.editRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.example.cookit.ui.theme.darkOrange
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
        val updatedRecipeWithKeywords = updatedRecipe
            .withTitleKeywords(extractTitleKeywords(updatedRecipe.title))
            .withIngredientsKeywords(extractIngredientsKeywords(updatedRecipe.ingredients))

        recipesCollection.document(recipeId).set(updatedRecipeWithKeywords)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    private fun extractTitleKeywords(title: String): List<String> {
        val wordPattern = "\\b([A-Za-z]+)\\b".toRegex()
        val STOP_WORDS = setOf(
            "a", "an", "and", "as", "at", "by", "for", "from", "in", "into", "of",
            "on", "or", "the", "to", "with", "your", "my", "our", "their", "this",
            "that", "these", "those", "over", "under", "around", "up", "down", "out",
            "inside", "outside", "through", "onto", "off"
        )
        return wordPattern.findAll(title)
            .map { it.groupValues[1].lowercase() }
            .filter { it !in STOP_WORDS }
            .toList()
    }

    private fun extractIngredientsKeywords(ingredients: List<String>): MutableList<String> {
        val regex = """(?:\d+\s*[^a-zA-Z]*|\b)([a-zA-Z\s]+?)(?=\b\d*[^a-zA-Z]*$|\b)""".toRegex()
        val STOP_WORDS = setOf(
            "cup", "teaspoon", "tablespoon", "small", "medium", "large", "chopped", "diced", "minced", "fresh",
            "optional", "to", "taste", "and", "or", "any", "half", "cooked", "ounces", "pounds", "g", "mg", "ml",
            "liter", "kilogram", "gram", "liter", "tsp", "tbsp", "flour", "salt", "pepper", "oil", "water", "sugar",
            "butter", "cheese", "egg"
        )
        return ingredients.flatMap { ingredient ->
            regex.findAll(ingredient).mapNotNull { matchResult ->
                val keyword = matchResult.groupValues[1].trim().lowercase()
                if (keyword.isNotBlank() && keyword !in STOP_WORDS) keyword else null
            }
        }.toMutableList()
    }

    fun Recipe.withTitleKeywords(titleKeywords: List<String>): Recipe {
        return this.copy(title_keywords = titleKeywords)
    }

    fun Recipe.withIngredientsKeywords(ingredientsKeywords: List<String>): Recipe {
        return this.copy(ingredients_keywords = ingredientsKeywords)
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
        val typeOptions = listOf("vegetarian", "quick", "complex", "other")

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
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = buildAnnotatedString {
                            append("Edit")
                            withStyle(
                                style = SpanStyle(
                                    color = darkOrange,
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "TITLE",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(text = "Enter Menu Name") },
                        maxLines = 1,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFFE4C4), // Fondo naranja pastel al enfocar
                            unfocusedContainerColor = Color(0xFFFFE4C4), // Fondo naranja pastel sin enfocar
                            disabledContainerColor = Color(0xFFE0E0E0), // Fondo gris si está deshabilitado
                            errorContainerColor = Color(0xFFFFCDD2), // Fondo rojo en caso de error
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )  {
                    Text(
                        text = "TYPE",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .clickable { expanded = true }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (type.isBlank()) "Select Type" else type,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        typeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    type = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "TIME",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = estimatedTime,
                        onValueChange = {
                                newValue ->
                            if(newValue.all { it.isDigit() }) {
                                estimatedTime = newValue
                            } },
                        label = { Text(text = "Enter Estimated Time (minutes)") },
                        maxLines = 1,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFFE4C4), // Fondo naranja pastel al enfocar
                            unfocusedContainerColor = Color(0xFFFFE4C4), // Fondo naranja pastel sin enfocar
                            disabledContainerColor = Color(0xFFE0E0E0), // Fondo gris si está deshabilitado
                            errorContainerColor = Color(0xFFFFCDD2), // Fondo rojo en caso de error
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "SERVES",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = serves,
                        onValueChange = {
                                newValue ->
                            if(newValue.all { it.isDigit() }) {
                                serves = newValue
                            } },
                        label = { Text(text = "Enter Servings") },
                        maxLines = 1,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFFE4C4), // Fondo naranja pastel al enfocar
                            unfocusedContainerColor = Color(0xFFFFE4C4), // Fondo naranja pastel sin enfocar
                            disabledContainerColor = Color(0xFFE0E0E0), // Fondo gris si está deshabilitado
                            errorContainerColor = Color(0xFFFFCDD2), // Fondo rojo en caso de error
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
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
                )  {
                    Text(
                        text = "INGREDIENTS",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    AddEditIngredients(ingredients)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
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
                            onSuccess = { navController.navigate("home") },
                            onFailure = { e -> println("Error updating recipe: ${e.message}") }
                        )
                    },
                    enabled = isEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEnabled) {
                            darkOrange
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