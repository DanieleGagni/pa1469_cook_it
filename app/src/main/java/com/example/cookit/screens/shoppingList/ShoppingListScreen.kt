package com.example.cookit.screens.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.ui.theme.darkOrange
import com.example.cookit.ui.theme.lightGrey


@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    viewModel: ShoppingListViewModel = viewModel()
) {
    val shoppingList by viewModel.shoppingList.collectAsState()
    var newIngredient by remember { mutableStateOf(TextFieldValue("")) }

    var showWarningDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchShoppingList(navController)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = darkOrange)) {
                                append("SHOPPING ")
                            }
                            append("LIST")
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 25.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    if (shoppingList.isEmpty()) {
                        Text(
                            text = "Your shopping list is empty. Start adding items!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        shoppingList.forEachIndexed { index, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = item.done,
                                    onCheckedChange = {
                                        viewModel.toggleItemStatus(navController, index)
                                    }
                                )
                                Text(
                                    text = item.entry,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.removeShoppingItem(navController, index) }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.remove),
                                        contentDescription = "Remove Icon",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Unspecified
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = newIngredient,
                        onValueChange = { newIngredient = it },
                        placeholder = { Text("Add ingredients") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                    )

                    IconButton(
                        onClick = {
                            viewModel.addShoppingItem(navController, newIngredient.text)
                            newIngredient = TextFieldValue("") // Clear input
                        },
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_black),
                            contentDescription = "Add Icon",
                            tint = Color.Unspecified
                        )
                    }
                }

                Button(
                    onClick = { showWarningDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkOrange)
                ) {
                    Text(
                        text = "REMOVE ALL",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Confirmation Dialog
            if (showWarningDialog) {
                AlertDialog(
                    onDismissRequest = { showWarningDialog = false },
                    title = { Text(text = "Clear Shopping List") },
                    text = { Text("Are you sure you want to remove all items from your shopping list? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.removeAllItems(navController)
                            showWarningDialog = false
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showWarningDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = lightGrey
                )
            }
        }
    )
}
