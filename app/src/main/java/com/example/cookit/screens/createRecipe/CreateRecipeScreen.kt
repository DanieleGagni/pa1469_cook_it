package com.example.cookit.screens.createRecipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cookit.screens.createRecipe.components.AddEditIngredients
import com.example.cookit.screens.createRecipe.components.AddEditStepsScreen

@Composable
fun CreateRecipeScreen(
    navController: NavHostController // Pass the NavController for navigation
) {
    var menuName by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField to enter the recipe name
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = menuName,
            onValueChange = { newText -> menuName = newText },
            label = { Text(text = "Enter Menu Name") },
            maxLines = 1
        )

        // Add ingredients section
        AddEditIngredients(
            onClick = { ingredients ->
                println("Posted ingredients: $ingredients")
            }
        )

        // Add steps section
        AddEditStepsScreen(
            onClick = { steps ->
                println("Posted steps: $steps")
            }
        )

        // Post Button
        Button(
            onClick = {
                // Add navigation to the home screen or perform your post action
                navController.navigate("home")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Post")
        }
    }
}
