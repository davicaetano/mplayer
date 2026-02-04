package com.davicaetano.mplayer.player

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.davicaetano.mplayer.data.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// res/raw/ = track_01.mp3 → "track_01" | res/drawable/ = art_1.jpg → "art_1"
private fun tracks(application: Application): List<Track> {
    val res = application.resources
    val pkg = application.packageName
    return listOf(
        Track("Mario - Ao vivo no Circo voador", "Criollo", res.getIdentifier("track_01", "raw", pkg), res.getIdentifier("art_1", "drawable", pkg)),
        Track("Banditismo por uma questao de classe", "Chico Science e Nacao Zumbi", res.getIdentifier("track_02", "raw", pkg), res.getIdentifier("art_2", "drawable", pkg)),
        Track("Os alquimistas estao chegando", "Jorge Benjor", res.getIdentifier("track_03", "raw", pkg), 0), // 0 = no art, shows MusicNote placeholder
    ).filter { it.rawResId != 0 }
}

private const val PREFS_FAVORITES = "player_favorites"
private const val KEY_FAVORITE_INDICES = "favorite_track_indices"
private const val KEY_REPEAT_MODE = "repeat_mode"

enum class RepeatMode { Off, All, One }

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application.applicationContext
    private val prefs = app.getSharedPreferences(PREFS_FAVORITES, android.content.Context.MODE_PRIVATE)
    private val trackList = tracks(application)
    private var mediaPlayer: MediaPlayer? = null
    private var positionJob: Job? = null

    private val _currentTrackIndex = MutableStateFlow(0)
    private val _currentPositionMs = MutableStateFlow(0L)
    private val _durationMs = MutableStateFlow(0L)
    private val _isPlaying = MutableStateFlow(false)
    private val _favoriteIndices = MutableStateFlow(loadFavoriteIndices())
    private val _repeatMode = MutableStateFlow(loadRepeatMode())
    private val _lastNavigationDirection = MutableStateFlow(0) // 1 = next, -1 = previous

    val tracks: List<Track> = trackList
    val lastNavigationDirection: StateFlow<Int> = _lastNavigationDirection.asStateFlow()
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    val favoriteIndices: StateFlow<Set<Int>> = _favoriteIndices.asStateFlow()

    private fun loadFavoriteIndices(): Set<Int> {
        val raw = prefs.getString(KEY_FAVORITE_INDICES, null) ?: return emptySet()
        return raw.split(",")
            .mapNotNull { it.toIntOrNull() }
            .filter { it in trackList.indices }
            .toSet()
    }

    private fun saveFavoriteIndices(indices: Set<Int>) {
        prefs.edit()
            .putString(KEY_FAVORITE_INDICES, indices.sorted().joinToString(","))
            .apply()
    }

    private fun loadRepeatMode(): RepeatMode {
        val name = prefs.getString(KEY_REPEAT_MODE, RepeatMode.Off.name) ?: return RepeatMode.Off
        return enumValues<RepeatMode>().find { it.name == name } ?: RepeatMode.Off
    }

    private fun saveRepeatMode(mode: RepeatMode) {
        prefs.edit().putString(KEY_REPEAT_MODE, mode.name).apply()
    }

    init {
        if (trackList.isNotEmpty()) prepareTrack(0)
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    fun playPause() {
        val player = mediaPlayer ?: return
        if (_isPlaying.value) {
            player.pause()
            positionJob?.cancel()
        } else {
            player.start()
            startPositionUpdates()
        }
        _isPlaying.value = !_isPlaying.value
    }

    fun previous() {
        val idx = _currentTrackIndex.value
        if (_currentPositionMs.value > 2000 && idx >= 0) {
            seekTo(0f)
            return
        }
        _lastNavigationDirection.value = -1
        val prev = if (idx > 0) idx - 1 else trackList.size - 1
        switchToTrack(prev)
    }

    fun next() {
        _lastNavigationDirection.value = 1
        val idx = _currentTrackIndex.value
        val nextIdx = if (idx < trackList.size - 1) idx + 1 else 0
        switchToTrack(nextIdx)
    }

    fun seekTo(progressFraction: Float) {
        val player = mediaPlayer ?: return
        val dur = player.duration
        if (dur <= 0) return
        val ms = (progressFraction * dur).toInt().toLong()
        player.seekTo(ms.toInt())
        _currentPositionMs.value = ms
    }

    fun toggleFavorite() {
        val idx = _currentTrackIndex.value
        val next = if (idx in _favoriteIndices.value) {
            _favoriteIndices.value - idx
        } else {
            _favoriteIndices.value + idx
        }
        _favoriteIndices.value = next
        saveFavoriteIndices(next)
    }

    fun onRepeatClick() {
        val next = when (_repeatMode.value) {
            RepeatMode.Off -> RepeatMode.All
            RepeatMode.All -> RepeatMode.One
            RepeatMode.One -> RepeatMode.Off
        }
        _repeatMode.value = next
        saveRepeatMode(next)
    }

    private fun prepareTrack(index: Int) {
        releasePlayer()
        if (index !in trackList.indices) return
        val track = trackList[index]
        if (track.rawResId == 0) return
        val player = MediaPlayer.create(app, track.rawResId) ?: return
        player.setOnCompletionListener {
            _isPlaying.value = false
            positionJob?.cancel()
            when (_repeatMode.value) {
                RepeatMode.One -> {
                    seekTo(0f)
                    mediaPlayer?.start()
                    _isPlaying.value = true
                    startPositionUpdates()
                }
                RepeatMode.All -> {
                    _lastNavigationDirection.value = 1
                    val nextIndex = if (index < trackList.size - 1) index + 1 else 0
                    switchToTrack(nextIndex)
                }
                RepeatMode.Off -> {
                    if (index < trackList.size - 1) {
                        _lastNavigationDirection.value = 1
                        switchToTrack(index + 1)
                    } else {
                        _currentPositionMs.value = 0L
                    }
                }
            }
        }
        mediaPlayer = player
        _durationMs.value = player.duration.toLong()
        _currentPositionMs.value = 0L
        _currentTrackIndex.value = index
    }

    private fun switchToTrack(index: Int) {
        prepareTrack(index)
        mediaPlayer?.start()
        _isPlaying.value = true
        startPositionUpdates()
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = viewModelScope.launch {
            while (isActive) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _currentPositionMs.value = mp.currentPosition.toLong()
                    }
                }
                delay(200)
            }
        }
    }

    private fun releasePlayer() {
        positionJob?.cancel()
        positionJob = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
