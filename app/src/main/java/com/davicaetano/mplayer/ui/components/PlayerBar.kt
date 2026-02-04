package com.davicaetano.mplayer.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davicaetano.mplayer.ui.theme.PlayerArtistStyle
import com.davicaetano.mplayer.ui.theme.PlayerBackground
import com.davicaetano.mplayer.ui.theme.PlayerTextPrimary
import com.davicaetano.mplayer.ui.theme.PlayerTextSecondary
import com.davicaetano.mplayer.ui.theme.PlayerTitleStyle
import com.davicaetano.mplayer.ui.theme.PlayerTokens
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlayerBar(
    title: String,
    artist: String,
    albumArtResId: Int = 0,
    currentTrackIndex: Int = 0,
    artworkResIds: List<Int> = emptyList(),
    navigationDirection: Int = 0,
    currentPosition: Long,
    duration: Long,
    progress: Float,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    repeatLabel: String = "",
    onRepeatClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = PlayerTokens.MaxPlayerWidth)
            .clip(RoundedCornerShape(12.dp))
            .background(PlayerBackground)
            .padding(
                horizontal = PlayerTokens.ContentHorizontalPadding,
                vertical = PlayerTokens.ContentVerticalPadding
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerAlbumArt(
                albumArtResId = albumArtResId,
                currentTrackIndex = currentTrackIndex,
                artworkResIds = artworkResIds,
                navigationDirection = navigationDirection,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = PlayerTitleStyle,
                    color = PlayerTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                )
                Text(
                    text = artist,
                    style = PlayerArtistStyle,
                    color = PlayerTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                )
            }
        }

        Spacer(modifier = Modifier.height(PlayerTokens.SectionSpacing))

        PlayerProgressBar(
            progress = progress,
            currentPosition = currentPosition,
            duration = duration,
            onSeek = onSeek,
        )

        Spacer(modifier = Modifier.height(PlayerTokens.SectionSpacing))

        PlayerControls(
            isPlaying = isPlaying,
            isFavorite = isFavorite,
            repeatLabel = repeatLabel,
            onPlayPauseClick = onPlayPauseClick,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            onRepeatClick = onRepeatClick,
            onFavoriteClick = onFavoriteClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerBarPreview() {
    Box(
        modifier = Modifier
            .background(Color.DarkGray)
            .padding(24.dp)
    ) {
        PlayerBar(
            title = "Black Friday (pretty like the sun)",
            artist = "Lost Frequencies, Tom Odell, Poppy Baskcomb",
            currentPosition = 132_000,
            duration = 311_000,
            progress = 0.42f,
            isPlaying = true,
            isFavorite = false,
            onPlayPauseClick = {},
            onPreviousClick = {},
            onNextClick = {},
            repeatLabel = "",
            onRepeatClick = {},
            onFavoriteClick = {},
            onSeek = {}
        )
    }
}
