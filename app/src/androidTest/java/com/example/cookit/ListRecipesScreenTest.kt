package com.example.cookit

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookit.screens.components.Recipe
import com.example.cookit.screens.listRecipes.ListRecipesScreen
import com.example.cookit.screens.listRecipes.ListRecipesViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListRecipesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testListRecipesScreenDisplaysContent() {
        val mockNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        val viewModel = ListRecipesViewModel().apply {
            setMockRecipes(
                listOf(
                    Recipe(
                        id = "1",
                        title = "Vegetarian Pizza",
                        type = "vegetarian",
                        estimatedTime = 30
                    ),
                    Recipe(
                        id = "2",
                        title = "Quick Pasta",
                        type = "quick",
                        estimatedTime = 15
                    )
                )
            )
        }

        composeTestRule.setContent {
            ListRecipesScreen(
                navController = mockNavController,
                //recipeIds = listOf("1", "2"),
                recipeIds = emptyList(),
                isFavorites = false,
                type = "all",
                viewModel = viewModel
            )
        }

        //composeTestRule.waitForIdle()

        composeTestRule.onRoot().printToLog("ListRecipesScreenTest")
        composeTestRule.onNodeWithText("Your results").assertIsDisplayed()

        composeTestRule.onNodeWithText("Vegetarian Pizza").assertExists()
        composeTestRule.onNodeWithText("Vegetarian Pizza").assertIsDisplayed()


    }


}