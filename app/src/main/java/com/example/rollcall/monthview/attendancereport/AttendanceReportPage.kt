package com.example.rollcall.monthview.attendancereport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.rollcall.firebasedatabase.AttendanceData
import com.example.rollcall.firebasedatabase.ClassroomData
import com.example.rollcall.firebasedatabase.FirebaseDbHelper
import com.example.rollcall.firebasedatabase.StudentData
import com.example.rollcall.ui.theme.Laila
import com.example.rollcall.ui.theme.Lalezar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportPage(
    navController: NavController,
    classroom: ClassroomData,
    monthName: String,
    year: Int
) {
    // Load students and attendance from Firebase
    val students = remember { mutableStateListOf<StudentData>() }
    val attendance = remember { mutableStateListOf<AttendanceData>() }
    val allDates = remember { mutableStateListOf<String>() }
    val calendar = remember { mutableStateOf(Calendar.getInstance()) }
    val isAttendanceLoading = remember { mutableStateOf(true) } // ✅ for attendance only


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
        isAttendanceLoading.value = true
        FirebaseDbHelper.getAttendanceByClassroom(
            classroomId = classroom.id,
            onSuccess = { allAttendance ->
                val filtered = allAttendance.filter {
                    try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                        val dateObj = sdf.parse(it.date)
                        val cal = Calendar.getInstance()
                        cal.time = dateObj!!
                        val monthStr = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
                        val yearNum = cal.get(Calendar.YEAR)
                        monthStr.equals(
                            newMonthName,
                            ignoreCase = true
                        ) && yearNum == currentYear.value
                    } catch (e: Exception) {
                        false
                    }
                }
                attendance.clear()
                attendance.addAll(filtered)
                val uniqueDates = filtered.mapNotNull {
                    try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                        val dateObj = sdf.parse(it.date)
                        SimpleDateFormat("d", Locale.ENGLISH).format(dateObj!!)
                    } catch (e: Exception) {
                        null
                    }
                }.distinct().sortedBy { it.toInt() }
                allDates.clear()
                allDates.addAll(uniqueDates)
                isAttendanceLoading.value = false
            },
            onFailure = { isAttendanceLoading.value = false }
        )
    }

    LaunchedEffect(Unit) {
        // Load students
        FirebaseDbHelper.getStudentsByClassroom(
            classroomId = classroom.id,
            onSuccess = {
                val sortedList = it.sortedBy { s -> s.enrollment.lowercase() }
                students.clear()
                students.addAll(sortedList)
            },
            onFailure = { /* handle error */ }
        )
        isAttendanceLoading.value = true
        // Load attendance
        FirebaseDbHelper.getAttendanceByClassroom(
            classroomId = classroom.id,
            onSuccess = { allAttendance ->
                val filtered = allAttendance.filter { // new addd
                    try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH) // new addd
                        val dateObj = sdf.parse(it.date)
                        val cal = Calendar.getInstance()
                        cal.time = dateObj!!

                        val monthStr = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
                        val yearNum = cal.get(Calendar.YEAR)

                        monthStr.equals(monthName, ignoreCase = true) && yearNum == year
                    } catch (e: Exception) {
                        false
                    }
                }


                attendance.clear()
                attendance.addAll(filtered)


                val uniqueDates = filtered.mapNotNull { // new addd
                    try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                        val dateObj = sdf.parse(it.date)
                        SimpleDateFormat("d", Locale.ENGLISH).format(dateObj!!)
                    } catch (e: Exception) {
                        null
                    }
                }.distinct().sortedBy { it.toInt() }

                allDates.clear()
                allDates.addAll(uniqueDates)
                isAttendanceLoading.value = false

            },
            onFailure = { isAttendanceLoading.value = false}
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = colorResource(id = R.color.prem),
                tonalElevation = 4.dp,
                modifier = Modifier.defaultMinSize(minHeight = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.statusBars.asPaddingValues())
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
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color.White
    ) { inner ->

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─────────────── Month Selector Row ───────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.backarrow),
                    contentDescription = "Previous Month",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { updateMonth(-1) }
                )

                Text(
                    text = currentMonthName.value.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = Lalezar,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Icon(
                    painter = painterResource(R.drawable.forwardarrow),
                    contentDescription = "Next Month",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { updateMonth(1) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ─────────────── Attendance Table ───────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ) {
                when {
                    isAttendanceLoading.value -> {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = colorResource(id = R.color.prem)
                        )
                    }

                    (students.isEmpty()) -> {
                        Text(
                            "No students found 👤",
                            modifier = Modifier.align(Alignment.Center),
                            fontFamily = Laila,
                            fontSize = 18.sp
                        )
                    }

                    (attendance.isEmpty()) -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.nomatch),
                                    contentDescription = "No match",
                                    modifier = Modifier.size(180.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No attendance taken for this month :(",
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Laila
                                )
                            }
                        }
                    }

                    else -> {
                        HorizontalScroll(
                            attendance = attendance,
                            students = students,
                            monthName = currentMonthName.value,
                            year = currentYear.value
                        )
                    }
                }
            }
        }

    }
}

