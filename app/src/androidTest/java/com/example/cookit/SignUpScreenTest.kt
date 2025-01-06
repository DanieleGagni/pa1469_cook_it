package com.example.cookit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookit.screens.home.HomeScreen
import com.example.cookit.screens.logIn.LogInScreen
import com.example.cookit.screens.logIn.LoginViewModel
import com.example.cookit.screens.signUp.SignUpScreen
import com.example.cookit.screens.signUp.SignUpViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.sign

@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSignUpScreenUIElements() {
        // Arrange: Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val signUpViewModel = SignUpViewModel()
        val logInViewModel = LoginViewModel()


        // Act: Set the content with a NavHost for navigation simulation
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "signUp"
            ) {
                composable("signUp") {
                    SignUpScreen(navController = navController, viewModel = signUpViewModel)
                }
                composable("login") {
                    LogInScreen(navController = navController, viewModel = logInViewModel)
                }
                composable("home") {
                    HomeScreen(navController)
                }
            }
        }

        // Assert: Check if UI elements are displayed correctly
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("john.doe@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("logInButton").assertIsDisplayed()
    }



    //NOT WORKING PROBLEMS WITH LOGIC I THINK (what doesnt work is the redirection to the home screen when clicking)
    @Test
    fun testSignUpButtonFunctionality() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val viewModel = SignUpViewModel()

        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "signUp"
            ) {
                composable("signUp") {
                    SignUpScreen(navController = navController, viewModel = viewModel)
                }
                composable("home") {
                    HomeScreen(navController)
                }
            }
        }

        // Interact with UI elements: Enter name, email, and password
        composeTestRule.onNodeWithText("John Doe").performTextInput("Test User")
        composeTestRule.onNodeWithText("john.doe@example.com").performTextInput("testuser@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")

        // Click the "Create Account" button
        composeTestRule.onNodeWithText("Create Account").performClick()

        // Verify navigation to "home" (assuming successful sign-up)
        composeTestRule.onNodeWithTag("homeScreen").assertIsDisplayed()
        //assert(navController.currentDestination?.route == "home")
    }

    @Test
    fun testSignUpValidation() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val viewModel = SignUpViewModel()

        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "signUp"
            ) {
                composable("signUp") {
                    SignUpScreen(navController = navController, viewModel = viewModel)
                }
            }
        }

        // Leave fields empty and try signing up
        composeTestRule.onNodeWithText("Create Account").performClick()

        // Assert error message is displayed
        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()

        // Enter invalid password and check for error
        composeTestRule.onNodeWithText("John Doe").performTextInput("Test User2")
        composeTestRule.onNodeWithText("john.doe@example.com").performTextInput("testuser2@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("short")
        composeTestRule.onNodeWithText("Create Account").performClick()
        composeTestRule.onNodeWithText("Password must be at least 8 characters").assertIsDisplayed()
    }
}