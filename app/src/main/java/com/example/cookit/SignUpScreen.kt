package com.example.cookit


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SignUpScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordError by remember { mutableStateOf("") }
    var signUpError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun validatePassword(username: String, password: String): Boolean {
        return when {
            password.length < 8 -> {
                passwordError = "Password must be at least 8 characters"
                false
            }
            password == username -> {
                passwordError = "Password cannot be the username"
                false
            }
            else -> {
                passwordError = ""
                true
            }
        }
    }

    fun handleSignUp() {
        if (validatePassword(username.text, password.text)) {
            isLoading = true
            signUpError = "" // Clear previous errors
            auth.createUserWithEmailAndPassword(username.text, password.text)
                .addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        // Navigate to home screen after successful sign-up
                        navController.navigate("home")
                    } else {
                        // Display the error message
                        signUpError = task.exception?.localizedMessage ?: "Sign up failed"
                    }
                }
        }
    }

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

                    Spacer(modifier = Modifier.height(screenHeight * 0.1f))

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.height(screenHeight * 0.05f))

                    Text(
                        text = "Just a few quick things to get started",
                        fontSize = (screenWidth.value * 0.05f).sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("john.doe@example.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                    )

                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = Color.Red,
                            fontSize = (screenWidth.value * 0.04f).sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if (signUpError.isNotEmpty()) {
                        Text(
                            text = signUpError,
                            color = Color.Red,
                            fontSize = (screenWidth.value * 0.04f).sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = { handleSignUp() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.07f),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text("Create Account")
                        }
                    }

                    Spacer(modifier = Modifier.height(screenHeight * 0.05f))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Already have an account?",
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            Surface(
                                onClick = {
                                    //PRESSABLE FUNCTIONALITY HAS TO CHANGE TO HOME SCREEN
                                    navController.navigate("logIn")
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .padding(vertical = 4.dp),
                                color = Color(0xFFFFF5DD)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Log In",
                                        modifier = Modifier.padding(8.dp),
                                        color = Color.Blue,
                                        fontSize = 16.sp
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

