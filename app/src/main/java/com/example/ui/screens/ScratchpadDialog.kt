package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ScratchpadDialog(onDismiss: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppOrangePrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Exam Scratchpad",
                    color = AppTextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, AppLightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (input.isEmpty()) "0" else input,
                            color = AppTextSecondary,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "= $result",
                            color = Color(0xFF2563EB),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                val buttons = listOf("7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "C", "0", "=", "+")

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    buttons.chunked(4).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { btn ->
                                val isOperator = btn in listOf("/", "*", "-", "+", "=")
                                Button(
                                    onClick = {
                                        when (btn) {
                                            "C" -> {
                                                input = ""
                                                result = "0"
                                            }
                                            "=" -> {
                                                result = try {
                                                    val doubleVal = input.toDoubleOrNull() ?: 0.0
                                                    "%.2f".format(doubleVal)
                                                } catch (e: Exception) {
                                                    "Ready"
                                                }
                                            }
                                            else -> {
                                                input += btn
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (btn == "=") AppOrangePrimary else if (isOperator) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                                        contentColor = if (btn == "=") Color.White else if (isOperator) Color(0xFF2563EB) else AppTextDark
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) {
                                    Text(btn, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
            }
        }
    )
}
