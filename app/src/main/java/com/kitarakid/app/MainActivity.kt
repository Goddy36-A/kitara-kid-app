package com.kitarakid.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kitarakid.app.data.FavoritesRepository
import com.kitarakid.app.data.SongRepository
import com.kitarakid.app.player.PlayerViewModel
import com.kitarakid.app.ui.components.MiniPlayer
import com.kitarakid.app.ui.screens.PlayerScreen
import com.kitarakid.app.ui.screens.SongListScreen
import com.kitarakid.app.ui.theme.KitaraKidTheme

class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 13+ requires runtime permission to show the playback
        // notification (which is what makes background/lock-screen controls
        // visible). Playback still works without it, just without the
        // system notification.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

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
    val context = LocalContext.current
    val songs = remember { SongRepository.getSongs() }
    val playbackState by playerViewModel.state.collectAsState()
    var isPlayerExpanded by remember { mutableStateOf(false) }

    val favoritesRepository = remember { FavoritesRepository(context) }
    val favoriteIds by favoritesRepository.favoriteIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    LaunchedEffect(songs) { playerViewModel.setQueue(songs) }

    Box(Modifier.fillMaxSize()) {
        Scaffold { padding ->
            SongListScreen(
                songs = songs,
                currentlyPlayingId = playbackState.currentSong?.id,
                favoriteIds = favoriteIds,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                showFavoritesOnly = showFavoritesOnly,
                onToggleShowFavoritesOnly = { showFavoritesOnly = !showFavoritesOnly },
                onSongClick = { song ->
                    if (song.isPlayableInApp) {
                        playerViewModel.play(song)
                    }
                    isPlayerExpanded = true
                },
                onToggleFavorite = { song -> favoritesRepository.toggleFavorite(song.id) },
                onShufflePlay = {
                    playerViewModel.shuffleAndPlay()
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
                        isFavorite = favoriteIds.contains(current.id),
                        onTogglePlay = { playerViewModel.togglePlayPause() },
                        onSeek = { playerViewModel.seekTo(it) },
                        onNext = { playerViewModel.skipNext() },
                        onPrevious = { playerViewModel.skipPrevious() },
                        onToggleShuffle = { playerViewModel.toggleShuffle() },
                        onCycleRepeat = { playerViewModel.cycleRepeatMode() },
                        onToggleFavorite = { favoritesRepository.toggleFavorite(current.id) },
                        onCollapse = { isPlayerExpanded = false }
                    )
                }
            }
        }
    }
}
