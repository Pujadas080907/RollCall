package com.pujadas.rollcall.monthview

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pujadas.rollcall.R
import com.pujadas.rollcall.firebasedatabase.AttendanceData
import com.pujadas.rollcall.firebasedatabase.ClassroomData
import com.pujadas.rollcall.firebasedatabase.FirebaseDbHelper
import com.pujadas.rollcall.firebasedatabase.StudentData
import com.pujadas.rollcall.ui.theme.Laila
import com.pujadas.rollcall.ui.theme.Lalezar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp


@Composable
fun PercentagePage(
    navController: NavController,
    classroom: ClassroomData,
    monthName: String,
    year: Int
) {
    val students = remember { mutableStateListOf<StudentData>() }
    val attendance = remember { mutableStateListOf<AttendanceData>() }
    val isLoading = remember { mutableStateOf(true) }
    val currentMonthName = remember { mutableStateOf(monthName) }
    val currentYear = remember { mutableStateOf(year) }

    fun updateMonth(offset: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, currentYear.value)
        cal.set(
            Calendar.MONTH,
            SimpleDateFormat("MMMM", Locale.ENGLISH).parse(currentMonthName.value)?.let {
                Calendar.getInstance().apply { time = it }.get(Calendar.MONTH)
            } ?: 0)
        cal.add(Calendar.MONTH, offset)
        currentMonthName.value = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
        currentYear.value = cal.get(Calendar.YEAR)

        // Trigger attendance reload
        isLoading.value = true
        FirebaseDbHelper.getAttendanceByClassroom(
            classroom.id,
            onSuccess = {
                val filtered = it.filter { data ->
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    val dateObj = sdf.parse(data.date)
                    val calDate = Calendar.getInstance()
                    calDate.time = dateObj!!
                    val m = SimpleDateFormat("MMMM", Locale.ENGLISH).format(calDate.time)
                    val y = calDate.get(Calendar.YEAR)
                    m.equals(currentMonthName.value, true) && y == currentYear.value
                }
                attendance.clear()
                attendance.addAll(filtered)
                isLoading.value = false
            },
            onFailure = { isLoading.value = false }
        )
    }


    LaunchedEffect(Unit) {
        FirebaseDbHelper.getStudentsByClassroom(
            classroom.id,
            onSuccess = {
                val sortedList = it.sortedBy { s -> s.enrollment.lowercase() }
                students.clear()
                students.addAll(sortedList)
            },
            onFailure = {}
        )

        FirebaseDbHelper.getAttendanceByClassroom(
            classroom.id,
            onSuccess = {
                val filtered = it.filter { data ->
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                    val dateObj = sdf.parse(data.date)
                    val cal = Calendar.getInstance()
                    cal.time = dateObj!!
                    val m = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
                    val y = cal.get(Calendar.YEAR)
                    m.equals(monthName, true) && y == year
                }
                attendance.clear()
                attendance.addAll(filtered)
                isLoading.value = false
            },
            onFailure = { isLoading.value = false }
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
                    modifier = Modifier.fillMaxWidth()
                        .padding(WindowInsets.statusBars.asPaddingValues())
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(45.dp))
                        Text(
                            text = "${currentMonthName.value} - ${currentYear.value}",

                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Lalezar
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(classroom.degree, color = Color.White, fontFamily = Laila)
                        Text(
                            "${classroom.year} / Sec: ${classroom.section}",
                            color = Color.White,
                            fontFamily = Laila
                        )
                    }
                }
            }
        }
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
                    color = Color.Black,
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

            Spacer(modifier = Modifier.height(12.dp)) // Add a gap below selector

            if (isLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorResource(R.color.prem))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)
                ) {
                    val uniqueDates = attendance.map { it.date }.distinct()
                    val totalDays = uniqueDates.size

                    items(students) { student ->
                        val presentDays = attendance.count {
                            it.studentId == student.studentId && it.status == "P"
                        }
                        val percentage = if (totalDays > 0) (presentDays * 100 / totalDays) else 0

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularPercentageIndicator(percentage = percentage)

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        "${student.fullName} | ${student.enrollment}",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Laila
                                    )
                                    Text(
                                        "Attended: $presentDays | Conducted: $totalDays",
                                        fontSize = 12.sp,
                                        fontFamily = Laila
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CircularPercentageIndicator(
    percentage: Int,
    size: Dp = 50.dp,
    strokeWidth: Dp = 4.dp,
    backgroundColor: Color = colorResource(R.color.maya),
    progressColor: Color = colorResource(R.color.prem)// Purple
) {

    val sweep = remember { Animatable(0f) }
    LaunchedEffect(percentage) {
        sweep.animateTo(
            targetValue = (percentage / 100f) * 360f,
            animationSpec = tween(durationMillis = 2000)
        )
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {

            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)

            // Background circle
            drawCircle(
            color = backgroundColor,
            radius = size.toPx() / 2f
            )


            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweep.value,
                useCenter = false,
                style = stroke
            )
        }

        Text("$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
