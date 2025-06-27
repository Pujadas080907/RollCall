package com.example.rollcall.Reports

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rollcall.R
import com.example.rollcall.firebasedatabase.AttendanceData
import com.example.rollcall.firebasedatabase.ClassroomData
import com.example.rollcall.firebasedatabase.FirebaseDbHelper
import com.example.rollcall.firebasedatabase.StudentData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import com.example.rollcall.ui.theme.Laila
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import com.example.rollcall.ui.theme.Lalezar
import com.google.firebase.firestore.DocumentReference
import java.text.SimpleDateFormat
import java.util.Locale


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EditReportPage(
//    navController: NavController,
//    classroom: ClassroomData,
//    date: String
//) {
//    val context = LocalContext.current
//    val attendanceList = remember { mutableStateListOf<AttendanceData>() }
//    val isLoading = remember { mutableStateOf(true) }
//    val isEdited = remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        FirebaseDbHelper.getAttendanceForDate(
//            classroomId = classroom.id,
//            date = date,
//            onSuccess = { records ->
//                FirebaseDbHelper.getStudentsByClassroom(
//                    classroomId = classroom.id,
//                    onSuccess = { currentStudents ->
//                        val studentMap = currentStudents.associateBy { it.studentId }
//                        val validRecords = records.mapNotNull { record ->
//                            studentMap[record.studentId]?.let {
//                                record.copy(
//                                    fullName = it.fullName,
//                                    enrollment = it.enrollment
//                                )
//                            }
//                        }
//                        attendanceList.clear()
//                        attendanceList.addAll(validRecords)
//                        isLoading.value = false
//                    },
//                    onFailure = {
//                        Toast.makeText(context, "Failed to load students", Toast.LENGTH_SHORT).show()
//                        isLoading.value = false
//                    }
//                )
//            },
//            onFailure = {
//                Toast.makeText(context, "Failed to load attendance", Toast.LENGTH_SHORT).show()
//                isLoading.value = false
//            }
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Edit Attendance", fontFamily = Laila) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            if (isEdited.value) {
//                Button(
//                    onClick = {
//
//                            val db = Firebase.firestore
//                            val collection = db.collection("attendance")
//
//                            val updatesDone = mutableStateOf(0)
//                            val totalRecords = attendanceList.size
//                            val refsToUpdate = mutableListOf<Pair<DocumentReference, String>>()
//
//                            attendanceList.forEach { record ->
//                                collection
//                                    .whereEqualTo("classroomId", classroom.id)
//                                    .whereEqualTo("studentId", record.studentId)
//                                    .whereEqualTo("date", date)
//                                    .get()
//                                    .addOnSuccessListener { snapshot ->
//                                        val docRef = snapshot.documents.firstOrNull()?.reference
//                                        if (docRef != null) {
//                                            refsToUpdate.add(docRef to record.status)
//                                        }
//
//                                        updatesDone.value++
//                                        if (updatesDone.value == totalRecords) {
//                                            val batch = db.batch()
//                                            refsToUpdate.forEach { (ref, newStatus) ->
//                                                batch.update(ref, "status", newStatus)
//                                            }
//
//                                            batch.commit()
//                                                .addOnSuccessListener {
//                                                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
//                                                    isEdited.value = false
//                                                }
//                                                .addOnFailureListener {
//                                                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
//                                                }
//                                        }
//                                    }
//                                    .addOnFailureListener {
//                                        Toast.makeText(context, "Failed to fetch record", Toast.LENGTH_SHORT).show()
//                                    }
//                            }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(12.dp)
//                ) {
//                    Text("Update", fontFamily = Laila)
//                }
//            }
//        }
//    ) { innerPadding ->
//        if (isLoading.value) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else {
//            LazyColumn(modifier = Modifier.padding(innerPadding)) {
//                items(attendanceList) { record ->
//                    val bgColor = if (record.status == "P") colorResource(R.color.presentcolor) else colorResource(R.color.absentcolor)
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp, horizontal = 12.dp)
//                            .background(bgColor, shape = RoundedCornerShape(6.dp))
//                            .clickable {
//                                val i = attendanceList.indexOf(record)
//                                if (i != -1) {
//                                    val updated = record.copy(status = if (record.status == "P") "A" else "P")
//                                    attendanceList[i] = updated
//                                    isEdited.value = true
//                                }
//                            }
//                            .padding(16.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text("${record.fullName} | ${record.enrollment}", fontFamily = Laila)
//                            Text(record.status, fontWeight = FontWeight.Bold, fontFamily = Laila)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReportPage(
    navController: NavController,
    classroom: ClassroomData,
    date: String
) {
    val context = LocalContext.current
    val attendanceList = remember { mutableStateListOf<AttendanceData>() }
    val isLoading = remember { mutableStateOf(true) }
    val isEdited = remember { mutableStateOf(false) }

    val monthYear = remember(date) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateObj = sdf.parse(date)
            SimpleDateFormat("MMMM - yyyy", Locale.getDefault()).format(dateObj!!)
        } catch (e: Exception) {
            "Edit Attendance"
        }
    }

    LaunchedEffect(Unit) {
        FirebaseDbHelper.getAttendanceForDate(
            classroomId = classroom.id,
            date = date,
            onSuccess = { records ->
                FirebaseDbHelper.getStudentsByClassroom(
                    classroomId = classroom.id,
                    onSuccess = { currentStudents ->
                        val studentMap = currentStudents.associateBy { it.studentId }
                        val validRecords = records.mapNotNull { record ->
                            studentMap[record.studentId]?.let {
                                record.copy(
                                    fullName = it.fullName,
                                    enrollment = it.enrollment
                                )
                            }
                        }.sortedBy { it.enrollment.lowercase() }
                        attendanceList.clear()
                        attendanceList.addAll(validRecords)
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

    Scaffold(
        topBar = {
            Surface(
                color = colorResource(id = R.color.prem),
                tonalElevation = 4.dp,
                modifier = Modifier.defaultMinSize(minHeight = 50.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
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
        bottomBar = {
            if (isEdited.value) {
                Button(
                    onClick = {
                        val db = Firebase.firestore
                        val collection = db.collection("attendance")
                        val updatesDone = mutableStateOf(0)
                        val totalRecords = attendanceList.size
                        val refsToUpdate = mutableListOf<Pair<DocumentReference, String>>()

                        attendanceList.forEach { record ->
                            collection
                                .whereEqualTo("classroomId", classroom.id)
                                .whereEqualTo("studentId", record.studentId)
                                .whereEqualTo("date", date)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    val docRef = snapshot.documents.firstOrNull()?.reference
                                    if (docRef != null) {
                                        refsToUpdate.add(docRef to record.status)
                                    }
                                    updatesDone.value++
                                    if (updatesDone.value == totalRecords) {
                                        val batch = db.batch()
                                        refsToUpdate.forEach { (ref, newStatus) ->
                                            batch.update(ref, "status", newStatus)
                                        }
                                        batch.commit()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                                                isEdited.value = false
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text("Update", fontFamily = Laila, color = Color.White)
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(R.color.maya))
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(attendanceList) { record ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                            .background(
                                color = if (record.status == "P") colorResource(R.color.presentcolor)
                                else colorResource(R.color.absentcolor),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clickable {
                                val i = attendanceList.indexOf(record)
                                if (i != -1) {
                                    val updated = record.copy(status = if (record.status == "P") "A" else "P")
                                    attendanceList[i] = updated
                                    isEdited.value = true
                                }
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
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
