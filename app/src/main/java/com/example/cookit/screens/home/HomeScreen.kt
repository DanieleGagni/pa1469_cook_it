package com.example.cookit.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cookit.R
import com.google.firebase.auth.FirebaseAuth
import com.example.cookit.screens.components.NavigationBar


@Composable
fun HomeScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userName = currentUser?.displayName ?: "User" // Fallback to "User" if name is not set

    // Handle the login logic
    fun handleSearch() {
        // Trigger modal for search
    }

    var showSearchModal by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationBar(navController) }, // Bottom navigation bar
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally // Center the content
            ) {
                Spacer(modifier = Modifier.height(24.dp)) // Top space

                Text(
                    text = "Hello $userName!",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre saludo y eslogan

                // Eslogan separado
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFFF58D1E))) { // Color naranja para "Cook"
                            append("Cook")
                        }
                        append(" with ease, connect\n\nthrough ")
                        withStyle(style = SpanStyle(color = Color(0xFFF58D1E))) { // Color naranja para "Cook"
                            append("flavours")
                        }
                        append(".")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showSearchModal = true },
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search Icon",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Unspecified
                    )
                }

                // Display search modal when clicked
                if (showSearchModal) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)) // Overlay background
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Search Layout", // This can be your search-related UI
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                            )

                            //--------------------------Add search components (like text input, filters, etc.)

                            Button(onClick = { showSearchModal = false }) {
                                Text("Close")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre búsqueda y categorías

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .weight(1f) // Asegurar que ocupe el espacio restante
                ) {
                    val categoryImages = mapOf(
                        "VEGETARIAN" to R.drawable.ic_category_vegetarian,
                        "QUICK" to R.drawable.ic_category_quick,
                        "COMPLEX" to R.drawable.ic_category_complex,
                        "ALL" to R.drawable.ic_category_all
                    )

                    items(listOf("VEGETARIAN", "QUICK", "COMPLEX", "ALL")) { category ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            ) {
                                val imageRes =
                                    categoryImages[category] ?: R.drawable.ic_category_add
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = category,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = category,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

