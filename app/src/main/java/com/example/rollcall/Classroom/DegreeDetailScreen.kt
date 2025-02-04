package com.example.rollcall.Classroom

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rollcall.R
import com.example.rollcall.studentlists.Student
import com.example.rollcall.studentlists.StudentViewModel
import com.example.rollcall.studentlist.StudentBottomSheet
import com.example.rollcall.ui.theme.laila
import com.example.rollcall.ui.theme.title
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegreeDetailScreen(
    navController: NavController,
    degreeId: Int,
    degreeName: String,
    degreeYear: Int,
    degreeSection: String,
    studentViewModel: StudentViewModel
) {
    var showSheet by remember { mutableStateOf(false) }
    var editedStudent by remember { mutableStateOf<Student?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    //val students by studentViewModel.students.collectAsState(initial = emptyList())
    val students by studentViewModel.getStudentsByDegreeYearSection(degreeId, degreeYear, degreeSection)
        .collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = degreeName,
                            fontSize = 20.sp,
                            color = Color.White,
                            fontFamily = title,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "${degreeYear.ordinalSuffix()} Year / Sec: $degreeSection",
                            fontSize = 14.sp,
                            color = Color.White,
                            fontFamily = laila,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                        if (selectedDate.isNotEmpty()) {
                            Text(
                                text = selectedDate,
                                fontSize = 12.sp,
                                color = Color.White,
                                fontFamily = laila,

                            )
                        }
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Report") },
                            onClick = { showMenu = false },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.report),
                                    contentDescription = "Report",
                                    modifier = Modifier
                                        .size(16.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Calendar") },
                            onClick = {
                                showMenu = false
                                showDatePicker(context) { selectedDate = it }
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = "Calendar",
                                    modifier = Modifier
                                        .size(15.dp)
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorResource(id = R.color.main_color),
                    titleContentColor = Color.White
                )
            )
        },

        containerColor = Color.White,
        floatingActionButton = {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Button(
                    onClick = { /* Save action */ },
                    modifier = Modifier
                        .padding(start = 25.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.main_color))
                ) {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                FloatingActionButton(
                    onClick = { showSheet = true },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    containerColor = colorResource(id = R.color.main_color)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus_icon),
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }


    ) { paddingValues ->

        Column(modifier = Modifier.padding(top = 16.dp)) {

            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No students found. Click \"+\" to add a new student.",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = laila,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(students) { student ->
                        StudentItem(
                            student,
                            onEditStudent = { selectedStudent ->
                                editedStudent = selectedStudent
                                showSheet = true
                            },
                            onDeleteStudent = { selectedStudent ->
                                studentViewModel.deleteStudent(selectedStudent)

                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item{
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }


        if (showSheet) {
            StudentBottomSheet(
                student = editedStudent,
                onDismiss = {
                    showSheet = false
                    editedStudent = null
                },
                onSaveStudent = { name, enrollmentNo ->
                    if (editedStudent == null) {
                        studentViewModel.addStudent(name, enrollmentNo, degreeId, degreeYear, degreeSection)
                    } else {
                        studentViewModel.updateStudent(
                            editedStudent!!.copy(fullName = name, enrollmentNo = enrollmentNo)
                        )
                    }
                    showSheet = false
                }
            )
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            val formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun StudentItem(
        student: Student,
        onEditStudent: (Student) -> Unit,
        onDeleteStudent: (Student) -> Unit) {

        var isPresent by remember { mutableStateOf(false) }
        var isAbsent by remember { mutableStateOf(false) }
        var showMenu by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            colorResource(id = R.color.cardStart_color),
                            colorResource(id = R.color.cardEnd_color)
                        )
                    ),
                )
                .border(1.dp, color = colorResource(id = R.color.main_color))
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showMenu = true }
                )

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.profile_image),
                        contentDescription = "Student Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }


                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = " ${student.fullName} \n ${student.enrollmentNo}",
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )

                Text(
                    text = "P",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = title
                )


                Checkbox(
                    checked = isPresent,
                    onCheckedChange = {
                        isPresent = it
                        if (it) isAbsent = false
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.Gray
                    )
                )

                Text(
                    text = "A",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = title
                )


                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(2.dp, Color.Gray, shape = RectangleShape)
                        .background(if (isAbsent) Color.Red else Color.Transparent)
                        .clickable {
                            isAbsent = !isAbsent
                            if (isAbsent) isPresent = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isAbsent) {
                        Icon(
                            painter = painterResource(id = R.drawable.img),
                            contentDescription = "Absent",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        showMenu = false
                        onEditStudent(student)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        showMenu = false
                        showDeleteDialog = true

                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                )
            }
        }
        // **Delete Confirmation Dialog**
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete this student?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteStudent(student)
                            showDeleteDialog = false
                        },
                        modifier = Modifier.padding(start = 100.dp, end = 15.dp)
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }





