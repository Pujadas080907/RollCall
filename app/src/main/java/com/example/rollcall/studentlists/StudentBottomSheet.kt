package com.example.rollcall.studentlist

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.rollcall.R
import com.example.rollcall.ui.theme.laila
import com.example.rollcall.ui.theme.title


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentBottomSheet(
    onDismiss: () -> Unit,
    onAddStudent: (String, String) -> Unit
) {
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var enrollmentNo by remember { mutableStateOf(TextFieldValue("")) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Full-width modal
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, color = colorResource(id = R.color.main_color),MaterialTheme.shapes.medium)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title
                Text(
                    text = "Add New Student",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontFamily = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center

                )

                Spacer(modifier = Modifier.height(16.dp))

                // Full Name Field
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = {
                        Text("Full Name",
                            color = Color.Gray,
                            fontFamily = laila,
                            fontWeight = FontWeight.Medium
                            ) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        focusedBorderColor = colorResource(id = R.color.main_color),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Enrollment No. Field
                OutlinedTextField(
                    value = enrollmentNo,
                    onValueChange = { enrollmentNo = it },
                    label = {
                        Text("Enrollment No.",
                            color = Color.Gray,
                            fontFamily = laila,
                            fontWeight = FontWeight.Medium
                        )
                            },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.Black,
                        focusedBorderColor = colorResource(id = R.color.main_color),
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons Row
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.main_color)),
                        modifier = Modifier.size(width = 100.dp, height = 45.dp)
                    ) {
                        Text(
                            "Cancel",
                            color = Color.White,
                            )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onAddStudent(fullName.text, enrollmentNo.text)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.main_color)),
                        modifier = Modifier.size(width = 100.dp, height = 45.dp)
                    ) {
                        Text("Add",
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}
