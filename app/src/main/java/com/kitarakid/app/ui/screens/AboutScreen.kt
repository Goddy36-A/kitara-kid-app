package com.kitarakid.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kitarakid.app.data.ArtistInfo
import com.kitarakid.app.ui.theme.Gold

@Composable
fun AboutScreen(onCollapse: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(onClick = onCollapse) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Collapse",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(16.dp))

        AsyncImage(
            model = ArtistInfo.PHOTO_URL,
            contentDescription = ArtistInfo.NAME,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            ArtistInfo.NAME,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text(
            ArtistInfo.BIO,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        val links = listOfNotNull(
            ArtistInfo.INSTAGRAM_URL?.let { "Instagram" to it },
            ArtistInfo.WHATSAPP_URL?.let { "WhatsApp" to it },
            ArtistInfo.YOUTUBE_URL?.let { "YouTube" to it },
            ArtistInfo.MDUNDO_URL?.let { "Mdundo" to it }
        )

        if (links.isNotEmpty()) {
            Text(
                "FOLLOW",
                style = MaterialTheme.typography.labelLarge,
                color = Gold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 8.dp)
            ) {
                links.forEach { (label, url) ->
                    AssistChip(
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}
