package com.example.cookit.screens.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun AddEditIngredients(
    onClick: (List<String>) -> Unit
) {
    var currentIngredient by remember { mutableStateOf("") }
    val ingredients = remember { mutableStateListOf<String>() }
    var editIndex by remember { mutableStateOf(-1) }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = currentIngredient,
            onValueChange = { newText -> currentIngredient = newText},
            label = {
                Text(
                    text = if (editIndex >= 0) "Edit Ingredient" else "Enter Ingredient"
                )
            },
            maxLines = 1
        )
        Button(
            onClick = {
                if (currentIngredient.isNotBlank()) {
                    if (editIndex >= 0) {
                        ingredients[editIndex] = currentIngredient
                        editIndex = -1
                    } else {
                        ingredients.add(currentIngredient)
                    }
                    currentIngredient = ""
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = if (editIndex >= 0) "Update Ingredient" else "Add Ingredient"
            )
        }
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
                        modifier = Modifier.padding(horizontal = 3.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_edit),
                            contentDescription = "Edit Ingredient"
                        )
                    }
                    Button(
                        onClick = {
                            ingredients.removeAt(index)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_delete),
                            contentDescription = "Delete Ingredient"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditStepsScreen(
    onClick: (List<String>) -> Unit
) {
    var currentStep by remember { mutableStateOf("") }
    val steps = remember { mutableStateListOf<String>() }
    var editIndex by remember { mutableStateOf(-1) }
    var insertIndex by remember { mutableStateOf(-1) }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = currentStep,
            onValueChange = { newText -> currentStep = newText},
            label = {
                Text(
                    text = when {
                        editIndex >= 0 -> "Edit Step"
                        insertIndex >= 0 -> "Insert Step"
                        else -> "Enter Step"
                    }
                )
            },
            maxLines = 1
        )
        Button(
            onClick = {
                if (currentStep.isNotBlank()) {
                    when {
                        editIndex >= 0 -> {
                            steps[editIndex] = currentStep
                            editIndex = -1
                        }

                        insertIndex >= 0 -> {
                            steps.add(insertIndex, currentStep)
                            insertIndex = -1
                        }

                        else -> {
                            steps.add(currentStep)
                        }
                    }
                    currentStep = ""
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = if (editIndex >= 0) "Update Step" else "Add Step"
            )
        }
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
                        text = "${index+1}.$step",
                        Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            currentStep = step
                            editIndex = index
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = "Edit")
                    }
                    Button(
                        onClick = {
                            currentStep = ""
                            insertIndex = index + 1
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = "Insert")
                    }
                    Button(
                        onClick = {
                            steps.removeAt(index)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRecipeScreen(
    navController: NavHostController // Pass the NavController for navigation
) {
    var title by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var serves by remember { mutableStateOf("") }
    val ingredients = remember { mutableStateListOf<String>() }
    val steps = remember { mutableStateListOf<String>() }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            Box {
                Text(text = "Create Recipe",
                    modifier = Modifier
                        .background(color = Color.Red)
                        .width(300.dp)
                )
            }
        }

        // TextField to enter the recipe name
        item {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "Enter Menu Name") },
                maxLines = 1
            )
        }

        // TextField to enter the type of dish
        item {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = type,
                onValueChange = { type = it },
                label = { Text(text = "Enter Type") },
                maxLines = 1
            )
        }

        // TextField to enter estimated time
        item {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = estimatedTime,
                onValueChange = { estimatedTime = it },
                label = { Text(text = "Enter Estimated Time (minutes)") },
                maxLines = 1
            )
        }

        // TextField to enter how many people it serves
        item {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = serves,
                onValueChange = { serves = it },
                label = { Text(text = "Enter Servings") },
                maxLines = 1
            )
        }

        // Add ingredients section
        item {
            AddEditIngredients(
                onClick = { ingredients ->
                    println("Posted ingredients: $ingredients")
                }
            )
        }

        // Add steps section
        item {
            AddEditStepsScreen(
                onClick = { steps ->
                    println("Posted steps: $steps")
                }
            )
        }

        // Post Button
        item {
            Button(
                onClick = {
                    // Add navigation to the home screen or perform your post action
                    navController.navigate("home")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Save Recipe")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateRecipeScreenPreview() {
    val navController = rememberNavController() // Dummy NavController for preview
    CreateRecipeScreen(navController)
}
