package com.kitarakid.app.data

import android.content.Context
import com.kitarakid.app.model.Song
import org.json.JSONArray
import org.json.JSONObject

/**
 * Caches the last successfully fetched song catalog on-device, so the app
 * shows real (if slightly stale) data instantly on launch, and still works
 * fully offline or if GitHub's API is unreachable/rate-limited.
 */
class CatalogCache(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("kitara_catalog_cache", Context.MODE_PRIVATE)

    fun load(): List<Song>? {
        val raw = prefs.getString(KEY_SONGS, null) ?: return null
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Song(
                    id = o.getString("id"),
                    title = o.getString("title"),
                    coverUrl = o.getString("coverUrl"),
                    durationLabel = o.optString("durationLabel", "--:--"),
                    audioUrl = o.optString("audioUrl").ifBlank { null },
                    youtubeUrl = o.optString("youtubeUrl").ifBlank { null },
                    mdundoUrl = o.optString("mdundoUrl").ifBlank { null },
                    tubidyUrl = o.optString("tubidyUrl").ifBlank { null },
                    muzboxUrl = o.optString("muzboxUrl").ifBlank { null }
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun save(songs: List<Song>) {
        val arr = JSONArray()
        songs.forEach { song ->
            arr.put(
                JSONObject().apply {
                    put("id", song.id)
                    put("title", song.title)
                    put("coverUrl", song.coverUrl)
                    put("durationLabel", song.durationLabel)
                    put("audioUrl", song.audioUrl ?: "")
                    put("youtubeUrl", song.youtubeUrl ?: "")
                    put("mdundoUrl", song.mdundoUrl ?: "")
                    put("tubidyUrl", song.tubidyUrl ?: "")
                    put("muzboxUrl", song.muzboxUrl ?: "")
                }
            )
        }
        prefs.edit().putString(KEY_SONGS, arr.toString()).apply()
    }

    companion object {
        private const val KEY_SONGS = "songs_json"
    }
}
