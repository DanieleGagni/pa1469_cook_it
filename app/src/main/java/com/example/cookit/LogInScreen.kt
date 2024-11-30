package com.example.cookit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LogInScreen(navController: NavHostController) {

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordError by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    fun validatePassword(username: String, password: String) {
        when {
            password.length < 8 -> passwordError = "Password must be at least 8 characters"
            password == username -> passwordError = "Password cannot be username"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF5DD))
            )
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(100.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Your image description",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp) // Height of the Box
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White) // Background color
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("john.doe") },
                        modifier = Modifier
                            .fillMaxSize() // Ensures OutlinedTextField fills the Box
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person, // Change to your desired icon
                                contentDescription = "Username Icon", // Optional description
                                modifier = Modifier.padding(start = 16.dp) // Optional padding inside the TextField
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp) // Height of the Box
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White) // Background color
                ) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("password") },
                        modifier = Modifier
                            .fillMaxSize() // Ensures OutlinedTextField fills the Box
                            .fillMaxWidth(),
                        //.padding(horizontal = 16.dp), // Padding inside the TextField
                        shape = RoundedCornerShape(25.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock, // Change to your desired icon
                                contentDescription = "Username Icon", // Optional description
                                modifier = Modifier.padding(start = 16.dp) // Optional padding inside the TextField
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = {
                        // Perform login logic here
                        // navController.navigate("home")
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                ) {
                    Text("Log In")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize() // Fills the entire screen size
                        .background(Color(0xFFFFF5DD)) // Optional background color
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize() // Ensures the Column fills the entire screen
                            .padding(16.dp), // Optional padding for the Column
                        verticalArrangement = Arrangement.Center, // Centers the content vertically
                        horizontalAlignment = Alignment.CenterHorizontally // Centers the content horizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between text and button
                            verticalAlignment = Alignment.CenterVertically, // Vertically center the items
                        ) {
                            Text(
                                text = "Don't have an account?",
                                color = Color.Black, // Text color
                                fontSize = 16.sp, // Font size
                                modifier = Modifier.padding(end = 8.dp) // Padding between text and button
                            )

                            // Custom Pressable Component for Sign Up
                            Surface(
                                onClick = {
                                    navController.navigate("signUp")
                                },
                                modifier = Modifier
                                    .height(48.dp) // Set height for the button
                                    .clip(RoundedCornerShape(24.dp)) // Rounded corners for the button
                                    .padding(vertical = 4.dp), // Optional padding for the button
                                color = Color(0xFFFFF5DD) // Background color of the button
                            ) {
                                Box(contentAlignment = Alignment.Center) { // Ensures text is centered in the button
                                    Text(
                                        text = "Sign up", // Button text
                                        modifier = Modifier.padding(8.dp), // Padding inside the button
                                        color = Color.Blue, // Text color
                                        fontSize = 16.sp // Font size
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    )
}


