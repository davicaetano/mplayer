package com.davicaetano.mplayer.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.davicaetano.mplayer.ui.theme.PlayerBarTrackFilled
import com.davicaetano.mplayer.ui.theme.PlayerTextSecondary
import com.davicaetano.mplayer.ui.theme.PlayerTokens
import androidx.compose.ui.unit.dp

@Composable
fun PlayerAlbumArt(
    albumArtResId: Int,
    currentTrackIndex: Int,
    artworkResIds: List<Int>,
    navigationDirection: Int,
) {
    val resIds = if (artworkResIds.size >= 2) artworkResIds else listOf(albumArtResId)
    val index = currentTrackIndex.coerceIn(0, resIds.size - 1)
    Box(
        modifier = Modifier
            .size(PlayerTokens.AlbumSize)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AnimatedContent(
            targetState = index,
            transitionSpec = {
                val dir = if (navigationDirection != 0) navigationDirection else (targetState - initialState).coerceIn(-1, 1)
                if (dir > 0) {
                    slideInHorizontally(animationSpec = tween(280), initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(animationSpec = tween(280), targetOffsetX = { -it })
                } else if (dir < 0) {
                    slideInHorizontally(animationSpec = tween(280), initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(animationSpec = tween(280), targetOffsetX = { it })
                } else {
                    fadeIn(animationSpec = tween(280)) togetherWith
                        fadeOut(animationSpec = tween(280))
                }
            },
            label = "album_art"
        ) { idx ->
            val artResId = resIds.getOrNull(idx) ?: albumArtResId
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PlayerBarTrackFilled),
                contentAlignment = Alignment.Center
            ) {
                if (artResId != 0) {
                    Image(
                        painter = painterResource(artResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = PlayerTextSecondary
                    )
                }
            }
        }
    }
}
