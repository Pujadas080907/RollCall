

@file:OptIn(ExperimentalFoundationApi::class)

package com.example.rollcall.studentdata

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rollcall.R
import com.example.rollcall.firebasedatabase.FirebaseDbHelper
import com.example.rollcall.firebasedatabase.StudentData
import com.example.rollcall.ui.theme.Laila
import com.example.rollcall.ui.theme.Lalezar

@Composable
fun StudentRow(
    student: StudentData,
    onEditClick: (StudentData) -> Unit = {},
    onDeleteClick: (StudentData) -> Unit = {},
    presentIds: SnapshotStateList<String>,
    absentIds: SnapshotStateList<String>,
    selectedDate: String,
    attendanceAlreadyTaken: Boolean
) {
    val context = LocalContext.current

    val isPresent = remember { derivedStateOf { presentIds.contains(student.studentId) } }
    val isAbsent = remember { derivedStateOf { absentIds.contains(student.studentId) } }

    var menuOpen by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val studentToDelete = remember { mutableStateOf<StudentData?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { menuOpen = true }
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            colorResource(R.color.maya),
                            colorResource(R.color.prem),
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.studenticon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = student.fullName,
                    color = Color.White,
                    fontFamily = Laila,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                Text(
                    text = student.enrollment,
                    color = Color.White,
                    fontFamily = Laila,
                    fontSize = 13.sp
                )
            }

            Text("P", color = Color.White, fontFamily = Laila)

            Checkbox(
                checked = isPresent.value,
                onCheckedChange = {
                    if (!attendanceAlreadyTaken) {
                        if (it) {
                            if (!presentIds.contains(student.studentId)) presentIds.add(student.studentId)
                            absentIds.remove(student.studentId)
                        } else {
                            presentIds.remove(student.studentId)
                        }
                    }
                },
                enabled = !attendanceAlreadyTaken,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.White,
                    checkmarkColor = Color.Green
                )
            )

            Text("A", color = Color.White, fontFamily = Laila)

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = if (isAbsent.value) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .border(2.dp, Color.White, RoundedCornerShape(2.dp))
                    .clickable(enabled = !attendanceAlreadyTaken) {
                        if (!isAbsent.value) {
                            if (!absentIds.contains(student.studentId)) absentIds.add(student.studentId)
                            presentIds.remove(student.studentId)
                        } else {
                            absentIds.remove(student.studentId)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isAbsent.value) {
                    Icon(
                        painter = painterResource(R.drawable.cross),
                        contentDescription = "absent",
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .wrapContentSize(Alignment.CenterStart)
            ) {
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(R.drawable.edit),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Edit", fontSize = 15.sp, fontFamily = Laila)
                            }
                        },
                        onClick = {
                            menuOpen = false
                            onEditClick(student)
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(R.drawable.dlt),
                                    contentDescription = null,
                                    modifier = Modifier.size(23.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Delete", color = Color.Red, fontSize = 15.sp, fontFamily = Laila)
                            }
                        },
                        onClick = {
                            menuOpen = false
                            studentToDelete.value = student
                            showDialog.value = true
                        }
                    )
                }
            }
        }
    }

    if (showDialog.value && studentToDelete.value != null) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White,
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Delete Student", fontFamily = Lalezar, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            },
            text = {
                Text(
                    "Are you sure you want to delete this student?",
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
                        onClick = { showDialog.value = false },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.maya)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = Color.White, fontFamily = Laila)
                    }

                    Button(
                        onClick = {
                            FirebaseDbHelper.deleteStudent(
                                student = studentToDelete.value!!,
                                onSuccess = {
                                    showDialog.value = false
                                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                    onDeleteClick(studentToDelete.value!!)
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
}
