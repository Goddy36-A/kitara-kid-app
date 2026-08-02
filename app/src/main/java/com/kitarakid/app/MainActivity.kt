package com.kitarakid.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kitarakid.app.data.SongRepository
import com.kitarakid.app.player.PlayerViewModel
import com.kitarakid.app.ui.components.MiniPlayer
import com.kitarakid.app.ui.screens.PlayerScreen
import com.kitarakid.app.ui.screens.SongListScreen
import com.kitarakid.app.ui.theme.KitaraKidTheme

class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KitaraKidTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KitaraApp(playerViewModel)
                }
            }
        }
    }
}

@Composable
fun KitaraApp(playerViewModel: PlayerViewModel) {
    val songs = remember { SongRepository.getSongs() }
    val playbackState by playerViewModel.state.collectAsState()
    var isPlayerExpanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Scaffold { padding ->
            SongListScreen(
                songs = songs,
                currentlyPlayingId = playbackState.currentSong?.id,
                onSongClick = { song ->
                    if (song.isPlayableInApp) {
                        playerViewModel.play(song)
                    }
                    isPlayerExpanded = true
                },
                modifier = Modifier.padding(padding)
            )
        }

        val current = playbackState.currentSong
        if (current != null && !isPlayerExpanded) {
            MiniPlayer(
                song = current,
                isPlaying = playbackState.isPlaying,
                onTogglePlay = { playerViewModel.togglePlayPause() },
                onExpand = { isPlayerExpanded = true },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        AnimatedVisibility(visible = isPlayerExpanded && current != null) {
            if (current != null) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PlayerScreen(
                        song = current,
                        playbackState = playbackState,
                        onTogglePlay = { playerViewModel.togglePlayPause() },
                        onSeek = { playerViewModel.seekTo(it) },
                        onCollapse = { isPlayerExpanded = false }
                    )
                }
            }
        }
    }
}
