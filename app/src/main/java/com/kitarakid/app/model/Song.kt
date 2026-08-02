package com.kitarakid.app.model

/**
 * A single track.
 *
 * audioUrl: the primary, directly-playable source. Point this at an MP3 you
 * host yourself (Firebase Storage, S3, your own server, or even a GitHub
 * Release asset URL). This is what ExoPlayer streams inside the app.
 *
 * The four *Url fields are optional "listen elsewhere" backups shown as
 * external links when a track isn't available (or you haven't uploaded the
 * file yet) — they open the platform app or browser via an Intent.
 */
data class Song(
    val id: String,
    val title: String,
    val coverUrl: String,
    val durationLabel: String = "--:--",
    val audioUrl: String? = null,
    val youtubeUrl: String? = null,
    val mdundoUrl: String? = null,
    val tubidyUrl: String? = null,
    val muzboxUrl: String? = null
) {
    val isPlayableInApp: Boolean get() = !audioUrl.isNullOrBlank()
}
