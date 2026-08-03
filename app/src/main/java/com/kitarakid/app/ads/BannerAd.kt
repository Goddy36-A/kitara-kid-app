package com.kitarakid.app.ads

import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * A single adaptive banner pinned wherever the caller places it. Uses an
 * adaptive size (full device width, minimal height) so it looks right on
 * both phones and tablets rather than a fixed 320x50.
 */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            val displayMetrics: DisplayMetrics = ctx.resources.displayMetrics
            val widthPx = displayMetrics.widthPixels
            val density = displayMetrics.density
            val adWidth = (widthPx / density).toInt()
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidth)

            AdView(ctx).apply {
                setAdSize(adSize)
                adUnitId = AdIds.BANNER_AD_UNIT_ID
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
