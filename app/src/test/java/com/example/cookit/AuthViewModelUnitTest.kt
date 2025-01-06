package com.example.cookit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.cookit.screens.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*


class AuthViewModelUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockAuth = mock(FirebaseAuth::class.java)
    private val viewModel = AuthViewModel(mockAuth)

    @Test
    fun `onNameChange updates UI state`() {
        viewModel.onNameChange("John Doe")
        assertEquals("John Doe", viewModel.uiState.value.name)
    }

    @Test
    fun `onUsernameChange updates UI state`() {
        viewModel.onUsernameChange("test@example.com")
        assertEquals("test@example.com", viewModel.uiState.value.username)
    }

    @Test
    fun `onPasswordChange updates UI state`() {
        viewModel.onPasswordChange("password123")
        assertEquals("password123", viewModel.uiState.value.password)
    }

    @Test
    fun `validateInputs returns false when name is empty`() {
        viewModel.onNameChange("")
        viewModel.onUsernameChange("test@example.com")
        viewModel.onPasswordChange("password123")

        assertFalse(viewModel.validateInputs())
        assertEquals("Name cannot be empty", viewModel.uiState.value.signUpError)
    }

    @Test
    fun `validateInputs returns false when username is empty`() {
        viewModel.onNameChange("John Doe")
        viewModel.onUsernameChange("")
        viewModel.onPasswordChange("password123")

        assertFalse(viewModel.validateInputs())
        assertEquals("Email cannot be empty", viewModel.uiState.value.signUpError)
    }

    @Test
    fun `validateInputs returns false when password is less than 8 characters`() {
        viewModel.onNameChange("John Doe")
        viewModel.onUsernameChange("test@example.com")
        viewModel.onPasswordChange("pass")

        assertFalse(viewModel.validateInputs())
        assertEquals("Password must be at least 8 characters", viewModel.uiState.value.passwordError)
    }

    @Test
    fun `validateInputs returns false when password matches username`() {
        viewModel.onNameChange("John Doe")
        viewModel.onUsernameChange("test@example.com")
        viewModel.onPasswordChange("test@example.com")

        assertFalse(viewModel.validateInputs())
        assertEquals("Password cannot be the username", viewModel.uiState.value.passwordError)
    }

    @Test
    fun `validateInputs returns true when all inputs are valid`() {
        viewModel.onNameChange("John Doe")
        viewModel.onUsernameChange("test@example.com")
        viewModel.onPasswordChange("password123")

        assertTrue(viewModel.validateInputs())
        assertEquals("", viewModel.uiState.value.passwordError)
        assertEquals("", viewModel.uiState.value.signUpError)
    }
}