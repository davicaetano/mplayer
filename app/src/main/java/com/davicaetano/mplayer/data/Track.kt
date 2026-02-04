package com.davicaetano.mplayer.data

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes

data class Track(
    val title: String,
    val artist: String,
    @RawRes val rawResId: Int,
    @DrawableRes val artworkResId: Int = 0,
)
