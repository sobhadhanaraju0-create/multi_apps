package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyTermsScreen(onBack: () -> Unit) {
    AppLuminousBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Privacy Policy & Terms",
                            color = AppTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTextDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, AppLightBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Privacy Policy for APPSC Group 2 MCQ Portal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = AppTextDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Effective Date: September 2026\n\n" +
                                "1. Data Collection & Privacy\n" +
                                "APPSC Group 2 MCQ Portal respects user privacy. We store user bookmarks, test progress scores, and subscription statuses securely.\n\n" +
                                "2. Third-Party Services\n" +
                                "We use Google Firebase for authentication and database synchronization. No personal sensitive financial details are shared with third parties.\n\n" +
                                "3. User Data Rights\n" +
                                "Users can clear local data at any time via the app settings or request account deletion through support.\n\n" +
                                "4. Terms of Service\n" +
                                "All question sets, explanations, and study materials provided within this app are intended strictly for educational and exam preparation purposes.",
                        fontSize = 14.sp,
                        color = AppTextSecondary,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = AppOrangePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("I Accept", fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
        }
    }
}
}
