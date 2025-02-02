package com.example.rollcall.Classroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.rollcall.studentlists.Student
import com.example.rollcall.studentlists.StudentViewModel
import com.example.rollcall.studentlist.StudentBottomSheet
import com.example.rollcall.ui.theme.laila
import com.example.rollcall.ui.theme.title

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
                    IconButton(onClick = { /* More options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.White
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
                        StudentItem(student)
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
                onDismiss = { showSheet = false },
                onAddStudent = { name, enrollmentNo ->
                    studentViewModel.addStudent(name, enrollmentNo,degreeId,degreeYear,degreeSection)
                    showSheet = false
                }
            )
        }
    }
}
    @Composable
    fun StudentItem(student: Student) {
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
                    text = "${student.fullName} | ${student.enrollmentNo}",
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )
            }
        }
    }
