package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.CurrentAffair
import com.example.model.MonthlyCurrentAffairsPdf
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentAffairsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isTelugu by remember { mutableStateOf(true) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val activeApp = SampleData.getActiveApp()
    val caList = SampleData.getCurrentAffairsForApp(activeApp.id)
    val pdfList = SampleData.getPdfsForApp(activeApp.id)

    val categories = remember(caList) {
        listOf("ALL") + caList.map { it.category }.distinct()
    }

    val filteredCaList = remember(caList, selectedCategoryFilter) {
        if (selectedCategoryFilter == "ALL") caList else caList.filter { it.category == selectedCategoryFilter }
    }

    AppLuminousBackground {
        Scaffold(
            topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "📰 Current Affairs & Digests",
                            color = AppTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = activeApp.name,
                            color = Color(0xFF2563EB),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppTextDark)
                    }
                },
                actions = {
                    TextButton(onClick = { isTelugu = !isTelugu }) {
                        Text(if (isTelugu) "తెలుగు" else "English", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
            containerColor = Color.Transparent
        ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Monthly PDF Digests
            if (pdfList.isNotEmpty()) {
                item {
                    Text(
                        text = "📚 Monthly PDF Compilations",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                items(pdfList) { pdf ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEE2E2),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(26.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pdf.title,
                                    color = AppTextDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${pdf.pagesCount} Pages • ${pdf.language} • ${pdf.fileSizeBytes}",
                                    color = AppTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "📥 Opening '${pdf.title}' PDF Document", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Read PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section 2: Current Affairs Category Filters
            item {
                Column {
                    Text(
                        text = "⚡ Daily & Monthly Newsfeed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = category == selectedCategoryFilter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryFilter = category },
                                label = { Text(category, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2563EB),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Section 3: Current Affairs Articles
            items(filteredCaList) { ca ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AppLightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Text(
                                    text = ca.category,
                                    color = Color(0xFF2563EB),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(text = ca.publishedDate, color = AppTextMuted, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = ca.title,
                            color = AppTextDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = ca.content,
                            color = AppTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
}
