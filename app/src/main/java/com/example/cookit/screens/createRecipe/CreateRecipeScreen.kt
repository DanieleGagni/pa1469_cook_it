package com.example.cookit.screens.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xB3F58D1E),
                contentColor = Color.Black
            ),
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
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xB3F58D1E),
                contentColor = Color.Black
            ),
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
                            currentStep = ""
                            insertIndex = index + 1
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
                            painter = painterResource(id = android.R.drawable.ic_input_add),
                            contentDescription = "Insert Step",
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
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item{
            Box {
                Text(
                    text = buildAnnotatedString {
                        append("Create")
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

        // TextField to enter the recipe name
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
                )
            }
        }

        // TextField to enter the type of dish
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
                    maxLines = 1
                )
            }
        }

        // TextField to enter estimated time
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
                    onValueChange = { estimatedTime = it },
                    label = { Text(text = "Enter Estimated Time (minutes)") },
                    maxLines = 1
                )
            }
        }

        // TextField to enter how many people it serves
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
                    onValueChange = { serves = it },
                    label = { Text(text = "Enter Servings") },
                    maxLines = 1
                )
            }
        }

        // Add ingredients section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Text(
                    text = "INGREDIENTS",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp)
                )
                AddEditIngredients(
                    onClick = { ingredients ->
                        println("Posted ingredients: $ingredients")
                    }
                )
            }
        }

        // Add steps section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Text(
                    text = "STEPS",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp)
                )
                AddEditStepsScreen(
                    onClick = { steps ->
                        println("Posted steps: $steps")
                    }
                )
            }
        }

        // Post Button
        item {
            Button(
                onClick = {
                    // Add navigation to the home screen or perform your post action
                    navController.navigate("home")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF58D1E),
                    contentColor = Color.Black
                ),
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
