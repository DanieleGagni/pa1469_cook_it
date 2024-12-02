package com.example.cookit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
                    navController.navigate("home")
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
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Adjust the size of the logo dynamically based on screen size
                    Spacer(modifier = Modifier.height(100.dp))

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
                    Button(
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

                    // Sign-up Section
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Don't have an account? ")
                        TextButton(onClick = { navController.navigate("signUp") }) {
                            Text("Sign Up", color = Color.Blue)
                        }
                    }
                }
            }
        }
    )
}



