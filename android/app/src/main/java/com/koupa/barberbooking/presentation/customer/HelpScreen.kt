package com.koupa.barberbooking.presentation.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val KoupaTeal = Color(0xFF1A7A78)
private val KoupaGold = Color(0xFFE1A553)
private val KoupaDarkSlate = Color(0xFF323E4B)
private val KoupaBackground = Color(0xFFF3F5F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onNavigateBack: () -> Unit) {
    var expandedItems by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "المساعدة والأسئلة الشائعة",
                        color = KoupaDarkSlate,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = KoupaDarkSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = KoupaBackground
                )
            )
        },
        containerColor = KoupaBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(faqItems.indices.toList()) { index ->
                val item = faqItems[index]
                val isExpanded = index in expandedItems

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Question header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedItems = if (isExpanded) {
                                        expandedItems - index
                                    } else {
                                        expandedItems + index
                                    }
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.question,
                                color = KoupaDarkSlate,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = KoupaTeal
                            )
                        }

                        // Answer content
                        AnimatedVisibility(visible = isExpanded) {
                            Text(
                                text = item.answer,
                                color = KoupaDarkSlate.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            )
                        }
                    }
                }
            }

            // Contact Support Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "اتصل بالدعم",
                            color = KoupaDarkSlate,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "الهاتف: +213 555 123 456",
                            color = KoupaDarkSlate,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "البريد الإلكتروني: support@koupa.com",
                            color = KoupaDarkSlate,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "واتساب: +213 555 123 456",
                            color = KoupaDarkSlate,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { /* TODO: Implement contact support action */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KoupaTeal,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "اتصل بالدعم",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class FaqItem(
    val question: String,
    val answer: String
)

private val faqItems = listOf(
    FaqItem(
        question = "كيف أحجز موعد؟",
        answer = "لحجز موعد، اتبع الخطوات التالية:\n1. اضف على زر 'حجز موعد' في الصفحة الرئيسية\n2. اختر الخدمة التي ترغب فيها\n3. اختر التاريخ والوقت المناسب لك\n4. تأكد من تفاصيل الحجز واضغط على 'تأكيد الحجز'\nستحصل على تأكيد الحجز عبر الإشعار والبريد الإلكتروني."
    ),
    FaqItem(
        question = "كيف ألغي حجزي؟",
        answer = "لإلغاء حجزك:\n1. اذهب إلى قسم 'حجوزاتي' من القائمة الرئيسية\n2. ابحث عن الحجز الذي ترغب في إلغائه\n3. اضغط على الحجز لعرض التفاصيل\n4. اضغط على زر 'إلغاء الحجز'\n5. أكد الإلغاء في النافذة المنبثقة\nملاحظة: قد يتم تطبيق رسوم إلغاء حسب سياسة الإلغاء وموعد الإلغاء."
    ),
    FaqItem(
        question = "كيف أغير لغة التطبيق؟",
        answer = "لتغيير لغة التطبيق:\n1. اذهب إلى الإعدادات من القائمة الرئيسية\n2. اختر 'اللغة واللغة'\n3. اختر اللغة المطلوبة (العربية أو الإنجليزية)\n4. سيُعيد التطبيق التشغيل تلقائيًا لتطبيق التغييرات\nملاحظة: يدعم التطبيق حاليًا اللغتين العربية والإنجليزية."
    ),
    FaqItem(
        question = "كيف أتواصل مع الحلاق؟",
        answer = "يمكنك التواصل مع الحلاق المخصص لحجزك بعد تأكيد الحجز:\n1. اذهب إلى قسم 'حجوزاتي'\n2. ابحث عن الحجز المؤكد\n3. اضغط على الحجز لعرض التفاصيل\n4. ستجد زر 'مراسلة الحلاق' أو 'الاتصال بالحلاق'\n5. اختر الطريقة المفضلة للتواصل\nملاحظة: يتوفر التواصل مع الحلاق فقط بعد تأكيد الحجز وقبل موعد الحجز بساعة واحدة."
    ),
    FaqItem(
        question = "ما هي طرق الدفع المتاحة؟",
        answer = "نقبل طرق الدفع التالية:\n- بطاقات الائتمان/الخصم (فيزا، ماستركارد)\n- الدفع النقدي عند الاستلام\n- المحافظ الإلكترونية (PayPal، Apple Pay، Google Pay)\n- تحويل بنكي (للمؤسسات والشركات)\nجميع المعاملات آمنة ومشفرة لحماية بياناتك المالية."
    )
)
