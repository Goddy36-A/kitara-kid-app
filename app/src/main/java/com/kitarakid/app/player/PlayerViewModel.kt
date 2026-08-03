package com.kitarakid.app.player

import android.app.Application
import android.content.ComponentName
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
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
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val error: String? = null
)

/**
 * Talks to PlaybackService through a MediaController instead of owning an
 * ExoPlayer directly. This is what makes background playback + lock-screen
 * controls work: the actual player lives in the service, this ViewModel is
 * just a remote control that also mirrors its state into Compose.
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var controller: MediaController? = null
    private var queue: List<Song> = emptyList()
    private var pendingSongId: String? = null

    init {
        val context = getApplication<Application>()
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener({
            controller = future.get()
            attachListener()
            pendingSongId?.let { id -> queue.find { it.id == id }?.let { play(it) } }
        }, MoreExecutors.directExecutor())
        trackPosition()
    }

    private fun attachListener() {
        controller?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val c = controller ?: return
                _state.value = _state.value.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING,
                    durationMs = c.duration.coerceAtLeast(0L)
                )
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val c = controller
                val song = queue.find { it.id == mediaItem?.mediaId }
                _state.value = _state.value.copy(
                    currentSong = song,
                    hasNext = c?.hasNextMediaItem() == true,
                    hasPrevious = c?.hasPreviousMediaItem() == true
                )
            }

            override fun onPlayerError(error: PlaybackException) {
                _state.value = _state.value.copy(error = error.message ?: "Playback error")
            }
        })
    }

    private fun trackPosition() {
        viewModelScope.launch {
            while (isActive) {
                controller?.let { c ->
                    if (c.isPlaying) {
                        _state.value = _state.value.copy(positionMs = c.currentPosition)
                    }
                }
                delay(500)
            }
        }
    }

    /** Loads the playable catalog as the controller's queue, in list order. */
    fun setQueue(songs: List<Song>) {
        queue = songs.filter { it.isPlayableInApp }
    }

    fun play(song: Song) {
        val c = controller
        if (c == null) {
            // Controller not connected yet — remember the request and play
            // once the async connection to PlaybackService finishes.
            pendingSongId = song.id
            return
        }
        if (queue.isEmpty()) return
        val index = queue.indexOfFirst { it.id == song.id }
        if (index == -1) return

        if (c.mediaItemCount == 0) {
            c.setMediaItems(queue.map { it.toMediaItem() }, index, 0L)
            c.prepare()
        } else if (_state.value.currentSong?.id != song.id) {
            c.seekTo(index, 0L)
        }
        c.play()
    }

    fun togglePlayPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun skipNext() {
        controller?.takeIf { it.hasNextMediaItem() }?.seekToNextMediaItem()
    }

    fun skipPrevious() {
        controller?.takeIf { it.hasPreviousMediaItem() }?.seekToPreviousMediaItem()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _state.value = _state.value.copy(positionMs = positionMs)
    }

    private fun Song.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(id)
            .setUri(audioUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist("Kitara Kid")
                    .setArtworkUri(Uri.parse(coverUrl))
                    .build()
            )
            .build()

    override fun onCleared() {
        controller?.release()
        controller = null
        super.onCleared()
    }
}
