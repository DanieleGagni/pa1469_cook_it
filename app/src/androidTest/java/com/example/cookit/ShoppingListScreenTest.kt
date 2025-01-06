

/*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookit.screens.shoppingList.ShoppingItem
import com.example.cookit.screens.shoppingList.ShoppingListScreen
import com.example.cookit.screens.shoppingList.ShoppingListViewModel
import com.example.cookit.ui.theme.CookItTheme
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*

@RunWith(AndroidJUnit4::class)
class ShoppingListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Create a mock ViewModel with mockk
    private val viewModel: ShoppingListViewModel = mockk(relaxed = true)

    // Define a Composable to inject your ShoppingListScreen
    @Composable
    fun TestScreen(viewModel: ShoppingListViewModel) {
        ShoppingListScreen(
            navController = rememberNavController(),
            viewModel = viewModel
        )
    }

    /*
    @Test
    fun testShoppingListScreen_emptyList() {

        // Mock a MutableStateFlow with an empty list
        val mockShoppingList = MutableStateFlow<List<ShoppingItem>>(emptyList())
        every { viewModel.shoppingList } returns mockShoppingList

        composeTestRule.setContent {
            CookItTheme {
                TestScreen(viewModel)
            }
        }

        // Check if "Your shopping list is empty. Start adding items!" text is displayed
        composeTestRule.onNodeWithText("Your shopping list is empty. Start adding items!")
            .assertIsDisplayed()
    }
     */

    @Test
    fun testEmptyStateDisplay() {

        val viewModel = ShoppingListViewModel()

        // Arrange: Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.setContent {
            ShoppingListScreen(
                navController = rememberNavController(),
                viewModel = viewModel
            )
        }
        // Check if "Your shopping list is empty. Start adding items!" text is displayed
        composeTestRule.onNodeWithText("Your shopping list is empty. Start adding items!")
            .assertIsDisplayed()
    }

    /*
    @Test
    fun testAddShoppingItem() {
        // Create a list with a new item
        val newItem = "Milk"
        val updatedShoppingList = listOf(ShoppingItem(entry = newItem, done = false))

        // Mock a MutableStateFlow with the updated shopping list
        val mockShoppingList = MutableStateFlow(updatedShoppingList)
        every { viewModel.shoppingList } returns mockShoppingList

        composeTestRule.setContent {
            CookItTheme {
                TestScreen(viewModel)
            }
        }

        // Type the new item in the TextField
        composeTestRule.onNodeWithText("Add ingredients").performTextInput(newItem)

        // Click on the Add button (icon)
        composeTestRule.onNodeWithContentDescription("Add Icon").performClick()

        // Assert the new item is added to the list
        composeTestRule.onNodeWithText(newItem).assertIsDisplayed()
    }

    @Test
    fun testToggleItemStatus() {
        // Test toggling an item's status
        val itemText = "Eggs"
        val item = ShoppingItem(entry = itemText, done = false)

        // Mock a MutableStateFlow with the list containing the item
        val mockShoppingList = MutableStateFlow<List<ShoppingItem>>(listOf(item))
        every { viewModel.shoppingList } returns mockShoppingList

        composeTestRule.setContent {
            CookItTheme {
                TestScreen(viewModel)
            }
        }

        // Ensure the item is initially unchecked
        composeTestRule.onNodeWithText(itemText).assertIsDisplayed()
        composeTestRule.onNodeWithText(itemText).assertIsNotSelected()

        // Toggle the item's checkbox
        composeTestRule.onNodeWithText(itemText).performClick()

        // Check if the item is now marked as done
        composeTestRule.onNodeWithText(itemText).assertIsSelected()
    }

    @Test
    fun testRemoveShoppingItem() {
        // Test removing a shopping item
        val itemText = "Bread"
        val item = ShoppingItem(entry = itemText, done = false)

        // Mock a MutableStateFlow with the list containing the item
        val mockShoppingList = MutableStateFlow<List<ShoppingItem>>(listOf(item))
        every { viewModel.shoppingList } returns mockShoppingList

        composeTestRule.setContent {
            CookItTheme {
                TestScreen(viewModel)
            }
        }

        // Ensure the item is displayed
        composeTestRule.onNodeWithText(itemText).assertIsDisplayed()

        // Click the remove button
        composeTestRule.onNodeWithContentDescription("Remove Icon").performClick()

        // Ensure the item is no longer displayed
        composeTestRule.onNodeWithText(itemText).assertDoesNotExist()
    }

    @Test
    fun testRemoveAllItems() {
        // Test removing all items from the list
        val item1 = ShoppingItem(entry = "Milk", done = false)
        val item2 = ShoppingItem(entry = "Eggs", done = true)

        // Mock a MutableStateFlow with the list containing two items
        val mockShoppingList = MutableStateFlow<List<ShoppingItem>>(listOf(item1, item2))
        every { viewModel.shoppingList } returns mockShoppingList

        composeTestRule.setContent {
            CookItTheme {
                TestScreen(viewModel)
            }
        }

        // Ensure both items are displayed
        composeTestRule.onNodeWithText(item1.entry).assertIsDisplayed()
        composeTestRule.onNodeWithText(item2.entry).assertIsDisplayed()

        // Click the "REMOVE ALL" button
        composeTestRule.onNodeWithText("REMOVE ALL").performClick()

        // Ensure both items are no longer displayed
        composeTestRule.onNodeWithText(item1.entry).assertDoesNotExist()
        composeTestRule.onNodeWithText(item2.entry).assertDoesNotExist()
    }
     */
}
 */




import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookit.screens.shoppingList.ShoppingListScreen
import com.example.cookit.screens.shoppingList.ShoppingListViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class ShoppingListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Create a mock ViewModel with Mockito
    /*
    private val viewModel: ShoppingListViewModel = mock(ShoppingListViewModel::class.java)

    // Define a Composable to inject your ShoppingListScreen
    @Composable
    fun TestScreen(viewModel: ShoppingListViewModel) {
        ShoppingListScreen(
            navController = rememberNavController(),
            viewModel = viewModel
        )
    }

     */

    @Test
    fun testEmptyStateDisplay() {

        val viewModel: ShoppingListViewModel = mock(ShoppingListViewModel::class.java)

        // Arrange: Create a TestNavHostController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())

        composeTestRule.setContent {
            // Define the NavHost for the test to directly navigate to the ShoppingListScreen
            NavHost(navController = navController, startDestination = "shoppingList") {
                composable("shoppingList") {
                    ShoppingListScreen(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Check if "Your shopping list is empty. Start adding items!" text is displayed
        composeTestRule.onNodeWithText("Your shopping list is empty. Start adding items!")
            .assertIsDisplayed()
    }

    // Other test cases can be adapted similarly...
}