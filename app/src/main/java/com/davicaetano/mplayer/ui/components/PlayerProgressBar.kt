package com.davicaetano.mplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.davicaetano.mplayer.R
import com.davicaetano.mplayer.ui.theme.PlayerBarTrack
import com.davicaetano.mplayer.ui.theme.PlayerTextPrimary
import com.davicaetano.mplayer.ui.theme.PlayerTimeStyle
import com.davicaetano.mplayer.ui.theme.PlayerTokens
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

internal fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProgressBar(
    progress: Float,
    currentPosition: Long,
    duration: Long,
    onSeek: (Float) -> Unit,
) {
    var isDragging by remember { mutableStateOf(false) }
    var localSliderValue by remember { mutableFloatStateOf(progress) }
    LaunchedEffect(progress) {
        if (!isDragging) localSliderValue = progress
    }
    val displayValue = if (isDragging) localSliderValue else progress
    val displayedPosition = if (isDragging) {
        (localSliderValue * duration).toLong().coerceIn(0L, duration)
    } else {
        currentPosition
    }
    val sliderInteractionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { PlayerTokens.ProgressBarThumbRadius.toPx() }
    val seekBarDesc = stringResource(
        R.string.cd_seek_bar,
        formatTime(displayedPosition),
        formatTime(duration)
    )
    Slider(
        value = displayValue,
        onValueChange = {
            localSliderValue = it
            isDragging = true
        },
        onValueChangeFinished = {
            onSeek(localSliderValue)
            isDragging = false
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(PlayerTokens.ProgressBarContainerHeight)
            .semantics(mergeDescendants = true) {
                contentDescription = seekBarDesc
            }
            .layout { measurable, constraints ->
                val extraWidth = (thumbRadiusPx * 2).toInt()
                val expandedConstraints = constraints.copy(maxWidth = constraints.maxWidth + extraWidth)
                val placeable = measurable.measure(expandedConstraints)
                layout(constraints.maxWidth, placeable.height) {
                    placeable.placeRelative(-thumbRadiusPx.toInt(), 0)
                }
            },
        colors = SliderDefaults.colors(
            thumbColor = PlayerTextPrimary,
            activeTrackColor = PlayerTextPrimary,
            inactiveTrackColor = PlayerBarTrack
        ),
        track = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PlayerTokens.ProgressBarTrackHeight)
                    .clip(RoundedCornerShape(PlayerTokens.ProgressBarTrackHeight / 2))
                    .background(PlayerBarTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(displayValue.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(PlayerTokens.ProgressBarTrackHeight / 2))
                        .background(PlayerTextPrimary)
                )
            }
        },
        thumb = {
            Box(contentAlignment = Alignment.CenterStart) {
                SliderDefaults.Thumb(
                    modifier = Modifier.offset(y = (PlayerTokens.ProgressBarContainerHeight - PlayerTokens.ProgressBarThumbSize) / 2),
                    interactionSource = sliderInteractionSource,
                    thumbSize = DpSize(PlayerTokens.ProgressBarThumbSize, PlayerTokens.ProgressBarThumbSize),
                    colors = SliderDefaults.colors(
                        thumbColor = PlayerTextPrimary,
                        activeTrackColor = PlayerTextPrimary,
                        inactiveTrackColor = PlayerBarTrack
                    )
                )
            }
        }
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatTime(displayedPosition),
            style = PlayerTimeStyle,
            color = PlayerTextPrimary
        )
        Text(
            text = formatTime(duration),
            style = PlayerTimeStyle,
            color = PlayerTextPrimary
        )
    }
}
