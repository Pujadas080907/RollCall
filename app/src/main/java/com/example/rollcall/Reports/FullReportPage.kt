package com.example.rollcall.Reports

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rollcall.R
import com.example.rollcall.firebasedatabase.AttendanceData
import com.example.rollcall.firebasedatabase.ClassroomData
import com.example.rollcall.firebasedatabase.FirebaseDbHelper
import com.example.rollcall.ui.theme.Laila
import com.example.rollcall.ui.theme.Lalezar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullReportPage(
    navController: NavController,
    classroom: ClassroomData,
    selectedDate: String
) {
    val context = LocalContext.current
    val attendanceList = remember { mutableStateListOf<AttendanceData>() }

    // Load attendance from Firebase
    LaunchedEffect(Unit) {
        FirebaseDbHelper.getAttendanceByClassroom(
            classroomId = classroom.id,
            onSuccess = {
                attendanceList.clear()
                attendanceList.addAll(it)
            },
            onFailure = {
                Toast.makeText(context, "Failed to load attendance", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 🔤 Convert "25/01/2025" → "January - 2025"
    val monthYear = remember(selectedDate) {
        try {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedDate)
            SimpleDateFormat("MMMM - yyyy", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            "Attendance Report"
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(monthYear, fontFamily = Lalezar, color = Color.White)
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(R.color.prem))
        )

        Text(
            text = "${classroom.degree}   ${classroom.year} / Sec: ${classroom.section}",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.prem))
                .padding(horizontal = 16.dp),
            fontFamily = Laila,
            fontSize = 14.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Text(selectedDate, fontFamily = Laila, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }

        LazyColumn {
            items(attendanceList.filter { it.date == selectedDate }) { record ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background(
                            color = if (record.status == "P") Color(0xFF81C784) else Color(0xFFEF9A9A),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${record.fullName} | ${record.enrollment}", fontFamily = Laila)
                        Text(record.status, fontWeight = FontWeight.Bold, fontFamily = Laila)
                    }
                }
            }
        }
    }
}
