package com.example.cookit.screens.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cookit.R

@Composable
fun ShoppingListScreen(navController: NavHostController) {

    var ingredient by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            com.example.cookit.screens.home.BottomNavigationBar(navController) // Barra de navegación inferior
        },
        content = { innerPadding ->

            Box(
                modifier = Modifier
                    //.fillMaxSize()
                    .wrapContentHeight()
                    .padding(innerPadding) // Ensure padding includes insets from Scaffold
                    .imePadding() // Adjust for keyboard
                    .background(Color.White)
            ) {
                // Main content scrollable
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        //.verticalScroll(rememberScrollState()) // Allow scrolling
                        .padding(16.dp), // Adjust padding for content
                    horizontalAlignment = Alignment.CenterHorizontally // Center the content
                ) {
                    Spacer(modifier = Modifier.height(24.dp)) // More space at the top

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFF58D1E),
                                    fontWeight = FontWeight.Bold // Bold applied here
                                )
                            ) {
                                append("SHOPPING ")
                            }
                            append("LIST") // No bold applied to "LIST"
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    //------------------------CHECKLIST SHOULD BE HERE

                    // Just for spacing above the text input field
                    //Spacer(modifier = Modifier.weight(1f)) // Push content upwards

                }

                // Create a Row for the TextField and Button side by side
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(horizontal = 16.dp)
                        .align(Alignment.BottomCenter), // Align at the bottom
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between TextField and Button
                ) {
                    OutlinedTextField(
                        value = ingredient,
                        onValueChange = { newText -> ingredient = newText }, // Update the state
                        placeholder = { Text("Add ingredient...") },
                        modifier = Modifier
                            .weight(1f) // Make the TextField take up the available space, but not fill the entire width
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp)
                    )

                    // Add a Button next to the TextField
                    IconButton(
                        onClick = { /* Handle button click */ },
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterVertically) // Vertically center the button with the text field
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_black), // Use the appropriate icon
                            contentDescription = "Add Ingredient Icon",
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    )
}


// Barra inferior de navegación
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 4.dp), // Reducir padding vertical
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                navController.navigate("home")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home Icon",
                tint = Color.Unspecified
            )
        }
        IconButton(
            onClick = {
                navController.navigate("shoppingList")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stats),
                contentDescription = "Shopping List Icon",
                tint = Color.Unspecified
            )
        }
        IconButton(
            onClick = {
                navController.navigate("createRecipe")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Icon",
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
        }
        IconButton(onClick = { /* Navegar a Favoritos */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Favorite Icon",
                tint = Color.Unspecified
            )
        }
        IconButton(onClick = { /* Navegar a Settings */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings Icon",
                tint = Color.Unspecified
            )
        }
    }
}