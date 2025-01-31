package com.example.rollcall.Classroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.rollcall.Database.Degree
import com.example.rollcall.R
import com.example.rollcall.ui.theme.laila
import com.example.rollcall.ui.theme.title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegreeDetailScreen(
    navController: NavController,
    degreeId: Int,
    degreeName: String,) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }


                        Text(
                            text = degreeName,
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = title,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "menu",
                                tint = Color.White
                            )
                        }
                    }

                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorResource(id = R.color.main_color),
                    titleContentColor = Color.White
                )

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                          },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                containerColor = colorResource(id = R.color.main_color)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus_icon),
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier
                        .size(35.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Click on the \"+\" button to add a new student",
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = laila,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
