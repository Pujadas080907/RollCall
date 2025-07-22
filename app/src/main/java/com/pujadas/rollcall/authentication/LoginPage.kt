
package com.pujadas.rollcall.authentication

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pujadas.rollcall.ui.theme.Laila
import com.pujadas.rollcall.ui.theme.Lalezar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.pujadas.rollcall.R

@Composable
fun LoginPage(navController: NavHostController, authViewModel: AuthViewModel) {
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    var isGoogleSignInLoading by remember { mutableStateOf(false) }


    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authenticated -> {
                isGoogleSignInLoading = false
                Toast.makeText(context, "Sign In successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate("classroompage") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                isGoogleSignInLoading = false
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }


    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1014241138424-b69ibh57cdperg3ha2ag6t43ubm0fhek.apps.googleusercontent.com") // ✅ Your correct Web client ID
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val idToken = account.idToken
            if (idToken != null) {
                authViewModel.signInWithGoogle(idToken)
            } else {
                Toast.makeText(context, "Google Sign-In failed: No token", Toast.LENGTH_SHORT).show()
                isGoogleSignInLoading = false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            isGoogleSignInLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.prem))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.loginimage),
                        contentDescription = "Login Icon",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.maya))
            ) {
                val scrollState = rememberScrollState()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                        .verticalScroll(scrollState)
                        .imePadding()
                ) {
                    Text(
                        text = "Create your account",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontFamily = Lalezar,
                        fontWeight = FontWeight.Bold
                    )

                    val fieldModifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 2.dp)

                    OutlinedTextField(
                        value = fullname,
                        onValueChange = { fullname = it },
                        placeholder = { Text("Full Name", fontFamily = Laila, color = Color.Black) },
                        shape = RoundedCornerShape(50),
                        modifier = fieldModifier,
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            unfocusedTextColor = Color.Gray,
                            focusedTextColor = Color.Gray,
                            focusedBorderColor = colorResource(R.color.prem),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email ID", fontFamily = Laila, color = Color.Black) },
                        shape = RoundedCornerShape(50),
                        modifier = fieldModifier,
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            unfocusedTextColor = Color.Gray,
                            focusedTextColor = Color.Gray,
                            focusedBorderColor = colorResource(R.color.prem),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password", fontFamily = Laila, color = Color.Black) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(50),
                        modifier = fieldModifier,
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            unfocusedTextColor = Color.Gray,
                            focusedTextColor = Color.Gray,
                            focusedBorderColor = colorResource(R.color.prem),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (authState.value is AuthState.Loading && !isGoogleSignInLoading) {
                        CircularProgressIndicator(color = colorResource(R.color.prem))
                    } else {
                        Button(
                            onClick = { authViewModel.login(email, password) },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
                            shape = RoundedCornerShape(50),
                            modifier = fieldModifier
                        ) {
                            Text("Login", color = Color.White, fontFamily = Laila)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Divider(Modifier.weight(1f), color = Color.Gray)
                        Text("  or  ", color = Color.Gray, fontFamily = Laila)
                        Divider(Modifier.weight(1f), color = Color.Gray)
                    }

                    Button(
                        onClick = {
                            isGoogleSignInLoading = true
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(50),
                        modifier = fieldModifier
                    ) {

                        if (isGoogleSignInLoading) {
                            CircularProgressIndicator(
                                color = colorResource(R.color.prem),
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }else{
                            Icon(
                                painter = painterResource(id = R.drawable.googleicon),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Login with Google", color = Color.Black, fontFamily = Laila)
                        }

                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Text("Don't have an account? ", fontFamily = Laila)
                        Text(
                            text = "SignUp",
                            fontFamily = Laila,
                            color = colorResource(R.color.prem),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("signup")
                            }
                        )
                    }
                }
            }
        }
    }
}
