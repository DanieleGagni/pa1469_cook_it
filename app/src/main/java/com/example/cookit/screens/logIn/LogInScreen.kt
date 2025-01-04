package com.example.cookit.screens.logIn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cookit.R
import com.example.cookit.ui.theme.backOrange
import com.example.cookit.ui.theme.darkOrange
import com.example.cookit.ui.theme.lightOrange
import com.google.firebase.auth.FirebaseAuth


data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val loginError: String = "",
    val isLoading: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> get() = _uiState

    fun onUsernameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun login(navController: NavHostController) {
        val username = _uiState.value.username
        val password = _uiState.value.password

        // Reset error and show loading
        _uiState.value = _uiState.value.copy(isLoading = true, loginError = "")

        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginError = task.exception?.localizedMessage ?: "Login failed"
                    )
                }
            }
    }
}

@Composable
fun LogInScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel() // Default ViewModel instance
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
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

                    Spacer(modifier = Modifier.height(50.dp))

                    Image(
                        painter = painterResource(id = R.drawable.cookit),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(LocalConfiguration.current.screenWidthDp.dp * 0.6f)
                            .padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = { viewModel.onUsernameChange(it) },
                        placeholder = {
                            Text(
                                text = "john.doe@example.com",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") }
                    )

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = {
                            Text(
                                text = "Password",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") }
                    )

                    if (uiState.loginError.isNotEmpty()) {
                        Text(
                            text = uiState.loginError,
                            color = Color.Red,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = { viewModel.login(navController) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = darkOrange,
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "Log In",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }


                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Don't have an account?",
                            style = MaterialTheme.typography.bodyMedium,
                            //color = Color.Black,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Surface(
                            onClick = {
                                navController.navigate("signUp")
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .padding(vertical = 4.dp),
                            //screen's background color
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Sign up",
                                    //style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(8.dp),
                                    color = darkOrange
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
