package com.kblack.offlinemap.presentation.screen.overview.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.graphhopper.util.Instruction
import com.kblack.offlinemap.domain.models.Route
import com.kblack.offlinemap.domain.models.RouteInstruction
import com.kblack.offlinemap.domain.utils.RouteTextFormatter
import com.kblack.offlinemap.presentation.ui.theme.customColors

@Composable
fun RouteInstructionsSheetContent(
    route: Route?,
    isRouting: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        RouteSummaryHeader(route = route)

        when {
            isRouting -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            route == null || route.instructions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Chua co chi duong",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(route.instructions) { instruction ->
                        RouteInstructionRow(instruction = instruction)
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteSummaryHeader(route: Route?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.customColors.taskCardBgColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SummaryMetric(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Straighten,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            },
            label = "Distance",
            value = RouteTextFormatter.formatDistanceMeters(route?.distanceMeters ?: 0.0)
        )

        SummaryMetric(
            icon = {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            },
            label = "Time",
            value = RouteTextFormatter.formatDurationMillis(route?.durationMillis ?: 0L)
        )
    }
}

@Composable
private fun SummaryMetric(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            Text(text = value, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun RouteInstructionRow(instruction: RouteInstruction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        RouteSignIcon(sign = instruction.sign)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = instructionTitle(instruction),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = RouteTextFormatter.formatDistanceMeters(instruction.distanceMeters),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RouteSignIcon(sign: Int) {
    val rotation = routeSignRotationDegrees(sign)

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(MaterialTheme.customColors.taskCardBgColor),
        contentAlignment = Alignment.Center
    ) {
        // todo: FIXME Created by AI, I will revise it later. The icon should depend on the sign type, not just an arrow.
        Icon(
            imageVector = Icons.Filled.ArrowUpward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(16.dp)
                .rotate(rotation)
        )
    }
}

//todo: FIXME Created by AI, I will revise it later. :)))
private fun routeSignRotationDegrees(sign: Int): Float {
    return when (sign) {
        Instruction.TURN_SLIGHT_LEFT -> -35f
        Instruction.TURN_LEFT -> -90f
        Instruction.TURN_SHARP_LEFT -> -135f
        Instruction.KEEP_LEFT -> -20f
        Instruction.TURN_SLIGHT_RIGHT -> 35f
        Instruction.TURN_RIGHT -> 90f
        Instruction.TURN_SHARP_RIGHT -> 135f
        Instruction.KEEP_RIGHT -> 20f
        Instruction.U_TURN_LEFT,
        Instruction.U_TURN_RIGHT,
        Instruction.U_TURN_UNKNOWN -> 180f

        // Roundabout and unknown-like signs keep neutral direction in this base implementation.
        Instruction.USE_ROUNDABOUT,
        Instruction.LEAVE_ROUNDABOUT,
        Instruction.CONTINUE_ON_STREET,
        Instruction.FINISH,
        Instruction.REACHED_VIA,
        Instruction.UNKNOWN,
        Instruction.IGNORE,
        Instruction.PT_START_TRIP,
        Instruction.PT_TRANSFER,
        Instruction.PT_END_TRIP -> 0f

        else -> 0f
    }
}

private fun instructionTitle(instruction: RouteInstruction): String {
    val action = when (instruction.sign) {
        Instruction.UNKNOWN -> "Continue"
        Instruction.U_TURN_UNKNOWN -> "Make a U-turn"
        Instruction.U_TURN_LEFT -> "Make a U-turn left"
        Instruction.KEEP_LEFT -> "Keep left"
        Instruction.LEAVE_ROUNDABOUT -> "Leave roundabout"
        Instruction.TURN_SHARP_LEFT -> "Turn sharp left"
        Instruction.TURN_LEFT -> "Turn left"
        Instruction.TURN_SLIGHT_LEFT -> "Turn slight left"
        Instruction.CONTINUE_ON_STREET -> "Continue on"
        Instruction.TURN_SLIGHT_RIGHT -> "Turn slight right"
        Instruction.TURN_RIGHT -> "Turn right"
        Instruction.TURN_SHARP_RIGHT -> "Turn sharp right"
        Instruction.FINISH -> "Arrive"
        Instruction.REACHED_VIA -> "Reached via point"
        Instruction.USE_ROUNDABOUT -> "Enter roundabout"
        Instruction.KEEP_RIGHT -> "Keep right"
        Instruction.U_TURN_RIGHT -> "Make a U-turn right"
        Instruction.PT_START_TRIP -> "Start trip"
        Instruction.PT_TRANSFER -> "Transfer"
        Instruction.PT_END_TRIP -> "End trip"
        Instruction.IGNORE -> "Continue"
        else -> "Continue"
    }

    val name = instruction.name.trim()
    return if (name.isEmpty()) action else "$action $name"
}
