package com.kitarakid.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kitarakid.app.model.Song
import com.kitarakid.app.ui.theme.Gold

@Composable
fun SongListScreen(
    songs: List<Song>,
    currentlyPlayingId: String?,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 160.dp)
    ) {
        item {
            Column(Modifier.padding(24.dp, 32.dp, 24.dp, 16.dp)) {
                Text(
                    "KITARA KID",
                    style = MaterialTheme.typography.labelLarge,
                    color = Gold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Your tracks",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        items(songs, key = { it.id }) { song ->
            SongRow(
                song = song,
                isActive = song.id == currentlyPlayingId,
                onClick = { onSongClick(song) }
            )
        }
    }
}

@Composable
private fun SongRow(song: Song, isActive: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isActive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isActive) Gold else MaterialTheme.colorScheme.onBackground,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                if (song.isPlayableInApp) song.durationLabel else "Listen elsewhere \u2192",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isActive) Gold else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = if (isActive) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
