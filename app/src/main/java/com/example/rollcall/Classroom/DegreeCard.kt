
package com.example.rollcall.Classroom

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rollcall.Database.Degree
import com.example.rollcall.Database.DegreeViewModel
import com.example.rollcall.R
import com.example.rollcall.ui.theme.laila


@Composable
fun DegreeCard(degree: Degree, degreeViewModel: DegreeViewModel, navController: NavController, onEditClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }


    val context = LocalContext.current


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this batch?") },
            confirmButton = {
                Button(
                    onClick = {

                        isDeleting = true
                        degreeViewModel.deleteDegree(degree)
                        Toast.makeText(context, "Degree deleted", Toast.LENGTH_SHORT).show()
                        showDialog = false
                    },
                    modifier = Modifier
                        .padding(start = 100.dp, end = 15.dp)
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .shadow(10.dp, RoundedCornerShape(15.dp))
            .border(1.dp, color = colorResource(id = R.color.main_color), RoundedCornerShape(15.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(id = R.color.cardStart_color),
                        colorResource(id = R.color.cardEnd_color)
                    )
                ),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable {
                navController.navigate("degreeDetail/${degree.id}/${degree.degreeName}/${degree.year}/${degree.section}")

                }
            .padding(10.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.degree_icon),
                contentDescription = "Degree Icon",
                modifier = Modifier
                    .size(52.dp)
                    .padding(bottom = 2.dp)
            )
            Text(
                text = degree.degreeName,
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = laila,
                fontWeight = FontWeight.ExtraBold,
            )
            
            Text(
                text = "${degree.year.ordinalSuffix()} Year / Sec: ${degree.section}",
                fontSize = 12.sp,
                color = Color.Black,
                fontFamily = laila,
                fontWeight = FontWeight.Medium,
            )

        }

        Spacer(modifier = Modifier.height(3.dp))
        // More options menu
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { showMenu = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.Gray,
                modifier = Modifier
                    .size(24.dp)
            )

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        showMenu = false
                        onEditClick()
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
                        showDialog = true
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
    }
}

fun Int.ordinalSuffix(): String {
    return when {
        this % 100 in 11..13 -> "${this}th"
        this % 10 == 1 -> "${this}st"
        this % 10 == 2 -> "${this}nd"
        this % 10 == 3 -> "${this}rd"
        else -> "${this}th"
    }
}