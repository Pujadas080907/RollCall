package com.example.rollcall.Classroom

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rollcall.Navigation.Routes
import com.example.rollcall.R
import com.example.rollcall.ui.theme.laila
import com.example.rollcall.ui.theme.title


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegreeScreen(navController: NavController) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var degreeName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "RollCall",
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontFamily = title,
                            fontWeight = FontWeight.Bold
                        )
                    }
                        },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colorResource(id = R.color.main_color), titleContentColor = Color.White)
            )
        },
        bottomBar = { BottomNavigationBar(navController) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                containerColor = colorResource(id = R.color.main_color)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus_icon),
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.bg_color))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Click on the \"+\" button to get started!",
                fontSize = 18.sp,
                color = Color.Black,
                fontFamily = laila,
                fontWeight = FontWeight.Medium
            )
        }
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add new Batch",
                    fontSize = 20.sp,
                    fontFamily = laila,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = degreeName,
                    onValueChange = { degreeName = it },
                    label = { Text("Enter Degree") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { showBottomSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = {
                            // Handle add action
                            showBottomSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.main_color))
                    ) {
                        Text("Add", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}





@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(0) }
    val items = listOf(
        "Classroom" to R.drawable.classroom,
        "Month View" to R.drawable.month_view,


    )

    BottomAppBar(
        modifier = Modifier
            .height(55.dp)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
        containerColor = Color.LightGray,
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            IconButton(
                onClick = {
                    selectedIndex.value = index

                    when (index) {
                        0 -> navController.navigate(Routes.Degree.routes)
                        1 -> navController.navigate(Routes.MonthView.routes)

                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = item.second),
                        contentDescription = item.first,
                        modifier = Modifier.size(20.dp),
                        tint = if (selectedIndex.value == index) colorResource(id = R.color.main_color) else Color.Black
                    )
                    Text(
                        text = item.first,
                        fontSize = 12.sp,
                        fontFamily = laila,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedIndex.value == index) colorResource(id = R.color.main_color) else Color.Black
                    )
                }
            }
        }
    }
}
