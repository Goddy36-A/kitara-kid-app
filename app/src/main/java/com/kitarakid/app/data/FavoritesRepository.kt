package com.kitarakid.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Local-only favorites. No account/backend needed — just persists a set of
 * song IDs on-device via SharedPreferences. Good enough for a personal app;
 * revisit if favorites ever need to sync across a user's devices.
 */
class FavoritesRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("kitara_favorites", Context.MODE_PRIVATE)

    private val _favoriteIds = MutableStateFlow(loadFavorites())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private fun loadFavorites(): Set<String> =
        prefs.getStringSet(KEY_FAVORITES, emptySet())?.toSet() ?: emptySet()

    fun toggleFavorite(songId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (!current.add(songId)) current.remove(songId)
        _favoriteIds.value = current
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    companion object {
        private const val KEY_FAVORITES = "favorite_ids"
    }
}
