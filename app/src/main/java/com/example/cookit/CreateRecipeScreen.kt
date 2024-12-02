package com.example.cookit

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CreateRecipeScreen(
    onClick: () -> Unit,
    onChange: (String) -> Unit
) {
    var text1 by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = text1,
            onValueChange = { newText1 ->
                text1 = newText1
                onChange(newText1)
            },
            label = { Text(text = "Enter Menu Name")},
            maxLines = 5
        )
        AddEditIngredients(
            onClick = { ingredients -> println("Posted ingredients: $ingredients")}
        )
        AddEditStepsScreen (
            onClick = { steps -> println("Posted ingredients: $steps")}
        )
        Button(onClick = onClick) {
            Text(text = "Post")
        }
    }
}

@Preview
@Composable
fun CreateRecipeScreenPreview() {
    CreateRecipeScreen(
        onClick = { },
        onChange = { }
    )
}