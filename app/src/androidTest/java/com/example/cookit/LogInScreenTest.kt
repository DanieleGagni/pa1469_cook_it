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
import com.example.cookit.screens.shoppingList.ShoppingListScreen
import com.example.cookit.screens.shoppingList.ShoppingListViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import io.mockk.*

@RunWith(AndroidJUnit4::class)
class LogInScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /*
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

 */
    /*
        @Test
        fun testLoginNavigationToHome() {

            // Create a mock FirebaseAuth instance using MockK
            val mockAuth = mockk<FirebaseAuth>()
            // Create a mock AuthResult
            val mockAuthResult = mockk<AuthResult>()

            // Mock the behavior of signInWithEmailAndPassword
            every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockk {
                every { isSuccessful } returns true  // Simulate successful login
                every { result } returns mockAuthResult  // Simulate returning the mock AuthResult
            }

            // Create a mock NavController
            val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            // Create an instance of the LoginViewModel using the mocked FirebaseAuth
            val viewModel = LoginViewModel() // Using actual constructor

            // Set the username and password in the ViewModel
            viewModel.onUsernameChange("test@example.com")
            viewModel.onPasswordChange("password123")

            // Set up the NavHost with a "login" screen and a "home" screen
            composeTestRule.setContent {
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        // Trigger the login process in the ViewModel
                        viewModel.login(navController)
                    }
                    composable("home") {
                        // Simulate the home screen (after successful login)
                        ShoppingListScreen(
                            navController = navController,
                            viewModel = mockk() // Mock ShoppingListViewModel
                        )
                    }
                }
            }

            // Verify that navigation happens after login
            composeTestRule.waitForIdle()

            // Assert the navController navigates to the "home" screen
            assert(navController.currentBackStackEntry?.destination?.route == "home")

            // Optionally, check for UI elements on the home screen
            composeTestRule.onNodeWithText("Your shopping list is empty. Start adding items!")
                .assertIsDisplayed()

        }

     */
}