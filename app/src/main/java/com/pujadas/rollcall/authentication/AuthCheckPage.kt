
package com.pujadas.rollcall.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pujadas.rollcall.ui.theme.Laila
import kotlinx.coroutines.delay
import com.pujadas.rollcall.R
@Composable
fun AuthCheckPage(navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState().value

    LaunchedEffect(authState) {
        delay(1200L)

        when (authState) {
            is AuthState.Authenticated -> {
                delay(500)
                navController.navigate("classroompage") {
                    popUpTo("authcheck") { inclusive = true }
                }
            }
            is AuthState.Unauthenticated, null -> {
                delay(500)
                navController.navigate("login") {
                    popUpTo("authcheck") { inclusive = true }
                }
            }
            else -> {}
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.splashicon),
                contentDescription = "Loading icon",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))


            CircularProgressIndicator(color = colorResource(R.color.maya))
            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "Please wait... ⏳",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Laila
            )
        }
    }
}
