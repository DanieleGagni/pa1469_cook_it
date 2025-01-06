package com.example.cookit.screens.createRecipe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.navigation.NavHostController
import com.example.cookit.screens.components.Recipe
import com.example.cookit.ui.theme.darkOrange
import com.example.cookit.ui.theme.lightGrey
import com.example.cookit.ui.theme.lightOrange
import com.example.cookit.ui.theme.lightRed

class CreateRecipeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateRecipeScreen()
        }
    }
}

@Composable
fun AddEditIngredients(ingredients: MutableList<String>) {
    var currentIngredient by remember { mutableStateOf("") }
    var editIndex by remember { mutableStateOf(-1) }
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = currentIngredient,
            onValueChange = { newText -> currentIngredient = newText },
            label = {
                Text(
                    text = if (editIndex >= 0) "Edit Ingredient" else "Enter Ingredient"
                )
            },
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = lightOrange,
                unfocusedContainerColor = lightOrange,
                disabledContainerColor = lightGrey,
                errorContainerColor = lightRed,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (currentIngredient.isNotBlank()) {
                        if (editIndex >= 0) {
                            ingredients[editIndex] = currentIngredient
                            editIndex = -1
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
                            editIndex = index
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

@Composable
fun AddEditStepsScreen(steps: MutableList<String>) {
    var currentStep by remember { mutableStateOf("") }
    var editIndex by remember { mutableStateOf(-1) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = currentStep,
            onValueChange = { newText -> currentStep = newText },
            label = {
                Text(
                    text = when {
                        editIndex >= 0 -> "Edit Step"
                        else -> "Enter Step"
                    }
                )
            },
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = lightOrange,
                unfocusedContainerColor = lightOrange,
                disabledContainerColor = lightGrey,
                errorContainerColor = lightRed,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (currentStep.isNotBlank()) {
                        when {
                            editIndex >= 0 -> {
                                steps[editIndex] = currentStep
                                editIndex = -1
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
                            editIndex = index
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

@Composable
fun CreateRecipeScreen(
    navController: NavHostController,
    viewModel: CreateRecipeViewModel
) {
    var title by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var serves by remember { mutableStateOf("") }
    val ingredients = remember { mutableStateListOf<String>() }
    val steps = remember { mutableStateListOf<String>() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val typeOptions = listOf("vegetarian", "quick", "complex", "other")


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = buildAnnotatedString {
                        append("Create")
                        withStyle(
                            style = SpanStyle(
                                color = darkOrange,
                                fontWeight = FontWeight.Bold
                            )
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

        // TextField to enter the recipe name
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
                    modifier = Modifier.padding(start = 5.dp)
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = "Enter Recipe Name") },
                    maxLines = 1,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightOrange,
                        unfocusedContainerColor = lightOrange,
                        disabledContainerColor = lightGrey,
                        errorContainerColor = lightRed,
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

        // TextField to enter the type of dish
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
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

        // TextField to enter estimated time
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
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            estimatedTime = newValue
                        }
                    },
                    label = { Text(text = "Enter Estimated Time (minutes)") },
                    maxLines = 1,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightOrange,
                        unfocusedContainerColor = lightOrange,
                        disabledContainerColor = lightGrey,
                        errorContainerColor = lightRed,
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

        // TextField to enter how many people it serves
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
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            serves = newValue
                        }
                    },
                    label = { Text(text = "Enter Servings") },
                    maxLines = 1,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightOrange,
                        unfocusedContainerColor = lightOrange,
                        disabledContainerColor = lightGrey,
                        errorContainerColor = lightRed,
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

        // Add ingredients section
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
            ) {
                Text(
                    text = "INGREDIENTS",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
                AddEditIngredients(ingredients)
            }
        }

        // Add steps section
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
                    modifier = Modifier.padding(start = 8.dp)

                )
                AddEditStepsScreen(steps)
            }
        }

        // Post Button
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

                    val recipe = Recipe.create(
                        title = title,
                        ingredients = ingredients,
                        estimatedTime = estimatedTime.toInt(),
                        serves = serves.toInt(),
                        steps = steps,
                        type = type
                    )

                    Log.d(
                        "[------------------------- DEBUG]",
                        "------------------------- ${recipe.title}"
                    )
                    recipe.ingredients.forEach { ingredient ->
                        Log.d(
                            "[------------------------- DEBUG]",
                            "------------------------- $ingredient"
                        )
                    }
                    steps.forEach { step ->
                        Log.d(
                            "[------------------------- DEBUG]",
                            "------------------------- $step"
                        )
                    }

                    viewModel.addRecipe(navController, recipe)

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
                Text(text = "Save Recipe")
            }
        }
    }
}

