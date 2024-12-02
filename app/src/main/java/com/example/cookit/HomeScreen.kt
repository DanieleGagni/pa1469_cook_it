package com.example.cookit

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar() // Barra de navegación inferior
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Saludo ta gure "esaldie"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user_avatar), // sesiñue hasten daben pertsonan argazkixe
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            //TEXT SHOULD CHANGE SO TAHT IT TAKES THE USER NAME
                            text = "Hello Maria!",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFFFFA500))) { // Color naranja para "Cook"
                                    append("Cook")
                                }
                                append(" with ease, connect\nthrough flavours.")
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp), // Tamaño más grande
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cuadro de búsqueda
                OutlinedTextField(
                    value = "",
                    onValueChange = { /* Lógica de búsqueda */ },
                    placeholder = { Text("What would you like to eat?") },
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Menos ancha
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search), // Ícono de búsqueda
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(20.dp) // Tamaño reducido de la lupa
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Categorías
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grid de categorías
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
                                // Imagen específica para cada categoría
                                val imageRes = categoryImages[category] ?: R.drawable.ic_category_add //Hauxe da ez badau aurkitzen konkretuki bat, orokorrie
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
fun BottomNavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { /* Navegar a Home */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home), // Ícono de Home
                contentDescription = "Home Icon"
            )
        }
        IconButton(onClick = { /* Navegar a Estadísticas */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stats), // Ícono de estadísticas
                contentDescription = "Stats Icon"
            )
        }
        IconButton(onClick = { /* Agregar nueva receta */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add), // Ícono de agregar
                contentDescription = "Add Icon",
                modifier = Modifier.size(48.dp)
            )
        }
        IconButton(onClick = { /* Navegar a Favoritos */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite), // Ícono de favoritos
                contentDescription = "Favorite Icon"
            )
        }
        IconButton(onClick = { /* Navegar a Perfil */ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile), // Ícono de perfil
                contentDescription = "Profile Icon"
            )
        }
    }
}
