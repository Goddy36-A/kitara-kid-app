package com.kitarakid.app.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kitarakid.app.model.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val error: String? = null
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.value = _state.value.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING,
                    durationMs = exoPlayer.duration.coerceAtLeast(0L)
                )
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                _state.value = _state.value.copy(error = error.message ?: "Playback error")
            }
        })
        trackPosition()
    }

    private fun trackPosition() {
        viewModelScope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    _state.value = _state.value.copy(positionMs = exoPlayer.currentPosition)
                }
                delay(500)
            }
        }
    }

    fun play(song: Song) {
        val url = song.audioUrl ?: return
        if (_state.value.currentSong?.id != song.id) {
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            _state.value = _state.value.copy(currentSong = song, error = null)
        }
        exoPlayer.play()
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _state.value = _state.value.copy(positionMs = positionMs)
    }

    override fun onCleared() {
        exoPlayer.release()
        super.onCleared()
    }
}
