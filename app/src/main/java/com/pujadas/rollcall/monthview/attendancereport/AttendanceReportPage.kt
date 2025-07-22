package com.pujadas.rollcall.monthview.attendancereport

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.view.View
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.pujadas.rollcall.R
import com.pujadas.rollcall.classroom.BottomNavigationBar
import com.pujadas.rollcall.firebasedatabase.AttendanceData
import com.pujadas.rollcall.firebasedatabase.ClassroomData
import com.pujadas.rollcall.firebasedatabase.FirebaseDbHelper
import com.pujadas.rollcall.firebasedatabase.StudentData
import com.pujadas.rollcall.ui.theme.Laila
import com.pujadas.rollcall.ui.theme.Lalezar
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.android.material.snackbar.Snackbar



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
    val currentMonthName = remember { mutableStateOf(monthName) }
    val currentYear = remember { mutableStateOf(year) }
    val context = LocalContext.current // ✅ newly added
    val showExportDialog = remember { mutableStateOf(false) } // ✅ newly added

    LaunchedEffect(Unit) {
        val monthIndex = SimpleDateFormat("MMMM", Locale.ENGLISH)
            .parse(monthName)?.month ?: 0
        calendar.value.set(Calendar.MONTH, monthIndex)
        calendar.value.set(Calendar.YEAR, year)
    }



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
        containerColor = Color.White
    ) { inner ->

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),

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
                    .height(500.dp)
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
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { showExportDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.prem)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "Export Attendance Record",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Laila
                )
            }

            // ✅ Export Dialog
            if (showExportDialog.value) {
                AlertDialog(
                    onDismissRequest = { showExportDialog.value = false },
                    title = {
                        Text(
                            text = "Export Attendance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = Laila
                        )
                    },
                    text = { Text("Choose a format:", fontFamily = Laila) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                exportAttendanceToPDF(context, students, attendance, currentMonthName.value, currentYear.value)
                                showExportDialog.value = false
                            }
                        ) {
                            Text("As PDF", fontFamily = Laila)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                exportAttendanceToExcel(context, students, attendance, currentMonthName.value, currentYear.value)
                                showExportDialog.value = false
                            }
                        ) {
                            Text("As Excel", fontFamily = Laila)
                        }
                    }
                )
            }
        }

    }
}

