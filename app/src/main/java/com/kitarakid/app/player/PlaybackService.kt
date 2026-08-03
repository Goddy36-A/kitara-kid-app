package com.kitarakid.app.player

import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import java.io.File

/**
 * Runs playback as a foreground service so audio survives the app being
 * backgrounded or the screen turning off. Media3 automatically posts the
 * system media notification (with play/pause/skip) for whatever MediaSession
 * this service exposes.
 *
 * Also wires ExoPlayer's data source through a disk cache: previously played
 * tracks are read from disk on repeat plays instead of being re-streamed.
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    companion object {
        // 300 MB cap for cached audio. Oldest-used tracks get evicted first.
        private const val CACHE_SIZE_BYTES = 300L * 1024 * 1024

        @Volatile
        private var cacheInstance: SimpleCache? = null

        fun getCache(context: Context): SimpleCache =
            cacheInstance ?: synchronized(this) {
                cacheInstance ?: SimpleCache(
                    File(context.applicationContext.cacheDir, "kitara_media_cache"),
                    LeastRecentlyUsedCacheEvictor(CACHE_SIZE_BYTES),
                    StandaloneDatabaseProvider(context.applicationContext)
                ).also { cacheInstance = it }
            }
    }

    override fun onCreate() {
        super.onCreate()

        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(getCache(applicationContext))
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            .setHandleAudioBecomingNoisy(true) // pause if headphones are unplugged
            .build()

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        // If the user swipes the app away while nothing is actively playing,
        // stop the service instead of lingering. If music IS playing, Media3
        // keeps it alive via the foreground notification as expected.
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
