
package com.example.rollcall.Classroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rollcall.Database.Degree
import com.example.rollcall.Database.DegreeViewModel
import com.example.rollcall.Navigation.Routes
import com.example.rollcall.R
import com.example.rollcall.ui.theme.laila
import com.example.rollcall.ui.theme.title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegreeScreen(navController: NavController, degreeViewModel: DegreeViewModel) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedDegree by remember { mutableStateOf<Degree?>(null) }

    val degreeList by degreeViewModel.degreeList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "RollCall",
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontFamily = title,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colorResource(id = R.color.main_color), titleContentColor = Color.White)
            )
        },
        bottomBar = { BottomNavigationBar(navController) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedDegree = null
                    showBottomSheet = true },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                containerColor = colorResource(id = R.color.main_color)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus_icon),
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (degreeList.isEmpty()) {
                Text(
                    text = "Click on the \"+\" button to get started!",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontFamily = laila,
                    fontWeight = FontWeight.Bold
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(degreeList) { degree ->
                        DegreeCard(degree, degreeViewModel, navController) {
                            selectedDegree = degree
                            showBottomSheet = true
                        }
                    }
                }
            }
        }
    }

    // Show the degree modal (Add or Edit)
    if (showBottomSheet) {
        DegreeBottomSheet(
            degreeViewModel = degreeViewModel,
            isEdit = selectedDegree != null,
            initialDegreeName = selectedDegree?.degreeName ?: "",
            onDismiss = {
                showBottomSheet = false
                selectedDegree = null
            },
            onDegreeUpdated = { updatedDegree ->
                if (selectedDegree != null) {

                    degreeViewModel.updateDegree(updatedDegree.copy(id = selectedDegree!!.id))
                } else {

                    degreeViewModel.addDegree(updatedDegree)
                }
                selectedDegree = null
                showBottomSheet = false
            }
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedIndex = remember { mutableStateOf(0) }
    val items = listOf(
        "Classroom" to R.drawable.classroom,
        "Month View" to R.drawable.month_view,
    )

    BottomAppBar(
        modifier = Modifier
            .height(55.dp)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
        containerColor = Color.LightGray,
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            IconButton(
                onClick = {
                    selectedIndex.value = index

                    when (index) {
                        0 -> navController.navigate(Routes.Degree.routes)
                        1 -> navController.navigate(Routes.MonthView.routes)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = item.second),
                        contentDescription = item.first,
                        modifier = Modifier.size(20.dp),
                        tint = if (selectedIndex.value == index) colorResource(id = R.color.main_color) else Color.Black
                    )
                    Text(
                        text = item.first,
                        fontSize = 12.sp,
                        fontFamily = laila,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedIndex.value == index) colorResource(id = R.color.main_color) else Color.Black
                    )
                }
            }
        }
    }
}
