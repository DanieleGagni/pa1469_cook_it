package com.example.cookit.screens.editRecipe

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
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
import androidx.navigation.NavHostController
import com.example.cookit.screens.components.Recipe
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.SetOptions

class EditRecipeViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val recipesCollection = db.collection("recipes")

    private val _editingRecipe = MutableStateFlow<Recipe?>(null)
    val editingRecipe: StateFlow<Recipe?> = _editingRecipe

    fun loadRecipe(recipeId: String) {
        recipesCollection
            .document(recipeId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val recipe = documentSnapshot.toObject(Recipe::class.java)
                _editingRecipe.value = recipe

                Log.d(" ---------------------------- EditRecipeViewModel - loadRecipe -------- ",
                    "ID ${_editingRecipe.value!!.id}, TITLE ${_editingRecipe.value!!.title}")
            }
            .addOnFailureListener { e ->
                Log.e("---------------------------- EditRecipeScreen, loadRecipe ------------------- ", "FIREBASE ERROR", e)
                // TODO error handling (Snackbar)}
            }
    }

    fun updateRecipe(
        recipeId: String,
        updatedRecipe: Recipe,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        Log.d("---------------------------- EditRecipeScreen, updateRecipe ------------------- ",
            "${updatedRecipe.title} - ${updatedRecipe.id} - $recipeId  well well well")

        recipesCollection.document(recipeId).set(updatedRecipe, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("---------------------------- EditRecipeScreen, updateRecipe ------------------- ", "FIREBASE ERROR", e)
                // TODO error handling (Snackbar)}
            }
    }

    // extract keywords from recipe title
    fun extractTitleKeywords(title: String): List<String> {

        // \b[\w]+\b --> match alphanumeric words
        val regex = "\\b[\\w]+\\b".toRegex()

        val STOP_WORDS = setOf(
            "a", "an", "and", "at", "by", "for", "from", "in", "into", "of",
            "on", "or", "the", "to", "with", "your", "my", "our", "their", "this",
            "that", "these", "those", "over", "under", "around", "inside", "outside",
            "through", "onto", "off", "how", "make", "recipe", "easy", "quick",
            "best", "delicious", "simple", "perfect", "classic", "homemade",
            "ultimate", "basic", "favorite", "authentic"
        )

        val extractedKeywords = regex.findAll(title)
            .map { it.value }
            .map { it.lowercase() }
            .filter { it.any { char -> char.isLetter() } } // exclude numbers
            .filter { it !in STOP_WORDS }
            .toList()

        extractedKeywords.forEach { keyword ->
            Log.d("extractTitleKeywords ------------ ", keyword)
        }

        return extractedKeywords
    }

    // extract keywords from ingredients
    fun extractIngredientsKeywords(ingredients: List<String>): MutableList<String> {

        val regex = "\\b[\\w]+\\b".toRegex()

        val STOP_WORDS = setOf(
            "cup",
            "teaspoon",
            "tablespoon",
            "small",
            "medium",
            "large",
            "chopped",
            "diced",
            "minced",
            "fresh",
            "optional",
            "to",
            "taste",
            "and",
            "or",
            "any",
            "half",
            "cooked",
            "ounces",
            "pounds",
            "g",
            "mg",
            "ml",
            "liter",
            "kilogram",
            "gram",
            "liter",
            "tsp",
            "tbsp",
        )

        val allIngredientsKeywords = mutableListOf<String>()

        ingredients.forEach { ingredient ->

            val keywords = regex.findAll(ingredient)
                .map { it.value } //extracts ONLY the captured word group
                .map { it.lowercase() }
                .filter { it.any { char -> char.isLetter() } } // exclude numbers
                .filter { it !in STOP_WORDS }
                .toList()

            allIngredientsKeywords.addAll(keywords)

        }

        allIngredientsKeywords.forEach{ keyword ->
            Log.d("extractIngredientsKeywords ------------ " , keyword)
        }

        return allIngredientsKeywords
    }

}

