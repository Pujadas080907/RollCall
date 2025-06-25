package com.example.rollcall.monthview

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rollcall.R
import com.example.rollcall.classroom.BottomNavigationBar
import com.example.rollcall.firebasedatabase.ClassroomData
import com.example.rollcall.navGraph.Routes
import com.example.rollcall.ui.theme.Laila
import com.example.rollcall.ui.theme.Lalezar
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDetailedViewPage(navController: NavController, classroom: ClassroomData) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val yearOptions = (2025..(currentYear + 5)).toList()

    val selectedYear = remember { mutableStateOf(currentYear) }
    val expanded = remember { mutableStateOf(false) }

    val months = listOf(
        "January" to R.drawable.jan,
        "February" to R.drawable.feb,
        "March" to R.drawable.mar,
        "April" to R.drawable.apr,
        "May" to R.drawable.may,
        "June" to R.drawable.jun,
        "July" to R.drawable.jul,
        "August" to R.drawable.aug,
        "September" to R.drawable.sep,
        "October" to R.drawable.oct,
        "November" to R.drawable.nov,
        "December" to R.drawable.dec
    )

    Scaffold(

        topBar = {

            Surface(
                color = colorResource(id = R.color.prem),
                tonalElevation = 4.dp,
                modifier = Modifier.defaultMinSize(minHeight = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(WindowInsets.statusBars.asPaddingValues())
                ) {


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Records - ${selectedYear.value}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = Lalezar,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        // Year Dropdown
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedYear.value.toString(), color = Color.White)
                            IconButton(onClick = { expanded.value = true }) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) {
                                yearOptions.forEach { year ->
                                    DropdownMenuItem(
                                        text = { Text(year.toString()) },
                                        onClick = {
                                            selectedYear.value = year
                                            expanded.value = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Degree & Section Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = classroom.degree,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = Laila
                        )
                        Text(
                            text = "${classroom.year} / Sec: ${classroom.section}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = Laila
                        )
                    }
                }
            }
        },

                bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(months.size) { index ->
                val (monthName, imageRes) = months[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable {
                            val route = Routes.reportAndPercentagePage.routes
                                .replace("{degree}", Uri.encode(classroom.degree))
                                .replace("{year}", Uri.encode(classroom.year))
                                .replace("{section}", Uri.encode(classroom.section))
                                .replace("{cid}", classroom.id)
                                .replace("{monthName}", monthName)
                                .replace("{yearNum}", selectedYear.value.toString())
                            navController.navigate(route)

                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colorResource(R.color.maya), colorResource(R.color.prem))
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = monthName,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "$monthName ${selectedYear.value}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                fontFamily = Laila
                            )
                        }
                    }
                }
            }
        }
    }
}
