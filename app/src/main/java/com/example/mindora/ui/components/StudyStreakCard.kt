package com.example.mindora.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.LimeAccent
import com.example.mindora.viewmodel.StudyStreakState

@Composable
fun StreakFlame(modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 52.dp) {
    val transition = rememberInfiniteTransition(label = "studyFlame")
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(760), RepeatMode.Reverse),
        label = "flamePulse"
    )
    Box(
        modifier.size(size).scale(pulse).clip(CircleShape)
            .background(CoralAccent.copy(alpha = .17f))
            .semantics { contentDescription = "Animated study streak flame" },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = CoralAccent,
            modifier = Modifier.size(size * .70f)
        )
    }
}

@Composable
fun StudyStreakCard(streak: StudyStreakState, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth()
            .clip(MindoraFeatureShape)
            .background(DeepMidnightPurple)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StreakFlame(size = 54.dp)
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text("STUDY STREAK", color = ElectricLavender, fontSize = 10.sp, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(streak.currentDays.toString(), color = Color.White, fontSize = 34.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(6.dp))
                    Text(if (streak.currentDays == 1) "day" else "days", color = Color.White.copy(alpha = .76f), fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("BEST", color = LimeAccent, fontSize = 9.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.Bold)
                Text(streak.bestDays.toString() + " days", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val labels = listOf("M", "T", "W", "T", "F", "S", "S")
            val fullLabels = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            labels.forEachIndexed { index, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val active = streak.activeThisWeek.getOrElse(index) { false }
                    Box(
                        Modifier.size(30.dp)
                            .clip(CircleShape)
                            .background(if (active) LimeAccent else Color.White.copy(alpha = .12f))
                            .semantics {
                                contentDescription = fullLabels[index] + if (active) ": study activity recorded" else ": no activity recorded"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (active) {
                            Icon(Icons.Default.LocalFireDepartment, null, tint = DeepMidnightPurple, modifier = Modifier.size(16.dp))
                        } else {
                            Box(Modifier.size(5.dp).clip(CircleShape).background(Color.White.copy(alpha = .4f)))
                        }
                    }
                    Text(label, color = Color.White.copy(alpha = .78f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().clip(MindoraSurfaceShape)
                .background(Color.White.copy(alpha = .08f)).padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(streak.weeklyActiveDays.toString() + " active " + (if (streak.weeklyActiveDays == 1) "day" else "days") + " this week", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(7, 14, 30, 100).forEach { milestone ->
                    val reached = streak.bestDays >= milestone
                    Text(
                        milestone.toString(),
                        color = if (reached) LimeAccent else Color.White.copy(alpha = .48f),
                        fontSize = if (milestone == 100) 9.sp else 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics {
                            contentDescription = milestone.toString() + " day milestone " + if (reached) "reached" else "not reached"
                        }
                    )
                }
            }
        }
    }
}
