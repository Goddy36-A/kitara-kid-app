package com.kitarakid.app.data

import com.kitarakid.app.model.Song

object SongRepository {

    // Your real Mdundo artist photo — swap per-track when you have individual
    // cover art; this is used as a fallback for every track below.
    private const val ARTIST_PHOTO =
        "https://mdundo.com/media/picture/600624_NNuX7tSGAfqyXeKNPHQagBDSJEQi_b.jpg"

    // Your Mdundo artist page, shown as a "listen elsewhere" link.
    private const val ARTIST_MDUNDO_PAGE = "https://play.mdundo.com/artist/600624/Kitara-kid"

    // Your GitHub Release where all 43 MP3s are hosted — free, no billing
    // account needed, direct download links work great with ExoPlayer.
    private const val RELEASE_BASE_URL =
        "https://github.com/Goddy36-A/kitara-kid-app/releases/download/kitara-kid-all-songs/"

    // title -> exact filename in the release, matched by hand against your
    // upload list. Numbering in the filename doesn't need to match this
    // list's order.
    private val tracks = listOf(
        "Dance To The King" to "01_Dance_To_The_King.mp3",
        "Omwonyo gwensi (Salt Of The World)" to "03_Omwonyo_gwensi_Salt_Of_The_World.mp3",
        "From grass to Grace" to "04_From_grass_to_Grace.mp3",
        "Paper I pluck" to "02_Paper_I_pluck.mp3",
        "Hossana Nyakusiga Jerusalema remix" to "05_Hossana_Nyakusiga_Jerusalema_remix.mp3",
        "When Angels Sing" to "06_When_Angels_Sing.mp3",
        "The Lord is my Shepherd" to "12_The_Lord_is_my_Shepherd.mp3",
        "Na You Be God" to "07_Na_You_Be_God.mp3",
        "No weapon" to "08_No_weapon.mp3",
        "Belle full" to "09_Belle_full.mp3",
        "Holy Intercession" to "10_Holy_Intercession.mp3",
        "Helper" to "11_Helper.mp3",
        "Carry Me Go" to "14_Carry_Me_Go.mp3",
        "Goodness And Mercy" to "15_Goodness_And_Mercy.mp3",
        "Apostles' Creed" to "16_Apostles_Creed.mp3",
        "Murekye Kuruha" to "17_Murekye_Kuruha.mp3",
        "Healer" to "18_Healer.mp3",
        "Love exe" to "19_Love_exe.mp3",
        "Salome don't cry for me" to "13_Salome_dont_cry_for_me.mp3",
        "Ideawood Movement" to "20_Ideawood_Movement.mp3",
        "We Praise" to "21_We_Praise.mp3",
        "Omugisha rwa'banyabyemwengye" to "22_Omugisha_rwabanyabyemwengye.mp3",
        "Yours digitally" to "23_Yours_digitally.mp3",
        "Merry Christmas" to "24_Merry_Christmas.mp3",
        "Baby No Dey Vex oh" to "25_Baby_No_Dey_Vex_oh.mp3",
        "Tindatine drill male version" to "32_Tindatine_drill_male_version.mp3",
        "No body female version" to "27_No_body_female_version.mp3",
        "I Love Mbarara" to "28_I_Love_Mbarara.mp3",
        "Living Water" to "34_Living_Water.mp3",
        "Mama" to "30_Mama.mp3",
        "Be wise" to "31_Be_wise.mp3",
        "Ninkunda" to "35_Ninkunda.mp3",
        "Bondage Breaker" to "26_Bondage_Breaker.mp3",
        "Emotional Ending remix" to "33_Emotional_Ending_remix.mp3",
        "x" to "29_x.mp3",
        "Muhara we" to "36_Muhara_we.mp3",
        "Owakigambire afro-house version" to "38_Owakigambire_afro-house_version.mp3",
        "Church Riddim" to "37_Church_Riddim.mp3",
        "Ninkunda 2" to "40_Ninkunda_2.mp3",
        "Non living thing" to "39_Non_living_thing.mp3",
        "Kitara talking" to "41_Kitara_talking.mp3",
        "Ebinyumu ebyaffe remix" to "42_Ebinyumu_ebyaffe_remix.mp3",
        "Tindatine remix" to "43_Tindatine_remix.mp3"
    )

    fun getSongs(): List<Song> = tracks.mapIndexed { index, (title, filename) ->
        Song(
            id = "mdundo-${index + 1}",
            title = title,
            coverUrl = ARTIST_PHOTO,
            durationLabel = "--:--",
            audioUrl = RELEASE_BASE_URL + filename,
            youtubeUrl = null,
            mdundoUrl = ARTIST_MDUNDO_PAGE,
            tubidyUrl = null,
            muzboxUrl = null
        )
    }
}
