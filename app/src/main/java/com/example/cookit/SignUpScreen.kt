package com.example.cookit


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun SignUpScreen(navController: NavHostController) {

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordError by remember { mutableStateOf("") }

    fun validatePassword(username : String, password : String) {
        when {
            password.length < 8 -> passwordError = "Password must be at least 8 characters"
            password == username -> passwordError = "Password cannot be username"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF5DD))
            )
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                //verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(100.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Your image description",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                Text("Just a few quick things to get started")

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    //label = { Text("Enter Username") },
                    placeholder = { Text("john.doe") },
                    modifier = Modifier
                        .width(400.dp),
                    shape = RoundedCornerShape(25.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    //label = { Text("Enter Password") },
                    placeholder = { Text("password") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = {
                        //navController.navigate("home")
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                ) {
                    Text("Create account")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Already have an account?")

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = {
                        navController.navigate("logIn")
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                ) {
                    Text("Log in")
                }
            }
        }
    )
}
