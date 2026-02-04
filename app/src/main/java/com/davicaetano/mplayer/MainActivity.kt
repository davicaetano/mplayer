package com.davicaetano.mplayer

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davicaetano.mplayer.player.PlayerViewModel
import com.davicaetano.mplayer.player.RepeatMode
import com.davicaetano.mplayer.ui.components.PlayerBar
import com.davicaetano.mplayer.ui.theme.MPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MPlayerTheme {
                val viewModel: PlayerViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                        LocalContext.current.applicationContext as Application
                    )
                )
                val currentIndex by viewModel.currentTrackIndex.collectAsState()
                val track = remember(viewModel.tracks, currentIndex) {
                    viewModel.tracks.getOrNull(currentIndex)
                }
                val currentPosition by viewModel.currentPositionMs.collectAsState()
                val duration by viewModel.durationMs.collectAsState()
                val isPlaying by viewModel.isPlaying.collectAsState()
                val favoriteIndices by viewModel.favoriteIndices.collectAsState()
                val repeatMode by viewModel.repeatMode.collectAsState()
                val navigationDirection by viewModel.lastNavigationDirection.collectAsState()

                val progress = remember(currentPosition, duration) {
                    if (duration <= 0) 0f else (currentPosition.toFloat() / duration).coerceIn(0f, 1f)
                }
                val isFavorite = currentIndex in favoriteIndices

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color(0xFF4A4E5A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 480.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 48.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PlayerBar(
                                title = track?.title ?: "No tracks",
                                artist = track?.artist ?: "Add track_1.mp3, track_2.mp3, track_3.mp3 to res/raw/",
                                albumArtResId = track?.artworkResId ?: 0,
                                currentTrackIndex = currentIndex,
                                artworkResIds = viewModel.tracks.map { it.artworkResId },
                                navigationDirection = navigationDirection,
                                currentPosition = currentPosition,
                                duration = duration,
                                progress = progress,
                                isPlaying = isPlaying,
                                isFavorite = isFavorite,
                                onPlayPauseClick = { viewModel.playPause() },
                                onPreviousClick = { viewModel.previous() },
                                onNextClick = { viewModel.next() },
                                repeatLabel = when (repeatMode) {
                                    RepeatMode.Off -> ""
                                    RepeatMode.All -> "All"
                                    RepeatMode.One -> "1"
                                },
                                onRepeatClick = { viewModel.onRepeatClick() },
                                onFavoriteClick = { viewModel.toggleFavorite() },
                                onSeek = { viewModel.seekTo(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}