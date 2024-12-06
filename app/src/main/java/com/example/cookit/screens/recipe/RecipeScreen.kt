package com.example.cookit.screens.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cookit.R
import com.example.cookit.screens.components.NavigationBar


@Composable
fun FavoriteButton() {

    //GET FAVOURITE STATE FROM VIEWMODEL AS A PARAMETER
    var isFavorited by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            isFavorited = !isFavorited
        },
        modifier = Modifier.size(60.dp)
    ) {
        Icon(
            painter = if (isFavorited) painterResource(id = R.drawable.ic_favorite) else painterResource(id = R.drawable.ic_favourite_outlined),
            contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
            tint = Color.Unspecified,
            modifier = Modifier.size(60.dp)
        )
    }
}


@Composable
fun RecipeScreen(navController: NavHostController) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(navController)
        },
        content = { innerPadding ->

            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {

                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        //.padding(top = 30.dp)
                    //horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //---------RECIPE NAME
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFF58D1E),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                //CHANGE TO GET NAME FROM VIEW MODEL
                                append("RECIPE NAME ")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 30.sp),
                        color = Color.Black,
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.75f)
                            .padding(top = 16.dp)
                            .verticalScroll(scrollState)
                            .border(
                                width = 2.dp,                // Border thickness
                                color = Color.Green,         // Border color
                                shape = RoundedCornerShape(8.dp) // Optional: Rounded corners
                            ),
                    ) {

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("INGREDIENTS")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                            color = Color.Black,
                        )

                        //GET INGREDIENTS FROM VIEWMODEL
                        for (i in 1..10) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .padding(start = 10.dp)
                                    .padding(bottom = 10.dp)
                            ) {
                                Text("Ingredient $i")
                            }
                        }

                        //SPACER TO CHECK IF SCROLLABLE WORKS
                        //SPOILER ALERT IT DOESN'T WORK
                        Spacer(modifier = Modifier.height(250.dp))

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("STEPS")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                            color = Color.Black,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .padding(start = 10.dp)
                                .padding(end = 10.dp)
                                .border(
                                    width = 2.dp,                // Border thickness
                                    color = Color.Blue,         // Border color
                                    shape = RoundedCornerShape(8.dp) // Optional: Rounded corners
                                ),
                        ) {
                            Text(
                                text = "RECIPE STEPS RECIPE STEPS RECIPE STEPS RECIPE STEPS RECIPE STEPS RECIPE STEPS",
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        //PASS FAVOURITE ATRIBUTE AS PARAMETER
                        FavoriteButton()
                        IconButton(
                                onClick = {
                                    //navController.navigate("shoppingList")
                                },
                                modifier = Modifier.size(60.dp)
                                ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_shopping_cart),
                                contentDescription = "Shopping List Icon",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                    }
                }
            }
        }
    )
}