@Composable
fun EditRecipeScreen(
    navController: NavHostController,
    recipeId: String,
    viewModel: EditRecipeViewModel = viewModel()
) {
    val recipe by viewModel.editingRecipe.collectAsState()

    var hasBeenEdited by remember { mutableStateOf(false) } // TODO
    var showWarningDialog by remember { mutableStateOf(false) }



    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    // TODO
    BackHandler(enabled = hasBeenEdited) {
        showWarningDialog = true
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
        var ingredients = remember { mutableStateListOf(*recipe!!.ingredients.toTypedArray()) }
        var steps = remember { mutableStateListOf(*recipe!!.steps.toTypedArray()) }
        var createdBy by remember { mutableStateOf(recipe!!.createdBy) }

        var currentStep by remember { mutableStateOf("") }
        var editStepIndex by remember { mutableStateOf(-1) }

        var currentIngredient by remember { mutableStateOf("") }
        var editIngredientIndex by remember { mutableStateOf(-1) }

        val keyboardController = LocalSoftwareKeyboardController.current
        val typeOptions = listOf("vegetarian", "quick", "complex", "other")

        // Show the dialog when showDialog is true
        if (showWarningDialog) {
            AlertDialog(
                onDismissRequest = { showWarningDialog = false },
                title = { Text("Update Recipe") },
                text = { Text("Do you want to save your changes?") },
                confirmButton = {
                    Button(
                        onClick = {

                            val ingrediets_keywords = viewModel.extractIngredientsKeywords(ingredients)
                            val title_keywords = viewModel.extractTitleKeywords(title)

                            val updatedRecipe = recipe!!.copy(
                                id = recipeId,
                                title = title,
                                title_keywords = title_keywords,
                                estimatedTime = estimatedTime.toIntOrNull() ?: 0,
                                type = type,
                                serves = serves.toIntOrNull() ?: 0,
                                ingredients = ingredients.toList(),
                                ingredients_keywords = ingrediets_keywords,
                                steps = steps.toList(),
                                createdBy = createdBy
                            )


                            viewModel.updateRecipe(
                                recipeId = recipeId,
                                updatedRecipe = updatedRecipe,
                                onSuccess = { navController.navigate("home") },
                                onFailure = { e -> println("Error updating recipe: ${e.message}") }
                            )
                            showWarningDialog = false

                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showWarningDialog = false
                        }
                    ) {
                        Text("Go Back")
                    }
                }
            )
        }

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
                        onValueChange = {
                            title = it
                            hasBeenEdited = true
                        },
                        label = { Text(text = "Enter Recipe Name") },
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
                                    hasBeenEdited = true
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
                                hasBeenEdited = true
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
                                hasBeenEdited = true
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

                    //AddEditIngredients(ingredients)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            value = currentIngredient,
                            onValueChange = { newText ->
                                currentIngredient = newText
                                hasBeenEdited = true
                            },
                            label = {
                                Text(
                                    text = if (editIngredientIndex >= 0) "Edit Ingredient" else "Enter Ingredient"
                                )
                            },
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
                                    if (currentIngredient.isNotBlank()) {
                                        if (editIngredientIndex >= 0) {
                                            ingredients[editIngredientIndex] = currentIngredient
                                            editIngredientIndex = -1
                                        } else {
                                            ingredients.add(currentIngredient)
                                        }
                                        currentIngredient = ""
                                        keyboardController?.hide()
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                        )
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = "Ingredients:")
                            ingredients.forEachIndexed { index, ingredient ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "・$ingredient",
                                        Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = {
                                            currentIngredient = ingredient
                                            editIngredientIndex = index
                                            hasBeenEdited = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.LightGray,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp)
                                            .size(48.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = android.R.drawable.ic_menu_edit),
                                            contentDescription = "Edit Ingredient",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            ingredients.removeAt(index)
                                            hasBeenEdited = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.LightGray,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(48.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = android.R.drawable.ic_menu_delete),
                                            contentDescription = "Delete Ingredient",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
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

                    // AddEditStepsScreen(steps)
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                value = currentStep,
                                onValueChange = { newText ->
                                    currentStep = newText
                                    hasBeenEdited = true
                                },
                                label = {
                                    Text(
                                        text = when {
                                            editStepIndex >= 0 -> "Edit Step"
                                            //insertIndex >= 0 -> "Insert Step"
                                            else -> "Enter Step"
                                        }
                                    )
                                },
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
                                        if (currentStep.isNotBlank()) {
                                            when {
                                                editStepIndex >= 0 -> {
                                                    steps[editStepIndex] = currentStep
                                                    editStepIndex = -1
                                                }
                                                else -> {
                                                    steps.add(currentStep)
                                                }
                                            }
                                            currentStep = ""
                                            keyboardController?.hide()
                                        }
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp),
                            )
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(text = "Steps:")
                                steps.forEachIndexed { index, step ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${index + 1}.$step",
                                            Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                currentStep = step
                                                editStepIndex = index
                                                hasBeenEdited = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.LightGray,
                                                contentColor = Color.Black
                                            ),
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .size(48.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = android.R.drawable.ic_menu_edit),
                                                contentDescription = "Edit Step",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                steps.removeAt(index)
                                                hasBeenEdited = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.LightGray,
                                                contentColor = Color.Black
                                            ),
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .size(48.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = android.R.drawable.ic_menu_delete),
                                                contentDescription = "Delete Step",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
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
                                steps.isNotEmpty() &&
                                hasBeenEdited

                Button(
                    onClick = { showWarningDialog = true },
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
                    Text(text ="Update Recipe")
                }
            }
        }
    }
}