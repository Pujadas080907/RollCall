package com.example.rollcall.Classroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rollcall.Database.Degree
import com.example.rollcall.Database.DegreeViewModel
import com.example.rollcall.R
import com.example.rollcall.ui.theme.laila

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DegreeBottomSheet(
    degreeViewModel: DegreeViewModel,
    isEdit: Boolean = false,
    initialDegreeName: String = "",
    initialYear: Int? = null,
    initialSection: String? = null,
    onDismiss: () -> Unit,
    onDegreeUpdated: (Degree) -> Unit
) {
    var degreeName by remember { mutableStateOf(initialDegreeName) }
    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedSection by remember { mutableStateOf(initialSection) }

    val yearOptions = (1..5).toList()
    val sectionOptions = listOf("A", "B", "C", "D")

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isEdit) "Edit Batch Name" else "Add New Batch",
                fontSize = 20.sp,
                fontFamily = laila,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = degreeName,
                onValueChange = { degreeName = it },
                label = { Text("Enter Degree", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.Black,
                    focusedBorderColor = colorResource(id = R.color.main_color),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Year Dropdown
            var yearExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = yearExpanded,
                onExpandedChange = { yearExpanded = !yearExpanded }
            ) {
                OutlinedTextField(
                    value = selectedYear?.toString() ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Choose Year", color = Color.Gray) },
                    placeholder = { Text("Choose Year", color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        focusedBorderColor = colorResource(id = R.color.main_color),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = yearExpanded,
                    onDismissRequest = { yearExpanded = false }
                ) {
                    yearOptions.forEach { year ->
                        DropdownMenuItem(
                            text = { Text("Year $year") },
                            onClick = {
                                selectedYear = year
                                yearExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Section Dropdown
            var sectionExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = sectionExpanded,
                onExpandedChange = { sectionExpanded = !sectionExpanded }
            ) {
                OutlinedTextField(
                    value = selectedSection ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Choose Section",color = Color.Gray) },
                    placeholder = { Text("Choose Section",color = Color.Black) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        focusedBorderColor = colorResource(id = R.color.main_color),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = sectionExpanded,
                    onDismissRequest = { sectionExpanded = false }
                ) {
                    sectionOptions.forEach { section ->
                        DropdownMenuItem(
                            text = { Text("Section $section") },
                            onClick = {
                                selectedSection = section
                                sectionExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.main_color)),
                    modifier = Modifier.size(width = 100.dp, height = 45.dp)
                ) {
                    Text("Cancel", color = Color.White)
                }

                Button(
                    onClick = {
                        if (degreeName.isNotBlank() && selectedYear != null && selectedSection != null) {
                            val updatedDegree = Degree(
                                id = 0,
                                degreeName = degreeName,
                                year = selectedYear!!,
                                section = selectedSection!!
                              )
                            onDegreeUpdated(updatedDegree)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.main_color)),
                    modifier = Modifier.size(width = 100.dp, height = 45.dp)
                ) {
                    Text(if (isEdit) "Update" else "Add", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
