package com.example.rollcall.monthview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material.icons.filled.ArrowForward

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.rollcall.ui.theme.Laila
import com.example.rollcall.ui.theme.Lalezar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAndPercentagePage(
    navController: NavController,
    classroom: ClassroomData,
    monthName: String,
    year: Int
)
{
    val calendar = remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        val monthIndex = SimpleDateFormat("MMMM", Locale.ENGLISH)
            .parse(monthName)?.month ?: 0
        calendar.value.set(Calendar.MONTH, monthIndex)
        calendar.value.set(Calendar.YEAR, year)
    }

    val currentMonthName = remember { mutableStateOf(monthName) }
    val currentYear = remember { mutableStateOf(year) }

    fun updateMonth(offset: Int) {
        calendar.value.add(Calendar.MONTH, offset)
        val newMonthName = SimpleDateFormat("MMMM", Locale.ENGLISH).format(calendar.value.time)
        currentMonthName.value = newMonthName
        currentYear.value = calendar.value.get(Calendar.YEAR)
    }



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
                    // ── Top Row: Back Button + Title ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(45.dp))
                        Text(
//                            text = "$monthName - $year",
                            text = "${currentMonthName.value} - ${currentYear.value}",

                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = Lalezar,
                            textAlign = TextAlign.Center
                        )
                    }

                    // ── Second Row: Degree & Year/Sec ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = classroom.degree,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontFamily = Laila
                        )
                        Text(
                            text = "${classroom.year} / Sec: ${classroom.section}",
                            fontSize = 14.sp,
                            color = Color.White,
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Month Title with Arrows
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(painterResource(
                    R.drawable.backarrow), contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(start = 5.dp)
                        .clickable {
                            updateMonth(-1)
                        }
                    )
                Text(currentMonthName.value.uppercase(), fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Icon(painterResource(
                    R.drawable.forwardarrow),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(start = 5.dp)
                        .clickable {
                            updateMonth(1)

                        }
                    )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Report Card
            ReportCard(
                imageRes = R.drawable.student3,
                title = "SEE REPORT",
                onClick = {
                    // Navigate to full report
                    val date = "$monthName-$year"
                    val route = "fullreportpage/${classroom.degree}/${classroom.year}/${classroom.section}/${classroom.id}/$date"
                    navController.navigate(route)
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Percentage Card
            ReportCard(
                imageRes = R.drawable.report,
                title = "SEE PERCENTAGE",
                onClick = {
                    // TODO: Add navigation to percentage page
                }
            )
        }
    }
}

@Composable
fun ReportCard(imageRes: Int, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colorResource(R.color.maya),
                            colorResource(R.color.prem)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 25.sp,
                    color = Color.Black
                )
            }
        }
    }
}
