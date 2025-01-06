package com.example.cookit.screens.auth

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


// Data class for holding the UI state
data class AuthUiState(
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val passwordError: String = "",
    val signUpError: String = "",
    val loginError: String = "",
    val isLoading: Boolean = false
)

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> get() = _uiState

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onUsernameChange(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun validateInputs(): Boolean {
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
                                        popUpTo(0) { inclusive = true } // Clears the entire back stack
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

    fun login(navController: NavHostController) {
        val username = _uiState.value.username
        val password = _uiState.value.password
        // Reset error and show loading
        _uiState.value = _uiState.value.copy(isLoading = true, loginError = "")
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true } // Clears the entire back stack
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
