package com.example.cookit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogInScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordError by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Handle the login logic
    fun handleLogin() {
        isLoading = true
        loginError = "" // Clear previous errors
        auth.signInWithEmailAndPassword(username.text, password.text)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    // Navigate to the home screen upon successful login
                    navController.navigate("signUp")
                } else {
                    // Show error message
                    loginError = task.exception?.localizedMessage ?: "Login failed"
                }
            }
    }

    // Get screen width and height
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF5DD))
            ) {

                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Adjust the size of the logo dynamically based on screen size
                    Spacer(modifier = Modifier.height(50.dp))

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenWidth * 0.6f) // Adjusting height to 60% of screen width
                            .padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    // Username TextField
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("john.doe@example.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") }
                    )

                    // Password TextField
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") }
                    )

                    // Display error message if login fails
                    if (loginError.isNotEmpty()) {
                        Text(
                            text = loginError,
                            color = Color.Red,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Login Button
                    FilledTonalButton(
                        onClick = { handleLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text("Log In")
                        }
                    }


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



