package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(LoginStep.ENTER_PHONE) }
    var phoneNumber by remember { mutableStateOf("9876543210") }
    var otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    var remainingSeconds by remember { mutableStateOf(60) }

    // Countdown timer for OTP
    LaunchedEffect(currentStep, remainingSeconds) {
        if (currentStep == LoginStep.VERIFY_OTP && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F2FE), // Light sky blue
                        Color(0xFFF8FAFC),
                        Color(0xFFE2E8F0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            when (currentStep) {
                LoginStep.ENTER_PHONE -> {
                    PhoneLoginContent(
                        phoneNumber = phoneNumber,
                        onPhoneNumberChanged = { phoneNumber = it },
                        onContinueWithOtp = {
                            if (phoneNumber.length >= 10) {
                                currentStep = LoginStep.VERIFY_OTP
                                Toast.makeText(context, "OTP sent to +91 $phoneNumber", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onGoogleLogin = {
                            SampleData.sampleUserProfile = SampleData.sampleUserProfile.copy(
                                name = "APPSC Aspirant (Google)",
                                email = "aspirant@gmail.com",
                                isProSubscribed = true
                            )
                            Toast.makeText(context, "✅ Signed in with Google Successfully!", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        },
                        onBack = onBack
                    )
                }
                LoginStep.VERIFY_OTP -> {
                    OtpVerificationContent(
                        phoneNumber = phoneNumber,
                        otpValues = otpValues,
                        remainingSeconds = remainingSeconds,
                        onEditPhone = { currentStep = LoginStep.ENTER_PHONE },
                        onVerifyOtp = {
                            val enteredOtp = otpValues.joinToString("")
                            if (enteredOtp.length == 6 || enteredOtp.isBlank()) {
                                // Default or entered OTP success
                                SampleData.sampleUserProfile = SampleData.sampleUserProfile.copy(
                                    name = "APPSC Group-2 Aspirant",
                                    email = "aspirant.$phoneNumber@ap.gov.in",
                                    isProSubscribed = true,
                                    proPlanName = "APPSC VIP Aspirant"
                                )
                                Toast.makeText(context, "✅ OTP Verified Successfully!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, "Please enter the complete 6-digit OTP", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onResendOtp = {
                            remainingSeconds = 60
                            Toast.makeText(context, "🔄 OTP Resent successfully to +91 $phoneNumber", Toast.LENGTH_SHORT).show()
                        },
                        onNeedHelp = {
                            Toast.makeText(context, "Helpline: 1800-425-1111 (APPSC Support)", Toast.LENGTH_LONG).show()
                        },
                        onBack = { currentStep = LoginStep.ENTER_PHONE }
                    )
                }
            }
        }
    }
}

enum class LoginStep {
    ENTER_PHONE,
    VERIFY_OTP
}

@Composable
fun PhoneLoginContent(
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit,
    onContinueWithOtp: () -> Unit,
    onGoogleLogin: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header & Telugu Slogan
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(
                    text = "జ్ఞానమే జయానికి మార్గం",
                    color = Color(0xFF0F2C59),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Government Emblem Placeholder / Badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF15803D).copy(alpha = 0.15f),
                    border = BorderStroke(2.dp, Color(0xFF15803D)),
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "GOVERNMENT OF ANDHRA PRADESH",
                    color = Color(0xFF15803D),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "సత్యమేవ జయతే",
                    color = Color(0xFF15803D),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "APPSC\nGROUP-2",
                    color = Color(0xFF0F172A),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "A STEP TOWARDS\nA BRIGHTER ANDHRA PRADESH",
                    color = Color(0xFF475569),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FeatureBadgeItem(icon = Icons.Default.MenuBook, label = "PRACTICE")
                    Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFFCBD5E1))
                    FeatureBadgeItem(icon = Icons.Default.TrendingUp, label = "IMPROVE")
                    Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFFCBD5E1))
                    FeatureBadgeItem(icon = Icons.Default.EmojiEvents, label = "ACHIEVE")
                }
            }

            // Input and Login buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Phone input field with flag
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🇮🇳 +91", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(12.dp))
                        Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) onPhoneNumberChanged(it) },
                            placeholder = { Text("Enter your mobile number", color = Color(0xFF94A3B8)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Continue with OTP Button
                Button(
                    onClick = onContinueWithOtp,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2C59)),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Continue with OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                // OR Divider
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                    Text(text = "  OR  ", fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                }

                // Continue with Google Button
                OutlinedButton(
                    onClick = onGoogleLogin,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    elevation = ButtonDefaults.buttonElevation(1.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google Icon representation
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("G", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF2563EB))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Continue with Google", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                    }
                }

                // Footer Slogan
                Text(
                    text = "Andhra Pradesh Rises with Knowledge",
                    color = Color(0xFF0F2C59),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FeatureBadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF0F2C59), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2C59), letterSpacing = 0.5.sp)
    }
}

@Composable
fun OtpVerificationContent(
    phoneNumber: String,
    otpValues: MutableList<String>,
    remainingSeconds: Int,
    onEditPhone: () -> Unit,
    onVerifyOtp: () -> Unit,
    onResendOtp: () -> Unit,
    onNeedHelp: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }
                TextButton(onClick = onNeedHelp) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF0F2C59), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Need Help?", color = Color(0xFF0F2C59), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Center Content Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Header
                    Text(
                        text = "APPSC GROUP-2",
                        color = Color(0xFF0F172A),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "SERVE LEARN GROW FOR A BETTER ANDHRA PRADESH",
                        color = Color(0xFF64748B),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mobile Verification Icon Illustration
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0F2FE),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = Color(0xFF0F2C59),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Verify Your Mobile Number",
                        color = Color(0xFF0F172A),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "We have sent a 6-digit OTP to",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "+91 $phoneNumber",
                            color = Color(0xFF0F172A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(onClick = onEditPhone, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 6 OTP Digit Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 0 until 6) {
                            OutlinedTextField(
                                value = otpValues.getOrElse(i) { "" },
                                onValueChange = { newVal ->
                                    if (newVal.length <= 1 && newVal.all { char -> char.isDigit() }) {
                                        otpValues[i] = newVal
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF0F2C59),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                ),
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(52.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Resend Timer or Resend Button
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Didn't receive the OTP? ",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        if (remainingSeconds > 0) {
                            val formattedTime = String.format("00:%02d", remainingSeconds)
                            Text(
                                text = "Resend in $formattedTime",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        } else {
                            TextButton(
                                onClick = onResendOtp,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Resend OTP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Verify OTP Button
                    val isOtpComplete = otpValues.all { it.isNotBlank() && it.length == 1 && it.all { c -> c.isDigit() } }
                    Button(
                        onClick = onVerifyOtp,
                        enabled = isOtpComplete,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F2C59),
                            disabledContainerColor = Color(0xFF94A3B8).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        elevation = ButtonDefaults.buttonElevation(if (isOtpComplete) 4.dp else 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Verify OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isOtpComplete) Color.White else Color.White.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = if (isOtpComplete) Color.White else Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Bottom Quote
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "“Better Governance",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "A Brighter Andhra Pradesh”",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F2C59)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
