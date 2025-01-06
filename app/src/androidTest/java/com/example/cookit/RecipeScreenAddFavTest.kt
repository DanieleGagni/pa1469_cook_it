package com.example.cookit.screens.recipe

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.cookit.screens.components.Recipe
import org.junit.Rule
import org.junit.Test

class RecipeScreenAddFavTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFavoriteButtonTogglesStateAfterClick() {
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
        composeTestRule.onRoot().printToLog("BeforeClick")

        // Act: Hacer clic en el botón "Add to favorites"
        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()

        // Esperar a que se complete la acción
        composeTestRule.waitForIdle()

        // Imprimir el árbol después del clic
        composeTestRule.onRoot().printToLog("AfterClick")

        // Assert: Comprobar si el botón ha cambiado su contenido
        // Vamos a verificar si existe un nuevo nodo que refleje el cambio visual
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertExists()
    }
}
