package com.example.cookit

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookit.screens.components.NavigationBar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationBarIconsAreDisplayed() {
        // Arrange: Set up a test NavController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        // Act: Set the content with the NavigationBar
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "navigationBar"
            ) {
                composable("navigationBar") { NavigationBar(navController = navController) }
                composable("home") { /* Mock Home Screen */ }
                composable("shoppingList") { /* Mock Shopping List Screen */ }
                composable("createRecipe") { /* Mock Create Recipe Screen */ }
                composable("listRecipes/{ids}?isFavorites={isFavorites}") { /* Mock List Recipes Screen */ }
                composable("logIn") { /* Mock Log In Screen */ }
            }
        }

        // Assert: Check if all navigation icons are displayed
        composeTestRule.onNodeWithContentDescription("Home Icon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Shopping List Icon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Icon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Favorite Icon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Settings Icon").assertIsDisplayed()
    }

    @Test
    fun testNavigationToHome() {
        // Arrange: Set up a test NavController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        // Act: Set the content with the NavigationBar
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "navigationBar"
            ) {
                composable("navigationBar") { NavigationBar(navController = navController) }
                composable("home") { /* Mock Home Screen */ }
            }
        }

        // Act: Simulate clicking the "Home" icon
        composeTestRule.onNodeWithContentDescription("Home Icon").performClick()

        // Assert: Check if the NavController navigates to "home"
        assert(navController.currentDestination?.route == "home")
    }

    @Test
    fun testSettingsMenu() {
        // Arrange: Set up a test NavController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        // Act: Set the content with the NavigationBar
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "navigationBar"
            ) {
                composable("navigationBar") { NavigationBar(navController = navController) }
                composable("logIn") { /* Mock Log In Screen */ }
            }
        }

        // Act: Open the settings dropdown menu
        composeTestRule.onNodeWithContentDescription("Settings Icon").performClick()

        // Assert: Check if all dropdown menu options are displayed
        composeTestRule.onNodeWithText("My recipes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log Out").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete account").assertIsDisplayed()
    }

    @Test
    fun testDeleteAccountDialog() {
        // Arrange: Set up a test NavController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        // Act: Set the content with the NavigationBar
        composeTestRule.setContent {
            NavHost(
                navController = navController,
                startDestination = "navigationBar"
            ) {
                composable("navigationBar") { NavigationBar(navController = navController) }
            }
        }

        // Act: Open the settings dropdown menu and select "Delete account"
        composeTestRule.onNodeWithContentDescription("Settings Icon").performClick()
        composeTestRule.onNodeWithText("Delete account").performClick()

        // Assert: Check if the confirmation dialog is displayed
        composeTestRule.onNodeWithText("Are you sure you want to delete your account? This action cannot be undone.")
            .assertIsDisplayed()

        // Simulate clicking "Cancel" and ensure the dialog is dismissed
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.onNodeWithText("Are you sure you want to delete your account? This action cannot be undone.")
            .assertDoesNotExist()
    }
}
