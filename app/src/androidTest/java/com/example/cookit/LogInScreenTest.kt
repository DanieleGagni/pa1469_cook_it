package com.example.cookit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookit.screens.home.HomeScreen
import com.example.cookit.screens.logIn.LogInScreen
import com.example.cookit.screens.logIn.LoginViewModel
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

        val viewModel = LoginViewModel()

        // Act: Set the content with a programmatic NavHost
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") { LogInScreen(navController = navController, viewModel = viewModel) }
                composable("home") { HomeScreen(navController) }
            }
        }

        // Assert: Check if UI elements are displayed correctly
        composeTestRule.onNodeWithText("john.doe@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account?").assertIsDisplayed()

    }
}