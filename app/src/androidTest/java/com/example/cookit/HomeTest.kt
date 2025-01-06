package com.example.cookit.screens.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.cookit.ui.theme.CookItTheme
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cookit.screens.searchRecipe.FilterIngredientsScreen
import com.example.cookit.screens.searchRecipe.SearchRecipeScreen
import com.example.cookit.screens.searchRecipe.SearchRecipeViewModel

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHomeScreenDisplaysGreetingAndButtons() {
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Render HomeScreen
        composeTestRule.setContent {
            CookItTheme {
                HomeScreen(navController = mockNavController)
            }
        }

        composeTestRule.onNodeWithText("Hello User!").assertExists()

        composeTestRule.onNodeWithText("Cook with ease, connect\n\nthrough flavours.").assertExists()

        composeTestRule.onNodeWithText("Search by Name").assertExists()
        composeTestRule.onNodeWithText("Search by Ingredients").assertExists()
    }

    @Test
    fun testSearchByNameButtonNavigatesCorrectly() {
        // Create the TestNavHostController
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Add the ComposeNavigator to the NavController
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.setContent {
            CookItTheme {
                val searchRecipeViewModel: SearchRecipeViewModel = viewModel()

                NavHost(
                    navController = mockNavController,
                    startDestination = "home"
                ) {
                    composable("home") { HomeScreen(navController = mockNavController) }
                    composable("searchRecipes") {
                        SearchRecipeScreen(mockNavController, searchRecipeViewModel)
                    }
                }
            }
        }

        // Click on the "Search by Name" button
        composeTestRule.onNodeWithText("Search by Name").performClick()

        // Verify navigation
        assertEquals("searchRecipes", mockNavController.currentDestination?.route)
    }

    @Test
    fun testSearchByIngredientsButtonNavigatesCorrectly() {
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Add the ComposeNavigator to the NavController
        mockNavController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.setContent {
            CookItTheme {
                val searchRecipeViewModel: SearchRecipeViewModel = viewModel()

                NavHost(
                    navController = mockNavController,
                    startDestination = "home"
                ) {
                    composable("home") { HomeScreen(navController = mockNavController) }
                    composable("filterIngredients") {
                        FilterIngredientsScreen(mockNavController, searchRecipeViewModel)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Search by Ingredients").performClick()

        assertEquals("filterIngredients", mockNavController.currentDestination?.route)
    }
}
