package com.example.cookit.screens.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.cookit.ui.theme.CookItTheme
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHomeScreenDisplaysGreetingAndButtons() {
        // Configurar NavController de prueba
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Renderizar HomeScreen
        composeTestRule.setContent {
            CookItTheme {
                HomeScreen(navController = mockNavController)
            }
        }

        // Verificar saludo
        composeTestRule.onNodeWithText("Hello User!").assertExists()

        // Verificar eslogan
        composeTestRule.onNodeWithText("Cook with ease, connect\n\nthrough flavours.").assertExists()

        // Verificar botones
        composeTestRule.onNodeWithText("Search by Name").assertExists()
        composeTestRule.onNodeWithText("Search by Ingredients").assertExists()
    }

    @Test
    fun testSearchByNameButtonNavigatesCorrectly() {
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        composeTestRule.setContent {
            CookItTheme {
                HomeScreen(navController = mockNavController)
            }
        }

        // Hacer clic en el botón "Search by Name"
        composeTestRule.onNodeWithText("Search by Name").performClick()

        // Verificar navegación
        assertEquals("searchRecipes", mockNavController.currentDestination?.route)
    }

    @Test
    fun testSearchByIngredientsButtonNavigatesCorrectly() {
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        composeTestRule.setContent {
            CookItTheme {
                HomeScreen(navController = mockNavController)
            }
        }

        // Hacer clic en el botón "Search by Ingredients"
        composeTestRule.onNodeWithText("Search by Ingredients").performClick()

        // Verificar navegación
        assertEquals("filterIngredients", mockNavController.currentDestination?.route)
    }

    @Test
    fun testCategoryNavigation() {
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        composeTestRule.setContent {
            CookItTheme {
                HomeScreen(navController = mockNavController)
            }
        }

        // Probar cada categoría
        val categories = mapOf(
            "VEGETARIAN" to "listRecipes/[]?type=VEGETARIAN",
            "QUICK" to "listRecipes/[]?type=QUICK",
            "COMPLEX" to "listRecipes/[]?type=COMPLEX",
            "ALL" to "listRecipes/[]?type=ALL"
        )

        categories.forEach { (category, route) ->
            composeTestRule.onNodeWithText(category).performClick()
            assertEquals(route, mockNavController.currentDestination?.route)
        }
    }
}