fun exportAttendanceToPDF(
    context: Context,
    students: List<StudentData>,
    attendance: List<AttendanceData>,
    month: String,
    year: Int
) {
    val pdfDocument = PdfDocument()
    val paint = Paint().apply {
        textSize = 12f
    }
    val titlePaint = Paint().apply {
        textSize = 16f
        isFakeBoldText = true
    }

    val pageInfo = PdfDocument.PageInfo.Builder(842, 1190, 1).create() // A4 size in pixels
    var page = pdfDocument.startPage(pageInfo)
    var canvas = page.canvas

    var x: Int
    var y = 40

    // Title
    canvas.drawText("Attendance Report - $month $year", 20f, y.toFloat(), titlePaint)
    y += 30

    // Prepare filtered date headers
    val filteredDates = attendance.mapNotNull {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val dateObj = sdf.parse(it.date)
            val cal = Calendar.getInstance()
            cal.time = dateObj!!

            val monthStr = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
            val yearNum = cal.get(Calendar.YEAR)
            val day = SimpleDateFormat("d", Locale.ENGLISH).format(cal.time)

            if (monthStr.equals(month, ignoreCase = true) && yearNum == year) day else null
        } catch (e: Exception) {
            null
        }
    }.distinct().sortedBy { it.toInt() }

    // Define column widths smartly to fit 842px
    val totalWidth = 800  // Leave some margin
    val nameColWidth = 200
    val dateColWidth = 25
    val conductedColWidth = 60
    val presentColWidth = 60
    val percentColWidth = 60

    val tableStartX = 20
    val colWidths = mutableListOf<Int>()
    colWidths.add(nameColWidth)
    colWidths.addAll(List(filteredDates.size) { dateColWidth })
    colWidths.addAll(listOf(conductedColWidth, presentColWidth, percentColWidth))

    // Header row
    x = tableStartX
    val headers = mutableListOf("Student")
    headers.addAll(filteredDates)
    headers.addAll(listOf("Con.", "Pre.", "%"))

    headers.forEachIndexed { i, h ->
        canvas.drawText(h, x.toFloat(), y.toFloat(), titlePaint)
        x += colWidths[i]
    }

    y += 25

    // Table content
    students.forEach { student ->
        x = tableStartX
        val studentName = "${student.fullName} (${student.enrollment})"
        canvas.drawText(studentName, x.toFloat(), y.toFloat(), paint)
        x += nameColWidth

        var presentCount = 0

        filteredDates.forEach { day ->
            val fullDate = "%02d/%02d/%04d".format(
                day.toInt(),
                getMonthNumber(month),
                year
            )
            val att = attendance.find { it.date == fullDate && it.studentId == student.studentId }
            val status = att?.status ?: ""
            if (status == "P") presentCount++
            canvas.drawText(status, x.toFloat(), y.toFloat(), paint)
            x += dateColWidth
        }

        val conducted = filteredDates.size
        val percent = if (conducted > 0) (presentCount * 100 / conducted) else 0

        canvas.drawText("$conducted", x.toFloat(), y.toFloat(), paint)
        x += conductedColWidth
        canvas.drawText("$presentCount", x.toFloat(), y.toFloat(), paint)
        x += presentColWidth
        canvas.drawText("$percent%", x.toFloat(), y.toFloat(), paint)

        y += 20

        // New page check
        if (y > 1150) {
            pdfDocument.finishPage(page)
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = 40

            // Re-draw header on new page
            x = tableStartX
            headers.forEachIndexed { i, h ->
                canvas.drawText(h, x.toFloat(), y.toFloat(), titlePaint)
                x += colWidths[i]
            }
            y += 25
        }
    }

    pdfDocument.finishPage(page)

    val baseFileName = "Attendance_Report_${month}${year}"
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    var file = File(downloadsDir, "$baseFileName.pdf")
    var counter = 1

    while (file.exists()) {
        file = File(downloadsDir, "$baseFileName($counter).pdf")
        counter++
    }


//    Toast.makeText(context, "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()

    try {
        pdfDocument.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }

}

fun exportAttendanceToExcel(
    context: Context,
    students: List<StudentData>,
    attendance: List<AttendanceData>,
    month: String,
    year: Int
) {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val baseFileName = "Attendance_Report_${month}${year}"
    var file = File(downloadsDir, "$baseFileName.csv")
    var counter = 1

    // Ensure unique filename
    while (file.exists()) {
        file = File(downloadsDir, "$baseFileName($counter).csv")
        counter++
    }

    val writer = FileWriter(file)

    // Get filtered date headers (like in PDF)
    val filteredDates = attendance.mapNotNull {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val dateObj = sdf.parse(it.date)
            val cal = Calendar.getInstance()
            cal.time = dateObj!!

            val monthStr = SimpleDateFormat("MMMM", Locale.ENGLISH).format(cal.time)
            val yearNum = cal.get(Calendar.YEAR)
            val day = SimpleDateFormat("d", Locale.ENGLISH).format(cal.time)

            if (monthStr.equals(month, ignoreCase = true) && yearNum == year) day else null
        } catch (e: Exception) {
            null
        }
    }.distinct().sortedBy { it.toInt() }

    // Write headers
    writer.append("Student Name,Enrollment")
    filteredDates.forEach { day ->
        writer.append(",${day.padStart(2, '0')}")
    }
    writer.append(",Conducted,Present,%\n")

    // Write each student's row
    students.forEach { student ->
        var presentCount = 0

        writer.append("${student.fullName},${student.enrollment}")

        filteredDates.forEach { day ->
            val fullDate = "%02d/%02d/%04d".format(
                day.toInt(),
                getMonthNumber(month),
                year
            )
            val att = attendance.find { it.date == fullDate && it.studentId == student.studentId }
            val status = att?.status ?: ""
            if (status == "P") presentCount++
            writer.append(",$status")
        }

        val conducted = filteredDates.size
        val percent = if (conducted > 0) (presentCount * 100 / conducted) else 0

        writer.append(",$conducted,$presentCount,$percent%\n")
    }

    writer.flush()
    writer.close()

    Toast.makeText(context, "CSV saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
}

