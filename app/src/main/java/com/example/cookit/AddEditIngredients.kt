package com.example.cookit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

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
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(text = "Edit")
                    }
                    Button(
                        onClick = {
                            ingredients.removeAt(index)
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

@Preview
@Composable
fun AddEditIngredientsPreview() {
    AddEditIngredients(
        onClick = { ingredients -> println("Posted ingredients: $ingredients")}
    )
}