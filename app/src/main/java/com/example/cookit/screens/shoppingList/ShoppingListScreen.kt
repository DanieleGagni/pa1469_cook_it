package com.example.cookit.screens.shoppingList

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.NavigationBar
import com.example.cookit.ui.theme.darkOrange


data class ShoppingItem(
    val entry: String,
    val done: Boolean
)

class ShoppingListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private val _shoppingList = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val shoppingList: StateFlow<List<ShoppingItem>> = _shoppingList

    fun fetchShoppingList(navController: NavHostController) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
        } else {
            db.collection("shoppingLists")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        createEmptyShoppingList(userId)
                    } else {
                        val rawItems = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                        val items = rawItems.map {
                            ShoppingItem(
                                entry = it["entry"] as String,
                                done = it["done"] as Boolean
                            )
                        }
                        _shoppingList.value = items
                    }
                }
                .addOnFailureListener { e ->
                    println("Error fetching shopping list: $e")
                }
        }
    }

    private fun createEmptyShoppingList(userId: String) {
        val newShoppingList = hashMapOf(
            "items" to emptyList<Map<String, Any>>() // No items initially
        )

        db.collection("shoppingLists")
            .document(userId)
            .set(newShoppingList)
            .addOnSuccessListener {
                println("Created empty shopping list for user: $userId")
                _shoppingList.value = emptyList() // Reflect empty list in UI
            }
            .addOnFailureListener { e ->
                println("Error creating empty shopping list: $e")
            }
    }

    fun addShoppingItem(navController: NavHostController, itemText: String) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        val newItem = ShoppingItem(entry = itemText, done = false)
        _shoppingList.value += newItem

        db.collection("shoppingLists")
            .document(userId)
            .update(
                "items",
                _shoppingList.value.map { mapOf("entry" to it.entry, "done" to it.done) }
            )
            .addOnFailureListener { e ->
                println("Error updating Firestore: $e")
            }
    }

    fun toggleItemStatus(navController: NavHostController, index: Int) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        val updatedList = _shoppingList.value.toMutableList().apply {
            this[index] = this[index].copy(done = !this[index].done)
        }
        _shoppingList.value = updatedList

        db.collection("shoppingLists")
            .document(userId)
            .update(
                "items",
                updatedList.map { mapOf("entry" to it.entry, "done" to it.done) }
            )
            .addOnFailureListener { e ->
                println("Error updating item status in Firestore: $e")
            }
    }

    fun removeShoppingItem(navController: NavHostController, index: Int) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        val updatedList = _shoppingList.value.toMutableList().apply {
            removeAt(index)
        }
        _shoppingList.value = updatedList

        db.collection("shoppingLists")
            .document(userId)
            .update(
                "items",
                updatedList.map { mapOf("entry" to it.entry, "done" to it.done) }
            )
            .addOnFailureListener { e ->
                println("Error removing item from Firestore: $e")
            }
    }

    fun removeAllItems(navController: NavHostController) {
        val userId = currentUserId
        if (userId == null) {
            println("User not logged in.")
            navController.navigate("login")
            return
        }

        _shoppingList.value = emptyList()

        db.collection("shoppingLists")
            .document(userId)
            .update("items", emptyList<Map<String, Any>>())
            .addOnFailureListener { e ->
                println("Error clearing shopping list in Firestore: $e")
            }
    }
}



@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    viewModel: ShoppingListViewModel = viewModel()
) {
    val shoppingList by viewModel.shoppingList.collectAsState()
    var newIngredient by remember { mutableStateOf(TextFieldValue("")) }

    // State to manage the visibility of the warning dialog
    var showWarningDialog by remember { mutableStateOf(false) }

    // Trigger data fetch when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchShoppingList(navController)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(navController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = darkOrange)) {
                                append("SHOPPING ")
                            }
                            append("LIST")
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 25.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    if (shoppingList.isEmpty()) {
                        Text(
                            text = "Your shopping list is empty. Start adding items!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        shoppingList.forEachIndexed { index, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = item.done,
                                    onCheckedChange = {
                                        viewModel.toggleItemStatus(navController, index)
                                    }
                                )
                                Text(
                                    text = item.entry,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.removeShoppingItem(navController, index) }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.remove),
                                        contentDescription = "Remove Icon",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Unspecified
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = newIngredient,
                        onValueChange = { newIngredient = it },
                        placeholder = { Text("Add ingredients") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 6.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(25.dp),
                    )

                    IconButton(
                        onClick = {
                            viewModel.addShoppingItem(navController, newIngredient.text)
                            newIngredient = TextFieldValue("") // Clear input
                        },
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_black),
                            contentDescription = "Add Icon",
                            tint = Color.Unspecified
                        )
                    }
                }

                Button(
                    onClick = { showWarningDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkOrange)
                ) {
                    Text(
                        text = "REMOVE ALL",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Confirmation Dialog
            if (showWarningDialog) {
                AlertDialog(
                    onDismissRequest = { showWarningDialog = false },
                    title = {
                        Text(text = "Clear Shopping List", style = MaterialTheme.typography.titleMedium)
                    },
                    text = {
                        Text("Are you sure you want to remove all items from your shopping list? This action cannot be undone.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.removeAllItems(navController)
                                showWarningDialog = false
                            }
                        ) {
                            Text("Confirm", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showWarningDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    )
}
