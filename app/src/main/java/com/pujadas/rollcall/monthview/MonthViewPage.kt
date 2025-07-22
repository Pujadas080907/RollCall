package com.pujadas.rollcall.monthview

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.pujadas.rollcall.classroom.BottomNavigationBar
import com.pujadas.rollcall.classroom.ClassroomCard
import com.pujadas.rollcall.firebasedatabase.ClassroomData
import com.pujadas.rollcall.firebasedatabase.FirebaseDbHelper
import com.pujadas.rollcall.navGraph.Routes
import com.pujadas.rollcall.ui.theme.Laila
import com.pujadas.rollcall.ui.theme.topbarfont
import com.pujadas.rollcall.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthViewPage(navController: NavController) {
    val context = LocalContext.current
    val classrooms = remember { mutableStateListOf<ClassroomData>() }
    val isLoading = remember { mutableStateOf(true) }
    val searchQuery = remember { mutableStateOf("") }

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
            onFailure = {
                Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                isLoading.value = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Monthly Records", // ✅ Title changed
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = topbarfont
                    )
                },
                // ✅ Removed logout icon
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.prem)),
                modifier = Modifier.height(80.dp)
            )
        },
        // ✅ Removed FloatingActionButton
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
            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            } else if (classrooms.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.emptyclassroom),
                        contentDescription = null,
                        modifier = Modifier
                            .height(280.dp)
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "No classrooms found",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Laila
                    )
                }
            } else {
                // 🔍 Search Bar
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
                                Text(
                                    "Search by Degree/Year/Sec",
                                    color = Color.Gray,
                                    fontFamily = Laila
                                )
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                                classroom = classroom,
                                onEditClick = {},
                                onDeleteClick = {},
                                onViewClick = { selected ->
                                    val route = Routes.monthDetailedViewPage.routes
                                        .replace("{degree}", Uri.encode(selected.degree))
                                        .replace("{year}", Uri.encode(selected.year))
                                        .replace("{section}", Uri.encode(selected.section))
                                        .replace("{cid}", selected.id)

                                    navController.navigate(route)
                                }

                            )
                        }
                    }
                }
            }
        }
    }
}
