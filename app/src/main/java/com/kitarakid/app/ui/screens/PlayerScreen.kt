package com.kitarakid.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
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

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
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
