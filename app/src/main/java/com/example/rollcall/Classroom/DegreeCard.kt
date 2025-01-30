
package com.example.rollcall.Classroom

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rollcall.Database.Degree
import com.example.rollcall.Database.DegreeViewModel
import com.example.rollcall.R
import com.example.rollcall.ui.theme.laila


@Composable
fun DegreeCard(degree: Degree, degreeViewModel: DegreeViewModel, onEditClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    // Get the context to show the Toast
    val context = LocalContext.current

    // Show confirmation dialog before deleting
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this batch?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Delete the degree if confirmed
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
            .clickable { }
            .padding(16.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = degree.degreeName,
            fontSize = 24.sp,
            color = Color.Black,
            fontFamily = laila,
            fontWeight = FontWeight.ExtraBold,
        )

        // More options menu
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clickable { showMenu = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
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
                        showDialog = true // Show the confirmation dialog
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
