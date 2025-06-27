//package com.example.rollcall.authentication
//
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.imePadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Divider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.example.rollcall.R
//import com.example.rollcall.ui.theme.Lalezar
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.platform.LocalContext
//import com.example.rollcall.ui.theme.Laila
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//
//
//@Composable
//fun SignUpPage(navController: NavHostController,authViewModel: AuthViewModel){
//
//    val context = LocalContext.current
//
//    var username by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmpassword by remember { mutableStateOf("") }
//
//    val authState = authViewModel.authState.observeAsState()
//
////    LaunchedEffect(authState.value) {
////        when(authState.value){
////            is AuthState.Authenticated -> {
////                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
////                navController.navigate("classroompage")
////            }
////            is AuthState.Error-> Toast.makeText(context,
////                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
////            else -> Unit
////        }
////    }
//
//
//    val googleSignInOptions = remember {
//        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1014241138424-b69ibh57cdperg3ha2ag6t43ubm0fhek.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//
//    }
//    val googleSignInClient = remember {
//        GoogleSignIn.getClient(context,googleSignInOptions)
//    }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result->
//        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//        try {
//            val account = task.result
//            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//            Firebase.auth.signInWithCredential(credential)
//                .addOnCompleteListener { task->
//                    if(task.isSuccessful){
//                        Toast.makeText(context, "Google Sign-Up SuccessFull", Toast.LENGTH_SHORT).show()
//                        navController.navigate("classroompage"){
//                            popUpTo("signup"){ inclusive = true}
//                        }
//
//                    }else{
//                        Toast.makeText(context, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
//
//                    }
//
//                }
//        }catch (e: Exception){
//            Toast.makeText(context, "Google Sign-Up failed", Toast.LENGTH_SHORT).show()
//
//        }
//
//
//    }
//
//
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(colorResource(R.color.prem))
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(60.dp))
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                contentAlignment = Alignment.Center
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(120.dp)
//                        .background(Color.White, shape = CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.signupimage),
//                        contentDescription = "Signup Icon",
//                        modifier = Modifier.size(80.dp)
//                    )
//                }
//            }
//
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Orange Card for form content
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight(),
//                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
//                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.maya))
//            ) {
//                val scrollState = rememberScrollState()
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(top = 24.dp)
//                        .verticalScroll(scrollState)
//                        .imePadding()
//                ) {
//                    Text(
//                        text = "Create your account",
//                        color = Color.Black,
//                        fontSize = 20.sp,
//                        fontFamily = Lalezar,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    val fieldModifier = Modifier
//                        .fillMaxWidth(0.8f)
//                       .padding(vertical = 2.dp)
//
//
//                    OutlinedTextField(
//                        value = username,
//                        onValueChange = { username = it },
//                        placeholder = { Text("Full Name", fontFamily = Laila, color = Color.Black) },
//                        shape = RoundedCornerShape(50),
//                        modifier = fieldModifier,
//                        maxLines = 1,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            unfocusedContainerColor = Color.White,
//                            focusedContainerColor = Color.White,
//                            disabledContainerColor = Color.White,
//                            unfocusedTextColor = Color.Gray,
//                            focusedTextColor = Color.Gray,
//                            focusedBorderColor =  colorResource(R.color.prem),
//                            unfocusedBorderColor = Color.Gray
//                        )
//
//
//
//                    )
//
//                    OutlinedTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        placeholder = { Text("Email ID",fontFamily = Laila, color = Color.Black) },
//                        shape = RoundedCornerShape(50),
//                        modifier = fieldModifier,
//                        maxLines = 1,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            unfocusedContainerColor = Color.White,
//                            focusedContainerColor = Color.White,
//                            disabledContainerColor = Color.White,
//                            unfocusedTextColor = Color.Gray,
//                            focusedTextColor = Color.Gray,
//                            focusedBorderColor =  colorResource(R.color.prem),
//                            unfocusedBorderColor = Color.Gray
//                        )
//
//                    )
//
//                    OutlinedTextField(
//                        value = password,
//                        onValueChange = { password = it },
//                        placeholder = { Text("Password",fontFamily = Laila, color = Color.Black) },
//                        visualTransformation = PasswordVisualTransformation(),
//                        shape = RoundedCornerShape(50),
//                        modifier = fieldModifier,
//                        maxLines = 1,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            unfocusedContainerColor = Color.White,
//                            focusedContainerColor = Color.White,
//                            disabledContainerColor = Color.White,
//                            unfocusedTextColor = Color.Gray,
//                            focusedTextColor = Color.Gray,
//                            focusedBorderColor =  colorResource(R.color.prem),
//                            unfocusedBorderColor = Color.Gray
//                        )
//
//
//                    )
//
//                    OutlinedTextField(
//                        value = confirmpassword,
//                        onValueChange = { confirmpassword = it },
//                        placeholder = { Text("Confirm Password",fontFamily = Laila, color = Color.Black) },
//                        visualTransformation = PasswordVisualTransformation(),
//                        shape = RoundedCornerShape(50),
//                        modifier = fieldModifier,
//                        maxLines = 1,
//                        colors = OutlinedTextFieldDefaults.colors(
//                            unfocusedContainerColor = Color.White,
//                            focusedContainerColor = Color.White,
//                            disabledContainerColor = Color.White,
//                            unfocusedTextColor = Color.Gray,
//                            focusedTextColor = Color.Gray,
//                            focusedBorderColor =  colorResource(R.color.prem),
//                            unfocusedBorderColor = Color.Gray
//                        )
//
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    if (authState.value is AuthState.Loading) {
//                        CircularProgressIndicator(color = colorResource(R.color.prem))
//                    } else {
//                        Button(
//                            onClick = { authViewModel.signup(username, email, password) },
//                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
//                            shape = RoundedCornerShape(50),
//                            modifier = fieldModifier
//                        ) {
//                            Text("SignUp", color = Color.White, fontFamily = Laila)
//                        }
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    ) {
//                        Divider(Modifier.weight(1f), color = Color.Gray)
//                        Text("  or  ", color = Color.Gray,fontFamily = Laila)
//                        Divider(Modifier.weight(1f), color = Color.Gray)
//                    }
//
//                    Button(
//                        onClick = {
//                            val signInIntent = googleSignInClient.signInIntent
//                            launcher.launch(signInIntent)
//                                  },
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
//                        shape = RoundedCornerShape(50),
//                        modifier = fieldModifier
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.googleicon),
//                            contentDescription = null,
//                            tint = Color.Unspecified
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("SignUp with Google", color = Color.Black,fontFamily = Laila)
//                    }
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Row {
//                        Text("Already have an account? ",fontFamily = Laila)
//                        Text(
//                            text = "Login",
//                            fontFamily = Laila,
//                            color = colorResource(R.color.prem),
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.clickable {
//                                navController.navigate("login")
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


package com.example.rollcall.authentication

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
import com.example.rollcall.R
import com.example.rollcall.ui.theme.Laila
import com.example.rollcall.ui.theme.Lalezar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun SignUpPage(navController: NavHostController, authViewModel: AuthViewModel) {

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()

    // ✅ Restored authState listener for navigation on signup
    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authenticated -> {
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate("classroompage") {
                    popUpTo("signup") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    // ✅ Google Sign-In setup
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1014241138424-b69ibh57cdperg3ha2ag6t43ubm0fhek.apps.googleusercontent.com") // ✅ Your Web Client ID
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
                // ✅ Using ViewModel method to update auth state
                authViewModel.signInWithGoogle(idToken)
            } else {
                Toast.makeText(context, "Google Sign-Up failed: No token", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Google Sign-Up failed", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ UI Layout
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

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.signupimage),
                        contentDescription = "Signup Icon",
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
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("Full Name", fontFamily = Laila, color = Color.Black) },
                        shape = RoundedCornerShape(50),
                        modifier = fieldModifier,
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
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
                            unfocusedTextColor = Color.Gray,
                            focusedTextColor = Color.Gray,
                            focusedBorderColor = colorResource(R.color.prem),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = confirmpassword,
                        onValueChange = { confirmpassword = it },
                        placeholder = { Text("Confirm Password", fontFamily = Laila, color = Color.Black) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(50),
                        modifier = fieldModifier,
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedTextColor = Color.Gray,
                            focusedTextColor = Color.Gray,
                            focusedBorderColor = colorResource(R.color.prem),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (authState.value is AuthState.Loading) {
                        CircularProgressIndicator(color = colorResource(R.color.prem))
                    } else {
                        Button(
                            onClick = {
                                if (password == confirmpassword) {
                                    authViewModel.signup(username, email, password)
                                } else {
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
                            shape = RoundedCornerShape(50),
                            modifier = fieldModifier
                        ) {
                            Text("SignUp", color = Color.White, fontFamily = Laila)
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
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(50),
                        modifier = fieldModifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.googleicon),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SignUp with Google", color = Color.Black, fontFamily = Laila)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Text("Already have an account? ", fontFamily = Laila)
                        Text(
                            text = "Login",
                            fontFamily = Laila,
                            color = colorResource(R.color.prem),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("login")
                            }
                        )
                    }
                }
            }
        }
    }
}
