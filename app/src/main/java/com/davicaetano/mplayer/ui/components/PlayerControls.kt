package com.davicaetano.mplayer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.davicaetano.mplayer.R
import com.davicaetano.mplayer.ui.theme.PlayerSelected
import com.davicaetano.mplayer.ui.theme.PlayerTextPrimary
import com.davicaetano.mplayer.ui.theme.PlayerTextSecondary
import com.davicaetano.mplayer.ui.theme.PlayerTimeStyle
import com.davicaetano.mplayer.ui.theme.PlayerTokens
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
private fun ControlIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    tint: Color = PlayerTextPrimary
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(PlayerTokens.ControlIconSize)
            .minimumInteractiveComponentSize()
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isFavorite: Boolean,
    repeatLabel: String,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    CompositionLocalProvider(
        LocalRippleConfiguration provides RippleConfiguration(color = PlayerTextPrimary),
    ) {
        Row(
            modifier = modifier
                .widthIn(max = PlayerTokens.ControlsMaxWidth)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(PlayerTokens.ControlIconSize),
                contentAlignment = Alignment.Center
            ) {
                ControlIconButton(
                    onClick = onRepeatClick,
                    imageVector = Icons.Default.Repeat,
                    contentDescription = stringResource(R.string.cd_repeat_track)
                )
                if (repeatLabel.isNotEmpty()) {
                    androidx.compose.material3.Text(
                        text = repeatLabel,
                        style = PlayerTimeStyle,
                        color = PlayerTextSecondary,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 10.dp)
                    )
                }
            }
            ControlIconButton(
                onClick = onPreviousClick,
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.cd_previous_track)
            )
            Box(
                modifier = Modifier
                    .size(PlayerTokens.PauseButtonSize)
                    .clip(CircleShape)
                    .background(PlayerSelected)
                    .minimumInteractiveComponentSize()
                    .offset(2.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(
                            bounded = false,
                            radius = PlayerTokens.PauseButtonSize / 2,
                            color = PlayerTextPrimary
                        ),
                        onClick = onPlayPauseClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                val playPauseContentDesc = stringResource(
                    if (isPlaying) R.string.cd_pause_playback else R.string.cd_play_playback
                )
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.play_pause)
                )
                val progress by animateFloatAsState(
                    targetValue = if (isPlaying) 1.0f / 6.0f else 0.0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "play_pause"
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = playPauseContentDesc }
                )
            }
            ControlIconButton(
                onClick = onNextClick,
                imageVector = Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.cd_next_track)
            )
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(PlayerTokens.ControlIconSize)
                    .minimumInteractiveComponentSize()
            ) {
                val likeContentDesc = stringResource(
                    if (isFavorite) R.string.cd_remove_from_favorites else R.string.cd_add_to_favorites
                )
                val likeComposition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.love)
                )
                val likeProgress by animateFloatAsState(
                    targetValue = if (isFavorite) 30f / 45f else 8f / 45f,
                    animationSpec = tween(durationMillis = 300),
                    label = "like",
                )
                LottieAnimation(
                    composition = likeComposition,
                    progress = { likeProgress },
                    modifier = Modifier
                        .size(PlayerTokens.ControlIconSizeLottie)
                        .semantics { contentDescription = likeContentDesc }
                )
            }
        }
    }
}
