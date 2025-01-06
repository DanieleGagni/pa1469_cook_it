package com.example.cookit.screens.recipe

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.cookit.screens.components.Recipe
import org.junit.Rule
import org.junit.Test

class RecipeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAddIngredientsToShoppingList() {
        // Crear un NavHostController de prueba
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        // Configurar el Composable para el test
        composeTestRule.setContent {
            RecipeScreen(
                navController = mockNavController,
                recipe = Recipe(
                    id = "1",
                    title = "Test Recipe",
                    ingredients = listOf("Ingredient 1", "Ingredient 2"),
                    steps = listOf("Step 1", "Step 2"),
                    type = "vegetarian",
                    createdBy = "user1"
                )
            )
        }

        // Imprimir el árbol inicial
        composeTestRule.onRoot().printToLog("BeforeShoppingListClick")

        // Act: Hacer clic en el botón "Add to Shopping List"
        composeTestRule.onNodeWithContentDescription("Add to Shopping List").performClick()

        // Esperar a que la acción se complete
        composeTestRule.waitForIdle()

        // Imprimir el árbol después del clic
        composeTestRule.onRoot().printToLog("AfterShoppingListClick")

        // Assert: Verificar si los ingredientes aparecen en la lista de compras
        composeTestRule.onNodeWithText("Ingredient 1").assertExists()
        composeTestRule.onNodeWithText("Ingredient 2").assertExists()
    }
}
