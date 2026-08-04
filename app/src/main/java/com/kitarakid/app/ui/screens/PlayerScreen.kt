package com.kitarakid.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.kitarakid.app.model.Song
import com.kitarakid.app.player.PlaybackState
import com.kitarakid.app.ui.theme.Gold
import android.content.Intent
import android.net.Uri

@Composable
fun PlayerScreen(
    song: Song,
    playbackState: PlaybackState,
    isFavorite: Boolean,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(onClick = onCollapse) {
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Collapse", tint = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(Modifier.height(24.dp))

        AsyncImage(
            model = song.coverUrl,
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
        )

        Spacer(Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Kitara Kid",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                tint = if (isFavorite) Gold else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onToggleFavorite)
                    .padding(2.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        if (song.isPlayableInApp) {
            Slider(
                value = playbackState.positionMs.toFloat(),
                valueRange = 0f..(playbackState.durationMs.coerceAtLeast(1L)).toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                colors = SliderDefaults.colors(
                    thumbColor = Gold,
                    activeTrackColor = Gold
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleShuffle, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Filled.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.shuffleEnabled) Gold else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onPrevious,
                    enabled = playbackState.hasPrevious,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.SkipPrevious,
                        contentDescription = "Previous",
                        tint = if (playbackState.hasPrevious) MaterialTheme.colorScheme.onBackground
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Gold),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onTogglePlay) {
                        Icon(
                            if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                IconButton(
                    onClick = onNext,
                    enabled = playbackState.hasNext,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.SkipNext,
                        contentDescription = "Next",
                        tint = if (playbackState.hasNext) MaterialTheme.colorScheme.onBackground
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(onClick = onCycleRepeat, modifier = Modifier.size(40.dp)) {
                    Icon(
                        if (playbackState.repeatMode == Player.REPEAT_MODE_ONE) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                        contentDescription = "Repeat",
                        tint = if (playbackState.repeatMode != Player.REPEAT_MODE_OFF) Gold else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            Text(
                "Not hosted in-app yet \u2014 listen on one of the platforms below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(32.dp))

        val links = listOfNotNull(
            song.youtubeUrl?.let { "YouTube" to it },
            song.mdundoUrl?.let { "Mdundo" to it },
            song.tubidyUrl?.let { "Tubidy" to it },
            song.muzboxUrl?.let { "Muzbox" to it }
        )

        if (links.isNotEmpty()) {
            Text(
                "ALSO AVAILABLE ON",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            FlowRowLinks(links) { url ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }
}

@Composable
private fun FlowRowLinks(links: List<Pair<String, String>>, onClick: (String) -> Unit) {
    Column {
        links.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { (label, url) ->
                    AssistChip(
                        onClick = { onClick(url) },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
