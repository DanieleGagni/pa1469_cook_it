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

@RunWith(AndroidJUnit4::class)
class LogInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun testLogInScreenUIElements() {
        // Arrange: Set up the NavController and ViewModel
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val logInViewModel = LoginViewModel()
        val signUpViewModel = SignUpViewModel()

        // Act: Set the content with a programmatic NavHost
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LogInScreen(navController = navController, viewModel = logInViewModel)
                }
                composable("signUp") {
                    SignUpScreen(navController = navController, viewModel = signUpViewModel)
                }
                composable("home") {
                    HomeScreen(navController)
                }
            }
        }

        // Assert: Check if UI elements are displayed correctly
        composeTestRule.onNodeWithText("john.doe@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("signUpButton").assertIsDisplayed()

    }


    //NOT WORKING PROBLEMS WITH LOGIC I THINK (what doesnt work is the redirection to the home screen when clicking)
    @Test
    fun testLoginButtonFunctionality() {
        // Arrange: Set up the NavController and ViewModel
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val logInViewModel = LoginViewModel()
        val signUpViewModel = SignUpViewModel()

        // Act: Set the content with a programmatic NavHost
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LogInScreen(navController = navController, viewModel = logInViewModel)
                }
                composable("signUp") {
                    SignUpScreen(navController = navController, viewModel = signUpViewModel)
                }
                composable("home") {
                    HomeScreen(navController)
                }
            }
        }

        // Interact with the UI: Input username and password
        composeTestRule.onNodeWithText("john.doe@example.com", ignoreCase = true)
            .performTextInput("m@example.com")
        composeTestRule.onNodeWithText("Password", ignoreCase = true)
            .performTextInput("00000000")

        /*
        // Click the login button
        composeTestRule.onNodeWithText("Log In", ignoreCase = true).performClick()

        // Wait for any recompositions
        composeTestRule.waitForIdle()

        // Optionally, check for navigation or loading state
        //composeTestRule.onNodeWithText("Log In", ignoreCase = true).assertIsNotEnabled() // Example: Check if the button disables during loading

        // Verify navigation to "home" (assuming successful login)
        assert(navController.currentDestination?.route == "home")
         */

        // go to home page
        composeTestRule.onNodeWithText("Log In").performClick()
        //PROBLEMS CHECKING IT GOES TO THE HOME SCREEN WHEN CLICKING LOG IN BUTTON
        composeTestRule.onNodeWithTag("homeScreen").assertIsDisplayed()
    }


    //NOT WORKING
    @Test
    fun tesSignUpButtonFunctionality() {
        // Arrange: Set up the NavController and ViewModel
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        val logInViewModel = LoginViewModel()
        val signUpViewModel = SignUpViewModel()

        // Act: Set the content with a programmatic NavHost
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LogInScreen(navController = navController, viewModel = logInViewModel)
                }
                composable("signUp") {
                    SignUpScreen(navController = navController, viewModel = signUpViewModel)
                }
            }
        }

        // go to signUp screen
        composeTestRule.onNodeWithTag("signUpButton").performClick()
        composeTestRule.onNodeWithTag("signUpScreen").assertIsDisplayed()
    }

}