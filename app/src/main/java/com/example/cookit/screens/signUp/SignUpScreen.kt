package com.example.cookit.screens.signUp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.cookit.R
import com.example.cookit.ui.theme.backOrange
import com.example.cookit.ui.theme.darkOrange
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

// Data class for holding the UI state
data class SignUpUiState(
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val passwordError: String = "",
    val signUpError: String = "",
    val isLoading: Boolean = false
)

// ViewModel for SignUpScreen
class SignUpViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> get() = _uiState

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onUsernameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    private fun validateInputs(): Boolean {
        val state = _uiState.value
        return when {
            state.name.isEmpty() -> {
                _uiState.value = state.copy(signUpError = "Name cannot be empty")
                false
            }
            state.username.isEmpty() -> {
                _uiState.value = state.copy(signUpError = "Email cannot be empty")
                false
            }
            state.password.length < 8 -> {
                _uiState.value = state.copy(passwordError = "Password must be at least 8 characters")
                false
            }
            state.password == state.username -> {
                _uiState.value = state.copy(passwordError = "Password cannot be the username")
                false
            }
            else -> {
                _uiState.value = state.copy(passwordError = "", signUpError = "")
                true
            }
        }
    }

    fun handleSignUp(navController: NavHostController) {
        if (validateInputs()) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val name = _uiState.value.name
            val username = _uiState.value.username
            val password = _uiState.value.password

            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            val profileUpdates = userProfileChangeRequest {
                                displayName = name
                            }
                            it.updateProfile(profileUpdates).addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    navController.navigate("home") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                } else {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        signUpError = "Failed to update profile: ${updateTask.exception?.localizedMessage}"
                                    )
                                }
                            }
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            signUpError = task.exception?.localizedMessage ?: "Sign up failed"
                        )
                    }
                }
        }
    }
}

@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: SignUpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

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
                        .testTag("signUpScreen")
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(screenHeight * 0.1f))

                    Image(
                        painter = painterResource(id = R.drawable.cookit),
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
                        value = uiState.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        placeholder = {
                            Text(
                                text = "John Doe",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
                    )

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
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
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

                    if (uiState.passwordError.isNotEmpty()) {
                        Text(
                            text = uiState.passwordError,
                            color = Color.Red,
                            fontSize = (screenWidth.value * 0.04f).sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if (uiState.signUpError.isNotEmpty()) {
                        Text(
                            text = uiState.signUpError,
                            color = Color.Red,
                            fontSize = (screenWidth.value * 0.04f).sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = { viewModel.handleSignUp(navController) },
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
                                text = "Create Account",
                                style = MaterialTheme.typography.labelLarge
                            )
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
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            Surface(
                                onClick = {
                                    navController.navigate("logIn")
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .testTag("logInButton")
                                    .padding(vertical = 4.dp),
                                color = Color.White
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Log In",
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
        }
    )
}

