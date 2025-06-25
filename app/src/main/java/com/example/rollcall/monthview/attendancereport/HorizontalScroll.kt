package com.example.rollcall.monthview.attendancereport

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rollcall.R
import com.example.rollcall.firebasedatabase.AttendanceData
import com.example.rollcall.firebasedatabase.StudentData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun HorizontalScroll(
    attendance: List<AttendanceData>,
    students: List<StudentData>,
    monthName: String,
    year: Int
) {
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()

    val filteredDates = attendance.mapNotNull { // new addd
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val dateObj = sdf.parse(it.date)
            val cal = Calendar.getInstance()
            cal.time = dateObj!!

            val monthStr = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
            val yearNum = cal.get(Calendar.YEAR)
            val day = SimpleDateFormat("d", Locale.ENGLISH).format(cal.time)

            if (monthStr.equals(monthName, ignoreCase = true) && yearNum == year) day else null
        } catch (e: Exception) {
            null
        }
    }.distinct().sortedBy { it.toInt() }

    Column(
        modifier = Modifier
            .verticalScroll(verticalScroll)
            .padding(8.dp)
    ) {
        // Header Row 1
        Row(modifier = Modifier.horizontalScroll(horizontalScroll)) {
            Text("Student", modifier = Modifier.width(200.dp).border(1.dp, Color.Black).padding(4.dp), fontWeight = FontWeight.Bold,textAlign = TextAlign.Center)
            Text(monthName, modifier = Modifier.width((filteredDates.size * 50).dp).border(1.dp, Color.Black).padding(4.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Conducted", modifier = Modifier.width(90.dp).border(1.dp, Color.Black).padding(4.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Present", modifier = Modifier.width(70.dp).border(1.dp, Color.Black).padding(4.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("%", modifier = Modifier.width(60.dp).border(1.dp, Color.Black).padding(4.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }

        // Header Row 2 (Date numbers)
        Row(modifier = Modifier.horizontalScroll(horizontalScroll)) {
            Text("", modifier = Modifier.width(200.dp).border(1.dp, Color.Black))
            filteredDates.forEach { d ->
                Text(d, modifier = Modifier.width(50.dp).border(1.dp, Color.Black).padding(4.dp), textAlign = TextAlign.Center)
            }
            Text("", modifier = Modifier.width(90.dp).border(1.dp, Color.Black))
            Text("", modifier = Modifier.width(70.dp).border(1.dp, Color.Black))
            Text("", modifier = Modifier.width(60.dp).border(1.dp, Color.Black))
        }

        // Data Rows
        students.forEach { student ->
            Row(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                Text("${student.fullName} | ${student.enrollment}", modifier = Modifier.width(200.dp).border(1.dp, Color.Black).padding(4.dp))

                var presentCount = 0

                filteredDates.forEach { day ->
                    val fullDate = "%02d/%02d/%04d".format( // new addd
                        day.toInt(),
                        getMonthNumber(monthName), // new addd
                        year
                    )

                    val att = attendance.find { it.date == fullDate && it.studentId == student.studentId }
                    val status = att?.status ?: ""
                    if (status == "P") presentCount++
                    val statusColor = when (status) {
                        "P" -> colorResource(R.color.presentcolor)
                        "A" -> colorResource(R.color.absentcolor)
                        else -> Color.Black
                    }

                    Text(status,modifier = Modifier.width(50.dp).border(1.dp, Color.Black).background(statusColor) .padding(4.dp), textAlign = TextAlign.Center)
                }

                val conducted = filteredDates.size
                val percent = if (conducted > 0) (presentCount * 100 / conducted) else 0
                val percentColor = if (percent < 75) Color.Red else Color.White

                Text("$conducted", modifier = Modifier.width(90.dp).border(1.dp, Color.Black).padding(4.dp), textAlign = TextAlign.Center)
                Text("$presentCount", modifier = Modifier.width(70.dp).border(1.dp, Color.Black).padding(4.dp), textAlign = TextAlign.Center)
                Text("$percent%",modifier = Modifier.width(60.dp).border(1.dp, Color.Black).background(percentColor).padding(4.dp), textAlign = TextAlign.Center)
            }
        }
    }
}

// new addd
fun getMonthNumber(monthName: String): Int {
    val date = SimpleDateFormat("MMMM", Locale.ENGLISH).parse(monthName)
    val cal = Calendar.getInstance()
    cal.time = date!!
    return cal.get(Calendar.MONTH) + 1
}
