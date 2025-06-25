package com.example.rollcall.degreedetail

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.rollcall.ui.theme.Laila
import com.example.rollcall.ui.theme.Lalezar
import com.example.rollcall.R
import com.example.rollcall.firebasedatabase.FirebaseDbHelper
import com.example.rollcall.studentdata.StudentRow
import com.example.rollcall.ui.theme.topbarfont
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.LocalContext
import com.example.rollcall.firebasedatabase.AttendanceData
import com.example.rollcall.firebasedatabase.ClassroomData
import com.example.rollcall.firebasedatabase.StudentData
import com.google.firebase.auth.FirebaseAuth
import java.net.URLEncoder


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegreeDetailPage(
    navController: NavController,
    degree: String,
    year: String,
    section: String,
    cid: String
) {
    val showAddDialog   = remember { mutableStateOf(false) }
    val fullName        = remember { mutableStateOf("") }
    val enrollment      = remember { mutableStateOf("") }
    /* student list state */
    val students  = remember { mutableStateListOf<StudentData>() }
    val loading   = remember { mutableStateOf(true) }
    val query     = remember { mutableStateOf("") }
    val studentToEdit = remember { mutableStateOf<StudentData?>(null) }
    val attendanceAlreadyTaken = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf("") }

// REMOVE these lines from inside StudentRow:
    val presentIds = remember { mutableStateListOf<String>() }
    val absentIds = remember { mutableStateListOf<String>() }
    val classroom = ClassroomData(
        degree = degree,
        year = year,
        section = section,
        id = cid
    )




    /* initial fetch */
    LaunchedEffect(Unit) {
        FirebaseDbHelper.getStudentsByClassroom(
            classroomId = cid,
            onSuccess = { students.addAll(it); loading.value = false },
            onFailure = { loading.value = false }
        )
    }

    LaunchedEffect(selectedDate.value) {
        if (selectedDate.value.isNotEmpty()) {
            FirebaseDbHelper.getAttendanceForDate(
                classroomId = cid,
                date = selectedDate.value,
                onSuccess = { attendanceList ->
                    presentIds.clear()
                    absentIds.clear()

                    presentIds.addAll(attendanceList.filter { it.status == "P" }.map { it.studentId })
                    absentIds.addAll(attendanceList.filter { it.status == "A" }.map { it.studentId })

                    attendanceAlreadyTaken.value = attendanceList.isNotEmpty()
                },
                onFailure = {
                    Toast.makeText(context, "Failed to load attendance", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    val filtered = students.filter {
        val q = query.value.trim().lowercase()
        q.isEmpty() ||
                it.fullName.lowercase().contains(q) ||
                it.enrollment.lowercase().contains(q)
    }

    Scaffold(
        topBar = {

            Surface(
                color = colorResource(id = R.color.prem),
                tonalElevation = 4.dp,
                modifier = Modifier
                    .defaultMinSize(minHeight = 50.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        /* ← Back */
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        /*  Title */
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = degree,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = topbarfont
                            )
                        }


                        var menuExpanded by remember { mutableStateOf(false) }
                        Box(                               // anchor container
                            modifier = Modifier
                                .padding(end= 10.dp)
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "More",
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painterResource(R.drawable.seereporticon),
                                                contentDescription = null,
                                                Modifier.size(20.dp),
                                                tint = Color.Unspecified
                                                )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("See Report", fontSize = 15.sp, fontFamily = Laila)
                                        }
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        if (selectedDate.value.isNotEmpty()) {
                                            val encodedDate = URLEncoder.encode(selectedDate.value, "UTF-8")
                                            val route = "fullreportpage/${classroom.degree}/${classroom.year}/${classroom.section}/${classroom.id}/$encodedDate"
                                            navController.navigate(route)

                                        } else {
                                            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                                        }


                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(painterResource(R.drawable.edit), contentDescription = null,
                                                Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Coming soon", fontSize = 15.sp, fontFamily = Laila)
                                        }
                                    },
                                    onClick = { /* TODO */ }
                                )
                            }
                        }
                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$year / Sec: $section",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = Laila
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        selectedDate.value = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                                        FirebaseDbHelper.getAttendanceForDate(
                                            classroomId = cid,
                                            date = selectedDate.value,
                                            onSuccess = { existingList ->
                                                presentIds.clear()
                                                absentIds.clear()
                                                existingList.forEach {
                                                    when (it.status) {
                                                        "P" -> presentIds.add(it.studentId)
                                                        "A" -> absentIds.add(it.studentId)
                                                    }
                                                }
                                            },
                                            onFailure = {
                                                Toast.makeText(context, "Failed to fetch attendance", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.yearicon),
                                contentDescription = "Calendar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (selectedDate.value.isNotEmpty()) selectedDate.value else "Choose Date",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = Laila
                            )
                        }

                    }
                }
            }
        },

        containerColor = Color.White
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when {
                    loading.value -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = colorResource(R.color.maya))
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Students details are fetching...⏳",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Laila
                                )
                            }
                        }
                    }

                    students.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Student List is Empty :(",
                                fontFamily = Lalezar,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Image(
                                painter = painterResource(R.drawable.empty),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(180.dp)
                                    .padding(horizontal = 16.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Click on the ➕ button to add a student.",
                                fontFamily = Laila,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    else -> {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .background(Color.White, shape = RoundedCornerShape(15.dp))
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(15.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.search1),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(22.dp)
                                )

                                TextField(
                                    value = query.value,
                                    onValueChange = { query.value = it },
                                    placeholder = {
                                        Text("Search Student", fontFamily = Laila,color = Color.Gray)
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.White,
                                        focusedContainerColor = Color.White,
                                        unfocusedTextColor = Color.Gray,
                                        focusedTextColor = Color.Gray,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 8.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📋 Students List",
                                fontFamily = Lalezar,
                                fontSize = 20.sp
                            )
                        }


                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)) {
                            items(filtered) { student ->
                                StudentRow(student,
                                    onEditClick = {
                                        studentToEdit.value = it
                                        fullName.value = it.fullName
                                        enrollment.value = it.enrollment
                                        showAddDialog.value = true
                                    },
                                    onDeleteClick = {
                                        students.remove(it)
                                    },
                                    presentIds = presentIds,
                                    absentIds = absentIds,
                                    selectedDate = selectedDate.value,
                                    attendanceAlreadyTaken = attendanceAlreadyTaken.value
                                    )
                            }
                        }
                    }
                }
            }



            // Bottom buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 22.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (selectedDate.value.isEmpty()) {
                            Toast.makeText(context, "Please choose a date first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val attendanceList = students.map { student ->
                            val status = when {
                                presentIds.contains(student.studentId) -> "P"
                                absentIds.contains(student.studentId) -> "A"
                                else -> ""
                            }

                            AttendanceData( // new add
                                classroomId = cid,
                                studentId = student.studentId,
                                date = selectedDate.value,
                                status = status,
                                fullName = student.fullName,
                                enrollment = student.enrollment,
                                userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                            )
                        }.filter { it.status.isNotBlank() }

                        if (attendanceList.isEmpty()) {
                            Toast.makeText(context, "Mark attendance first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        FirebaseDbHelper.saveAttendance(
                            attendanceList = attendanceList,
                            onSuccess = {
                                Toast.makeText(context, "Attendance saved", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.saveicon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save", color = Color.White, fontFamily = Laila, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }

                Button(
                    onClick = {showAddDialog.value = true},
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.maya)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.addicon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add", color = Color.White, fontFamily = Laila,fontWeight = FontWeight.SemiBold,fontSize = 16.sp)
                }
            }
        }
        /* ───────────────── Add-Student Dialog ───────────────── */
        if (showAddDialog.value) {
            Dialog(onDismissRequest = {
                showAddDialog.value = false
                studentToEdit.value = null
                fullName.value = ""
                enrollment.value = ""
            }) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 4.dp,
                    border = BorderStroke(2.dp, colorResource(R.color.maya)),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .widthIn(min = 260.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (studentToEdit.value == null) "Add a new Student" else "Edit Student",
                            fontFamily = Lalezar,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(16.dp))

                        /* Full name field */
                        OutlinedTextField(
                            value = fullName.value,
                            onValueChange = { fullName.value = it },
                            placeholder = { Text("Full Name", fontFamily = Laila,color = Color.Gray) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor =  colorResource(R.color.mygray),
                                focusedContainerColor =colorResource(R.color.mygray) ,
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        /* Enrollment field */
                        OutlinedTextField(
                            value = enrollment.value,
                            onValueChange = { enrollment.value = it },
                            placeholder = {
                                Text(
                                    "Enrollment No.",
                                    fontFamily = Laila,color = Color.Gray
                                )
                            }, // new added
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = colorResource(R.color.mygray),
                                focusedContainerColor =  colorResource(R.color.mygray),
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        /* Buttons row */
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Cancel button
                            Button(
                                onClick = {
                                    showAddDialog.value = false
                                    studentToEdit.value = null
                                    fullName.value = ""
                                    enrollment.value = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.width(120.dp)
                            ) {
                                Text("Cancel", color = Color.White, fontFamily = Laila, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    if (fullName.value.isBlank() || enrollment.value.isBlank()) return@Button

                                    if (studentToEdit.value == null) {
                                        // ── ADD MODE ──
                                        val newStudent = StudentData(
                                            fullName = fullName.value.trim(),
                                            enrollment = enrollment.value.trim(),
                                            classroomId = cid
                                        )
                                        FirebaseDbHelper.addStudent(
                                            student = newStudent,
                                            onSuccess = {
                                                FirebaseDbHelper.getStudentsByClassroom(
                                                    classroomId = cid,
                                                    onSuccess = { latestStudents ->
                                                        students.clear()
                                                        students.addAll(latestStudents)
                                                    },
                                                    onFailure = {
                                                        Toast.makeText(context, "Failed to refresh student list", Toast.LENGTH_SHORT).show()
                                                    }
                                                )
//                                                students.add(newStudent)
                                                showAddDialog.value = false
                                                fullName.value = ""
                                                enrollment.value = ""
                                            },
                                            onFailure = {  Toast.makeText(context, "Failed to add student", Toast.LENGTH_SHORT).show()}
                                        )
                                    } else {
                                        // ── EDIT MODE ──
                                        val updatedStudent = studentToEdit.value!!.copy(
                                            fullName = fullName.value.trim(),
                                            enrollment = enrollment.value.trim()
                                        )
                                        FirebaseDbHelper.updateStudent(
                                            student = updatedStudent,
                                            onSuccess = {
                                                val index = students.indexOfFirst { it.studentId == updatedStudent.studentId }
                                                if (index != -1) students[index] = updatedStudent
                                                showAddDialog.value = false
                                                studentToEdit.value = null
                                                fullName.value = ""
                                                enrollment.value = ""
                                            },
                                            onFailure = { Toast.makeText(context, "Failed to update student", Toast.LENGTH_SHORT).show() }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.maya)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.width(120.dp)
                            ) {
                                Text(
                                    text = if (studentToEdit.value == null) "Add" else "Update",
                                    color = Color.White,
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



