//package com.example.rollcall.classroom
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.example.rollcall.R
//
//@Composable
//fun BottomNavigationBar(navController: NavController) {
//    val items = listOf(
//        "classroompage" to R.drawable.homeicon,
//        "monthviewpage" to R.drawable.monthviewicon
//    )
//
//    val currentRoute = navController.currentBackStackEntry?.destination?.route
//
//    BottomAppBar(
//        modifier = Modifier
//            .height(60.dp)
//            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
//            .navigationBarsPadding(),
//        containerColor = colorResource(R.color.maya),
//        tonalElevation = 8.dp
//    ) {
//        items.forEach { (route, iconRes) ->
//            val isSelected = currentRoute == route
//
//            IconButton(
//                onClick = {
//                    if (!isSelected) {
//                        navController.navigate(route) {
//                            launchSingleTop = true
//                            restoreState = true
//                            popUpTo(navController.graph.startDestinationId) {
//                                saveState = true
//                            }
//                        }
//                    }
//                },
//                modifier = Modifier.weight(1f)
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Icon(
//                        painter = painterResource(id = iconRes),
//                        contentDescription = route,
//                        modifier = Modifier.size(22.dp),
//                        tint = if (isSelected) colorResource(id = R.color.prem) else Color.Black
//                    )
//                    Text(
//                        text = if (route == "classroompage") "Classroom" else "Month View",
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = if (isSelected) colorResource(id = R.color.prem) else Color.Black
//                    )
//                }
//            }
//        }
//    }
//}

package com.example.rollcall.classroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rollcall.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        "classroompage" to R.drawable.homeicon,
        "monthviewpage" to R.drawable.monthviewicon
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        tonalElevation = 8.dp,
        color = colorResource(R.color.maya),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp) // Increased to make space for icon + text
                .padding(bottom = 4.dp), // Avoid clipping text on some devices
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (route, iconRes) ->
                val isSelected = currentRoute == route

                IconButton(
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = route,
                            modifier = Modifier.size(22.dp),
                            tint = if (isSelected) colorResource(id = R.color.prem) else Color.Black
                        )
                        Spacer(modifier = Modifier.height(1.dp)) // Space between icon and text
                        Text(
                            text = if (route == "classroompage") "Classroom" else "Month View",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colorResource(id = R.color.prem) else Color.Black
                        )
                    }
                }
            }
        }
    }
}
