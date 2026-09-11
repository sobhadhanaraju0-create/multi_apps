package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.SampleData
import com.example.model.CouponDiscountType
import com.example.model.CouponModel
import com.example.model.PromotionStatus
import com.example.model.SubscriptionPlanModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val plans = SampleData.subscriptionPlansState
    val coupons = SampleData.couponsState
    val posters = SampleData.postersState.filter { it.getStatus() == PromotionStatus.ACTIVE }

    var selectedPlan by remember(plans) { mutableStateOf(plans.firstOrNull { it.isPopular } ?: plans.first()) }
    var appliedCoupon by remember { mutableStateOf<CouponModel?>(null) }
    var couponInputCode by remember { mutableStateOf("") }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    // Calculate final price with discount
    val originalPrice = selectedPlan.promotionalPrice ?: selectedPlan.price
    val discountAmount = appliedCoupon?.calculateDiscount(originalPrice) ?: 0.0
    val finalPayablePrice = (originalPrice - discountAmount).coerceAtLeast(0.0)

    fun applyCouponCode(codeToTry: String) {
        val trimmed = codeToTry.trim().uppercase()
        val foundCoupon = coupons.find { it.code.equals(trimmed, ignoreCase = true) }

        if (foundCoupon == null) {
            Toast.makeText(context, "❌ Invalid Coupon Code '$trimmed'", Toast.LENGTH_SHORT).show()
            return
        }

        val status = foundCoupon.getStatus()
        if (status != PromotionStatus.ACTIVE) {
            Toast.makeText(context, "⚠️ Coupon '$trimmed' is currently $status and cannot be applied.", Toast.LENGTH_LONG).show()
            return
        }

        if (foundCoupon.minPurchaseAmount > 0 && originalPrice < foundCoupon.minPurchaseAmount) {
            Toast.makeText(context, "⚠️ Coupon '$trimmed' requires a minimum plan purchase of ₹${foundCoupon.minPurchaseAmount.toInt()}.", Toast.LENGTH_LONG).show()
            return
        }

        if (foundCoupon.restrictedPlanIds.isNotEmpty() && !foundCoupon.restrictedPlanIds.contains(selectedPlan.id)) {
            Toast.makeText(context, "⚠️ Coupon '$trimmed' is not applicable to '${selectedPlan.name}'.", Toast.LENGTH_LONG).show()
            return
        }

        appliedCoupon = foundCoupon
        couponInputCode = foundCoupon.code
        Toast.makeText(context, "🎉 Coupon '${foundCoupon.code}' Applied! Saved ₹${discountAmount.toInt()}.", Toast.LENGTH_SHORT).show()
    }

    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = AppOrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Razorpay Secure Checkout", color = AppTextDark, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, Color(0xFF2563EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(selectedPlan.name, color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Duration: ${selectedPlan.duration}", color = AppTextMuted, fontSize = 11.sp)
                                }
                                Text("₹${originalPrice.toInt()}", color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            if (appliedCoupon != null) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AppLightBorder)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Coupon (${appliedCoupon!!.code})", color = Color(0xFF16A34A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("- ₹${discountAmount.toInt()}", color = Color(0xFF16A34A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AppLightBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL PAYABLE", color = AppTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Text("${selectedPlan.currencySymbol}${finalPayablePrice.toInt()}", color = AppOrangePrimary, fontWeight = FontWeight.Black, fontSize = 22.sp)
                            }
                        }
                    }

                    Text(
                        "🔒 Locked In-App Payment via Razorpay UPI / Credit / Debit Cards.\nInstant Pro Pass Activation.",
                        color = AppTextSecondary,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCheckoutDialog = false
                        Toast.makeText(context, "⚡ Payment of ${selectedPlan.currencySymbol}${finalPayablePrice.toInt()} Successful! ${selectedPlan.name} Active.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppOrangePrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("PAY ${selectedPlan.currencySymbol}${finalPayablePrice.toInt()} VIA RAZORPAY", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckoutDialog = false }) {
                    Text("Cancel", color = AppTextSecondary)
                }
            }
        )
    }

    AppLuminousBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "👑 Pro Subscription Plans",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Promotional Banner Carousel
            if (posters.isNotEmpty()) {
                item {
                    Column {
                        Text("🔥 Special Festive Offers & Launches", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(posters) { poster ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, AppLightBorder),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.width(300.dp).height(120.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        PosterFhdCardImage(
                                            poster = poster,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, Color(0xFF0F172A).copy(alpha = 0.85f))
                                                )
                                            )
                                        )
                                        Column(
                                            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
                                        ) {
                                            Text(poster.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(poster.promotionalText, color = AppOrangePrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Coupon Code Application Box
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AppLightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Have a Coupon or Festival Code?", color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = couponInputCode,
                                onValueChange = { couponInputCode = it.uppercase() },
                                placeholder = { Text("Enter Code (e.g. DIWALI50)", color = AppTextMuted, fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = AppLightBorder,
                                    focusedTextColor = AppTextDark,
                                    unfocusedTextColor = AppTextDark
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = { applyCouponCode(couponInputCode) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppOrangePrimary, contentColor = Color.White)
                            ) {
                                Text("APPLY", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }

                        // Active Quick Coupon Chips
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Active Offers:", color = AppTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(coupons.filter { it.getStatus() == PromotionStatus.ACTIVE }) { coup ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (appliedCoupon?.id == coup.id) Color(0xFF2563EB) else Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, if (appliedCoupon?.id == coup.id) Color(0xFF2563EB) else Color(0xFFBFDBFE)),
                                    modifier = Modifier.clickable { applyCouponCode(coup.code) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(coup.code, color = if (appliedCoupon?.id == coup.id) Color.White else Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            if (coup.discountType == CouponDiscountType.PERCENTAGE) "${coup.discountValue.toInt()}% OFF" else "₹${coup.discountValue.toInt()} OFF",
                                            color = if (appliedCoupon?.id == coup.id) Color.White else AppOrangePrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Select your preferred subscription pass below:",
                    color = AppTextSecondary,
                    fontSize = 12.sp
                )
            }

            items(plans) { pkg ->
                val isSelected = selectedPlan.id == pkg.id

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFF2563EB) else AppLightBorder
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
                    modifier = Modifier.fillMaxWidth().clickable { selectedPlan = pkg }
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(pkg.name, color = AppTextDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    if (pkg.isPopular) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.Default.Star, contentDescription = null, tint = AppOrangePrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text("Billing Cycle: ${pkg.duration}", color = AppTextMuted, fontSize = 12.sp)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val pkgOriginalPrice = pkg.promotionalPrice ?: pkg.price
                                Text(
                                    "${pkg.currencySymbol}${pkgOriginalPrice.toInt()}",
                                    color = AppOrangePrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                )
                                if (pkg.promotionalPrice != null) {
                                    Text("Regular ₹${pkg.price.toInt()}", color = AppTextMuted, fontSize = 10.sp)
                                }
                            }
                        }

                        if (pkg.introductoryOfferText != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFEDE5),
                                border = BorderStroke(1.dp, Color(0xFFFFD5C2))
                            ) {
                                Text(pkg.introductoryOfferText, color = AppOrangePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = AppLightBorder)

                        pkg.features.forEach { feat ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(feat, color = AppTextSecondary, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "🎉 3-Day Free Trial Activated for ${pkg.name}!", Toast.LENGTH_LONG).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF2563EB)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("3-Day Trial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    selectedPlan = pkg
                                    showCheckoutDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppOrangePrimary, contentColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Select & Pay", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
}
