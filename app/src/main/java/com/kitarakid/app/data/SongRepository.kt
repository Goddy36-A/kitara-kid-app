package com.kitarakid.app.data

import com.kitarakid.app.model.Song

/**
 * Replace this hardcoded list with your real catalog.
 *
 * Fastest path to get real audio playing:
 * 1. Upload your MP3s to Firebase Storage (or any host with public URLs).
 * 2. Make each file's download URL public.
 * 3. Paste that URL into `audioUrl` below.
 * 4. Add your YouTube / Mdundo / Tubidy / Muzbox links for the tracks that
 *    live there so the "Listen elsewhere" row has somewhere to send fans.
 *
 * Later this can become a network call (Firestore, a simple JSON file on
 * your own server, etc.) instead of a hardcoded list — the rest of the app
 * doesn't need to change, it just reads whatever SongRepository returns.
 */
object SongRepository {

    // Your real Mdundo artist photo — swap per-track when you have individual
    // cover art; this is used as a fallback for every track below.
    private const val ARTIST_PHOTO =
        "https://mdundo.com/media/picture/600624_NNuX7tSGAfqyXeKNPHQagBDSJEQi_b.jpg"

    // Your Mdundo artist page. Each track below links here as a "listen
    // elsewhere" fallback since individual per-song Mdundo URLs weren't
    // pulled in this pass — swap in the specific mdundo.com/song/<id> link
    // for a track once you have it, for a more precise deep link.
    private const val ARTIST_MDUNDO_PAGE = "https://play.mdundo.com/artist/600624/Kitara-kid"

    // Pulled from your Mdundo artist page (43 tracks). audioUrl is left null
    // for all of them — Mdundo doesn't expose direct downloadable file URLs
    // without a Premium account, so this list is titles + a Mdundo link only.
    // Fill in `audioUrl` per track once you've hosted the actual MP3 file
    // (Firebase Storage, your own server, etc.) to make it playable in-app.
    private val trackTitles = listOf(
        "Dance To The King",
        "Omwonyo gwensi (Salt Of The World)",
        "From grass to Grace",
        "Paper I pluck",
        "Hossana Nyakusiga Jerusalema remix",
        "When Angels Sing",
        "The Lord is my Shepherd",
        "Na You Be God",
        "No weapon",
        "Belle full",
        "Holy Intercession",
        "Helper",
        "Carry Me Go",
        "Goodness And Mercy",
        "Apostles' Creed",
        "Murekye Kuruha",
        "Healer",
        "Love exe",
        "Salome don't cry for me",
        "Ideawood Movement",
        "We Praise",
        "Omugisha rwa'banyabyemwengye",
        "Yours digitally",
        "Merry Christmas",
        "Baby No Dey Vex oh",
        "Tindatine drill male version",
        "No body female version",
        "I Love Mbarara",
        "Living Water",
        "Mama",
        "Be wise",
        "Ninkunda",
        "Bondage Breaker",
        "Emotional Ending remix",
        "x",
        "Muhara we",
        "Owakigambire afro-house version",
        "Church Riddim",
        "Ninkunda 2",
        "Non living thing",
        "Kitara talking",
        "Ebinyumu ebyaffe remix",
        "Tindatine remix"
    )

    fun getSongs(): List<Song> = trackTitles.mapIndexed { index, title ->
        Song(
            id = "mdundo-${index + 1}",
            title = title,
            coverUrl = ARTIST_PHOTO,
            durationLabel = "--:--",
            audioUrl = null, // <- paste your hosted MP3 URL here once available
            youtubeUrl = null,
            mdundoUrl = ARTIST_MDUNDO_PAGE,
            tubidyUrl = null,
            muzboxUrl = null
        )
    }
}
