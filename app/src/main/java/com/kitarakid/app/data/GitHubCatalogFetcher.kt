package com.kitarakid.app.data

import com.kitarakid.app.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Pulls the current track list straight from the GitHub Release's assets
 * each time the app opens. Add or remove an MP3 in that release on GitHub
 * and every install picks it up automatically \u2014 no new app build needed.
 *
 * This is a deliberate stepping stone: GitHub's unauthenticated API is
 * rate-limited (~60 requests/hour per IP), which is fine for testing and a
 * small audience but not a real production catalog backend. When you move
 * to real cloud hosting (Cloudflare R2 / a proper API), swap the body of
 * fetchSongs() to call that instead \u2014 nothing else in the app needs to
 * change, since it still just returns a List<Song>.
 */
object GitHubCatalogFetcher {

    private const val RELEASE_API_URL =
        "https://api.github.com/repos/Goddy36-A/kitara-kid-app/releases/tags/kitara-kid-all-songs"

    private const val ARTIST_PHOTO =
        "https://mdundo.com/media/picture/600624_NNuX7tSGAfqyXeKNPHQagBDSJEQi_b.jpg"

    private const val ARTIST_MDUNDO_PAGE = "https://play.mdundo.com/artist/600624/Kitara-kid"

    suspend fun fetchSongs(): List<Song>? = withContext(Dispatchers.IO) {
        try {
            val connection = (URL(RELEASE_API_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github+json")
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            if (connection.responseCode != 200) return@withContext null

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val assets = JSONObject(body).optJSONArray("assets") ?: return@withContext null
            parseAssets(assets).ifEmpty { null }
        } catch (e: Exception) {
            null
        }
    }

    private data class RawEntry(val order: Int, val filename: String, val url: String)

    private fun parseAssets(assets: JSONArray): List<Song> {
        val entries = mutableListOf<RawEntry>()
        for (i in 0 until assets.length()) {
            val asset = assets.getJSONObject(i)
            val name = asset.optString("name")
            if (!name.endsWith(".mp3", ignoreCase = true)) continue
            val url = asset.optString("browser_download_url")
            if (url.isBlank()) continue

            val numberMatch = Regex("^(\\d+)_").find(name)
            val order = numberMatch?.groupValues?.get(1)?.toIntOrNull() ?: Int.MAX_VALUE
            entries.add(RawEntry(order, name, url))
        }

        return entries
            .sortedWith(compareBy({ it.order }, { it.filename }))
            .map { entry ->
                Song(
                    id = "github-${entry.filename}",
                    title = titleFromFilename(entry.filename),
                    coverUrl = ARTIST_PHOTO,
                    durationLabel = "--:--",
                    audioUrl = entry.url,
                    youtubeUrl = null,
                    mdundoUrl = ARTIST_MDUNDO_PAGE,
                    tubidyUrl = null,
                    muzboxUrl = null
                )
            }
    }

    private fun titleFromFilename(filename: String): String {
        var name = filename.removeSuffix(".mp3").removeSuffix(".MP3")
        // Strip a leading track-number prefix like "01_" — keeps hyphens
        // inside real words (e.g. "afro-house") intact.
        name = name.replaceFirst(Regex("^\\d+_"), "")
        name = name.replace('_', ' ').trim()
        return name
    }
}
