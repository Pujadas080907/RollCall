package com.example.rollcall.studentdata

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rollcall.R
import com.example.rollcall.firebasedatabase.StudentData
import com.example.rollcall.ui.theme.Laila

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentRow(
    student: StudentData,
    onEditClick: (StudentData) -> Unit = {},
    onDeleteClick: (StudentData) -> Unit = {}
    ) {


    var isPresent by remember { mutableStateOf(false) }
    var isAbsent  by remember { mutableStateOf(false) }
    var menuOpen  by remember { mutableStateOf(false) }
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
                    student.fullName,
                    color = Color.White,
                    fontFamily = Laila,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                Text(
                    student.enrollment,
                    color = Color.White,
                    fontFamily = Laila,
                    fontSize = 13.sp
                )
            }
            Text(
                "P",
                color = Color.White,
                fontFamily = Laila,
                )
            Checkbox(
                checked = isPresent,
                onCheckedChange = {
                    isPresent = it
                    if (it) isAbsent = false
                },
                colors = CheckboxDefaults.colors(
                    checkedColor   = Color.White,
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
                        color  = if (isAbsent) Color.White else Color.Transparent,
                        shape  = RoundedCornerShape(2.dp)
                    )
                    .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(2.dp))
                    .clickable {
                        isAbsent = !isAbsent
                        if (isAbsent) isPresent = false
                    },

                contentAlignment = Alignment.Center
            ) {
                if (isAbsent) {
                    Icon(
                        painter = painterResource(R.drawable.cross),
                        contentDescription = "absent",
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))

        }
        /* ───── Long-press dropdown  ───── */
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .wrapContentSize(Alignment.CenterStart)
        ) {
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },

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
                            Text(
                                "Delete",
                                color = Color.Red,
                                fontSize = 15.sp,
                                fontFamily = Laila
                            )
                        }
                    },
                    onClick = {
                        menuOpen = false
                        onDeleteClick(student)
                    }
                )
            }
        }
    }
}
