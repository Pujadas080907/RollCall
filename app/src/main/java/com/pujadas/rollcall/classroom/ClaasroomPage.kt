package com.pujadas.rollcall.classroom


import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pujadas.rollcall.authentication.AuthViewModel
import com.pujadas.rollcall.firebasedatabase.ClassroomData
import com.pujadas.rollcall.firebasedatabase.FirebaseDbHelper
import com.pujadas.rollcall.navGraph.Routes
import com.pujadas.rollcall.ui.theme.Laila
import com.pujadas.rollcall.ui.theme.Lalezar
import com.pujadas.rollcall.ui.theme.topbarfont
import com.google.firebase.auth.FirebaseAuth
import com.pujadas.rollcall.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomPage(navController: NavController,authViewModel: AuthViewModel) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val isEditMode        = remember { mutableStateOf(false) }
    val editingClassroom  = remember { mutableStateOf<ClassroomData?>(null) }

    val authState = authViewModel.authState.observeAsState()

    val degree = remember { mutableStateOf("") }
    val yearExpanded = remember { mutableStateOf(false) }
    val selectedYear = remember { mutableStateOf("Choose Year") }
    val secExpanded = remember { mutableStateOf(false) }
    val selectedSec = remember { mutableStateOf("Choose Sec") }

    val context = LocalContext.current
    val classrooms = remember { mutableStateListOf<ClassroomData>() }
    val showDialog = remember { mutableStateOf(false) }
    val showLogoutDialog = remember { mutableStateOf(false) }
    val classroomToDelete = remember { mutableStateOf<ClassroomData?>(null) }
    val searchQuery = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(true) }


    val filteredClassrooms = classrooms.filter {
        val query = searchQuery.value.trim().lowercase()
        query.isEmpty() || listOf(
            it.degree.lowercase(),
            it.year.lowercase(),
            it.section.lowercase()
        ).any { field -> field.contains(query) }
    }



    LaunchedEffect(Unit) {
        isLoading.value = true
        FirebaseDbHelper.getAllDegreeDetails(
            onSuccess = {
                classrooms.clear()
                classrooms.addAll(it)
                isLoading.value = false
            },
            onFailure = { e ->
                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading.value = false
            }
        )
    }


    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
                isEditMode.value = false
                editingClassroom.value = null
            },
            containerColor = colorResource(R.color.maya),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val fieldModifier = Modifier.fillMaxWidth().height(60.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEditMode.value) "Edit Batch" else "Add new a batch",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = Lalezar
                )

                Spacer(Modifier.height(6.dp))

                OutlinedTextField(
                    value = degree.value,
                    onValueChange = { degree.value = it },
                    placeholder = { Text("Enter Degree", fontFamily = Laila, color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.degreeicon),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedTextColor = Color.Gray,
                        focusedTextColor = Color.Gray,
                    )
                )

                Spacer(Modifier.height(6.dp))

                // Year Dropdown
                ExposedDropdownMenuBox(
                    expanded = yearExpanded.value,
                    onExpandedChange = { yearExpanded.value = !yearExpanded.value },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedYear.value,
                        onValueChange = {},
                        label = {},
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.yearicon),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = fieldModifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedTextColor = Color.Gray,
                            focusedTextColor = Color.Gray,
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = yearExpanded.value,
                        onDismissRequest = { yearExpanded.value = false }
                    ) {
                        listOf(
                            "1st Year",
                            "2nd Year",
                            "3rd Year",
                            "4th Year",
                            "5th Year"
                        ).forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year, fontFamily = Laila) },
                                onClick = {
                                    selectedYear.value = year
                                    yearExpanded.value = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Section Dropdown
                ExposedDropdownMenuBox(
                    expanded = secExpanded.value,
                    onExpandedChange = { secExpanded.value = !secExpanded.value },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedSec.value,
                        onValueChange = {},
                        label = {},
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.secicon),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = fieldModifier.menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedTextColor = Color.Gray,
                            focusedTextColor = Color.Gray,
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = secExpanded.value,
                        onDismissRequest = { secExpanded.value = false }
                    ) {
                        listOf("A", "B", "C").forEach { sec ->
                            DropdownMenuItem(
                                text = { Text(sec, fontFamily = Laila) },
                                onClick = {
                                    selectedSec.value = sec
                                    secExpanded.value = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            showBottomSheet.value = false
                            isEditMode.value = false
                            editingClassroom.value = null
                        },
                        modifier = Modifier
                            .width(95.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem))
                    ) {
                        Text("Cancel", color = Color.White, fontFamily = Laila)
                    }

                    Button(
                        onClick = {

                            val userEmail = FirebaseAuth.getInstance().currentUser?.email
                            if (
                                degree.value.isNotBlank() &&
                                selectedYear.value != "Choose Year" &&
                                selectedSec.value != "Choose Sec" &&
                                !userEmail.isNullOrBlank()
                            ) {
                                val payload = ClassroomData(
                                    degree = degree.value,
                                    year = selectedYear.value,
                                    section = selectedSec.value,
                                    userEmail = userEmail,
                                    id = editingClassroom.value?.id ?: ""
                                )

                                if (isEditMode.value && editingClassroom.value != null) {
                                    FirebaseDbHelper.updateClassroomDetails(
                                        payload,
                                        onSuccess = {
                                            classrooms.indexOfFirst { it.id == payload.id }
                                                .takeIf { it != -1 }
                                                ?.let { idx ->
                                                    classrooms[idx] =
                                                        payload
                                                }
                                            Toast.makeText(
                                                context,
                                                "Classroom updated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            showBottomSheet.value = false
                                            isEditMode.value = false
                                            editingClassroom.value = null
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                "Update failed: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                } else {
                                    FirebaseDbHelper.saveDegreeDetails(
                                        payload,
                                        onSuccess = {
                                            classrooms.add(it)
                                            Toast.makeText(
                                                context,
                                                "Classroom added",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            showBottomSheet.value = false
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                "Error: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.width(95.dp).height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem))
                    ) {
                        Text(
                            if (isEditMode.value) "Update" else "Add",
                            color = Color.White,
                            fontFamily = Laila
                        )
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }

    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "RollCall",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = topbarfont
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showLogoutDialog.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.prem)),
                modifier = Modifier.height(80.dp)
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    degree.value = ""
                    selectedYear.value = "Choose Year"
                    selectedSec.value = "Choose Sec"
                    showBottomSheet.value = true
                    editingClassroom.value = null
                    showBottomSheet.value = true
                },
                containerColor = colorResource(R.color.prem),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.addicon),
                    contentDescription = "Add",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = Color.White
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                isLoading.value -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = colorResource(R.color.maya))
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Please wait...⏳ Data are fetching.",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Laila
                            )
                        }
                    }

                }

                classrooms.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.emptyclassroom),
                            contentDescription = null,
                            modifier = Modifier.height(280.dp).padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = "Click on the ➕ button to get started!",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Laila
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
                                value = searchQuery.value,
                                onValueChange = { searchQuery.value = it },
                                placeholder = {
                                    Text("Search by Degree/Year/Sec", color = Color.Gray, fontFamily = Laila)
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

                    if (filteredClassrooms.isEmpty()) {
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
                                    text = "No matching batches found :(",
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Laila
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredClassrooms) { classroom ->
                                ClassroomCard(
                                    classroom,
                                    onEditClick = {selected ->
                                    degree.value = selected.degree
                                        selectedYear.value = selected.year
                                        selectedSec.value = selected.section
                                        editingClassroom.value = selected
                                        isEditMode.value = true
                                        showBottomSheet.value = true
                                    },
                                    onDeleteClick = { selected ->
                                        classroomToDelete.value = selected
                                        showDialog.value = true
                                    },
                                    onViewClick = { selected ->
                                        val route = Routes.degreeDetailPage.routes
                                            .replace("{degree}",  Uri.encode(selected.degree))
                                            .replace("{year}",    Uri.encode(selected.year))
                                            .replace("{section}", Uri.encode(selected.section))
                                            .replace("{cid}",     selected.id)
                                        navController.navigate(route)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDialog.value && classroomToDelete.value != null) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Delete  Batch",
                            fontFamily = Lalezar,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black

                        )
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete this batch?",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = Laila,
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { showDialog.value = false },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.maya)),
                            shape = RoundedCornerShape(10.dp)

                            ) {
                            Text("Cancel", color = Color.White, fontFamily = Laila)
                        }

                        Button(onClick = {
                            FirebaseDbHelper.deleteClassroom(
                                classroom = classroomToDelete.value!!,
                                onSuccess = {
                                    classrooms.remove(classroomToDelete.value)
                                    showDialog.value = false
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = {
                                    showDialog.value = false
                                    Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
                            shape = RoundedCornerShape(10.dp)
                            ) {
                            Text("Delete", color = Color.White, fontFamily = Laila)
                        }
                    }
                },
                dismissButton = {}
            )
        }
        if (showLogoutDialog.value) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog.value = false },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Logout",
                            fontFamily = Lalezar,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                },
                text = {
                    Text(
                        text = "Are you sure you want to logout?",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = Laila,
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showLogoutDialog.value = false },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.maya)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", color = Color.White, fontFamily = Laila)
                        }

                        Button(
                            onClick = {
                                showLogoutDialog.value = false
                                authViewModel.signout(context)
                                navController.navigate("login") {
                                    popUpTo("classroom") { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.prem)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Logout", color = Color.White, fontFamily = Laila)
                        }
                    }
                },
                dismissButton = {}
            )
        }


    }
}

