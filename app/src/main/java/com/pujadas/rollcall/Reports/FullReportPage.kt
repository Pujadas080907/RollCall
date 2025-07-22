
package com.pujadas.rollcall.Reports

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // ✅ NEW: For arrow click
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pujadas.rollcall.firebasedatabase.AttendanceData
import com.pujadas.rollcall.firebasedatabase.ClassroomData
import com.pujadas.rollcall.firebasedatabase.FirebaseDbHelper
import com.pujadas.rollcall.ui.theme.Laila
import com.pujadas.rollcall.ui.theme.Lalezar
import java.text.SimpleDateFormat
import java.util.*
import com.pujadas.rollcall.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullReportPage(
    navController: NavController,
    classroom: ClassroomData,
    initialDate: String // ✅ renamed from `selectedDate` to `initialDate`
) {
    val context = LocalContext.current
    val attendanceList = remember { mutableStateListOf<AttendanceData>() }
    val isLoading = remember { mutableStateOf(true) }

    // ✅ NEW: Make date changeable with arrow buttons
    val selectedDate = remember { mutableStateOf(initialDate) }

    // ✅ NEW: Date helpers
    fun getPreviousDate(dateStr: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(dateStr)!!
        calendar.add(Calendar.DATE, -1)
        return sdf.format(calendar.time)
    }

    fun getNextDate(dateStr: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(dateStr)!!
        calendar.add(Calendar.DATE, 1)
        return sdf.format(calendar.time)
    }

    // ✅ NEW: Reload attendance when date changes
    LaunchedEffect(selectedDate.value) {
        isLoading.value = true

        FirebaseDbHelper.getAttendanceByClassroom(
            classroomId = classroom.id,
            onSuccess = { attendanceData ->

                FirebaseDbHelper.getStudentsByClassroom(
                    classroomId = classroom.id,
                    onSuccess = { currentStudents ->
                        val studentMap = currentStudents.associateBy { it.studentId }

                        val updatedAttendance = attendanceData.mapNotNull { att ->
                            studentMap[att.studentId]?.let { student ->
                                att.copy(
                                    fullName = student.fullName,
                                    enrollment = student.enrollment
                                )
                            }
                        }.sortedBy { it.enrollment.lowercase() }

                        attendanceList.clear()
                        attendanceList.addAll(updatedAttendance)
                        isLoading.value = false
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to load students", Toast.LENGTH_SHORT).show()
                        isLoading.value = false
                    }
                )
            },
            onFailure = {
                Toast.makeText(context, "Failed to load attendance", Toast.LENGTH_SHORT).show()
                isLoading.value = false
            }
        )
    }

    // Format for top bar
    val monthYear = remember(selectedDate.value) {
        try {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedDate.value)
            SimpleDateFormat("MMMM - yyyy", Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            "Attendance Report"
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = colorResource(id = R.color.prem),
                tonalElevation = 4.dp,
                modifier = Modifier.defaultMinSize(minHeight = 50.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = monthYear, color = Color.White, fontSize = 22.sp, fontFamily = Lalezar)
                        }

                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = classroom.degree, color = Color.White, fontSize = 15.sp, fontFamily = Laila)
                        Text(text = "${classroom.year} / Sec: ${classroom.section}", color = Color.White, fontSize = 15.sp, fontFamily = Laila)
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(45.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ NEW: Clickable back arrow to move to previous date
                Icon(
                    painter = painterResource(R.drawable.backarrow), contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(start = 5.dp)
                        .clickable {
                            selectedDate.value = getPreviousDate(selectedDate.value)
                        }
                )

                Text(selectedDate.value, fontFamily = Laila, fontWeight = FontWeight.Bold)

                // ✅ NEW: Clickable forward arrow to move to next date
                Icon(
                    painter = painterResource(R.drawable.forwardarrow), contentDescription = null,
                    modifier = Modifier
                        .size(23.dp)
                        .padding(end = 5.dp)
                        .clickable {
                            selectedDate.value = getNextDate(selectedDate.value)
                        }
                )
            }

            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = colorResource(R.color.maya))
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Reports are fetching...⏳",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Laila
                        )
                    }
                }
            } else {
                // ✅ NEW: Filtered attendance for selected date
                val filteredRecords = attendanceList.filter { it.date == selectedDate.value }

                if (filteredRecords.isEmpty()) {
                    // ✅ NEW: No attendance fallback
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.nomatch),
                                contentDescription = "No class",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(180.dp)
                                    .padding(bottom = 12.dp)
                            )
                            Text(
                                text = "Class was not conducted on this day ❌",
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontFamily = Laila,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(filteredRecords) { record ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 4.dp)
                                    .background(
                                        color = if (record.status == "P")
                                            colorResource(R.color.presentcolor)
                                        else
                                            colorResource(R.color.absentcolor),
                                        shape = RoundedCornerShape(0.dp)
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
        }
    }
}
