package com.example.cookit.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    // Handle the login logic
    fun handleSearch() {
        //Navigate to the search screen
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController) // Barra de navegación inferior
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally // Centrar el contenido
            ) {
                Spacer(modifier = Modifier.height(24.dp)) // Más espacio superior

                //Saludo en columna
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        //TEXT SHOULD CALL TO VIEWMODEL TO GET THE USER'S NAME
                        text = "Hello Maria!",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre saludo y eslogan

                // Eslogan separado
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) { // Color naranja para "Cook"
                            append("Cook")
                        }
                        append(" with ease, connect\n\nthrough ")
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) { // Color naranja para "Cook"
                            append("flavours")
                        }
                        append(".")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 25.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp)) // Espacio entre eslogan y búsqueda

                //Search button
                Button(
                    onClick = { handleSearch() },
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)) // Set the background color
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .size(40.dp),
                        tint = Color.Unspecified
                    )
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
                                val imageRes = categoryImages[category] ?: R.drawable.ic_category_add
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

// Barra inferior de navegación
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 4.dp), // Reducir padding vertical
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { /* Navegar a Home */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "Home Icon",
                tint = Color.Unspecified
            )
        }
        IconButton(onClick = { /* Navegar a Shopping List */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stats),
                contentDescription = "Shopping List Icon",
                tint = Color.Unspecified
            )
        }
        IconButton(
            onClick = {
                navController.navigate("createRecipe")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add Icon",
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
        }
        IconButton(onClick = { /* Navegar a Favoritos */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Favorite Icon",
                tint = Color.Unspecified
            )
        }
        IconButton(onClick = { /* Navegar a Settings */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings Icon",
                tint = Color.Unspecified
            )
        }
    }
}