@Composable
fun ClassroomCard(
    classroom: ClassroomData,
    onEditClick: (ClassroomData) -> Unit,
    onDeleteClick: (ClassroomData) -> Unit,
    onViewClick: (ClassroomData) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .width(158.dp)
            .height(193.dp)
            .clickable { onViewClick(classroom) }
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(colorResource(R.color.maya), colorResource(R.color.prem))
                    )
                )
        ) {
            // Top-right icon with dropdown trigger
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = "Options",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .clickable { expanded = true },
                    tint = Color.White
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(R.drawable.edit), contentDescription = null,
                                    Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit", fontSize = 15.sp, fontFamily = Laila)
                            }
                        },
                        onClick = {
                            expanded = false
                            onEditClick(classroom)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(R.drawable.dlt), contentDescription = null,
                                    Modifier.size(23.dp), tint = Color.Unspecified)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete", color = Color.Red, fontSize = 15.sp, fontFamily = Laila)
                            }
                        },
                        onClick = {
                            expanded = false
                            onDeleteClick(classroom)
                        }
                    )
                }

            }

            // Card content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cardimage),
                    contentDescription = "Icon",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(classroom.degree, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Laila)
                Text("${classroom.year} / Sec : ${classroom.section}", color = Color.White, fontSize = 12.sp, fontFamily = Laila)
            }
        }
    }
}
