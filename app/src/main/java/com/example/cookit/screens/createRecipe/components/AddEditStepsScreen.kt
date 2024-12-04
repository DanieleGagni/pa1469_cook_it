package com.example.cookit.screens.createRecipe.components

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

@Preview
@Composable
fun AddEditStepsScreenPreview() {
    AddEditStepsScreen (
        onClick = { steps -> println("Posted ingredients: $steps")}
    )
}