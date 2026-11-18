package com.ghdinteractivestudio.movieboxbridge.service

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import com.ghdinteractivestudio.movieboxbridge.data.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SyncResult(
    val success: Boolean,
    val channelId: Long,
    val moviesCount: Int,
    val message: String
)

object MovieBoxChannelManager {

    private const val TAG = "MovieBoxChannelManager"
    private const val PREFS_NAME = "moviebox_bridge_prefs"
    private const val KEY_CHANNEL_ID = "moviebox_channel_id"

    const val CHANNEL_NAME = "MovieBox - Tendances"
    const val CHANNEL_DESCRIPTION = "Films populaires et nouveautés MovieBox sur Android TV"

    fun getStoredChannelId(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_CHANNEL_ID, -1L)
    }

    private fun setStoredChannelId(context: Context, channelId: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_CHANNEL_ID, channelId).apply()
    }

    suspend fun syncChannel(context: Context, requestBrowsable: Boolean = true): SyncResult = withContext(Dispatchers.IO) {
        try {
            var channelId = getStoredChannelId(context)

            // 1. Vérifier si la chaîne existe dans le ContentResolver
            if (channelId != -1L) {
                val checkUri = ContentUris.withAppendedId(TvContractCompat.Channels.CONTENT_URI, channelId)
                val cursor = context.contentResolver.query(checkUri, arrayOf(TvContractCompat.Channels._ID), null, null, null)
                val exists = cursor?.use { it.moveToFirst() } ?: false
                if (!exists) {
                    channelId = -1L
                }
            }

            // 2. Création directe de la chaîne via ContentResolver
            if (channelId == -1L) {
                Log.d(TAG, "Création directe du canal MovieBox dans TvContract...")
                val appLinkUri = Uri.parse(
                    "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LEANBACK_LAUNCHER;package=com.community.mbox.tv;component=com.community.mbox.tv/com.transsion.subroom.activity.SplashActivity;end"
                )

                val channel = Channel.Builder()
                    .setType(TvContractCompat.Channels.TYPE_PREVIEW)
                    .setDisplayName(CHANNEL_NAME)
                    .setDescription(CHANNEL_DESCRIPTION)
                    .setAppLinkIntentUri(appLinkUri)
                    .build()

                val channelContentValues = channel.toContentValues()
                val insertedUri = context.contentResolver.insert(TvContractCompat.Channels.CONTENT_URI, channelContentValues)

                if (insertedUri != null) {
                    channelId = ContentUris.parseId(insertedUri)
                    setStoredChannelId(context, channelId)
                    Log.i(TAG, "Chaîne MovieBox insérée avec succès ! ID=$channelId")
                } else {
                    return@withContext SyncResult(
                        success = false,
                        channelId = -1L,
                        moviesCount = 0,
                        message = "Erreur : Échec insertion de la chaîne dans TvContract"
                    )
                }
            } else {
                Log.d(TAG, "Chaîne existante réutilisée ID=$channelId")
            }

            // Demande d'affichage au lanceur TV
            if (requestBrowsable) {
                try {
                    TvContractCompat.requestChannelBrowsable(context, channelId)
                } catch (e: Exception) {
                    Log.w(TAG, "Notice demande browsable", e)
                }
            }

            // 3. Nettoyer les anciens programmes de cette chaîne
            try {
                context.contentResolver.delete(
                    TvContractCompat.PreviewPrograms.CONTENT_URI,
                    "${TvContractCompat.PreviewPrograms.COLUMN_CHANNEL_ID} = ?",
                    arrayOf(channelId.toString())
                )
            } catch (e: Exception) {
                Log.w(TAG, "Notice nettoyage programmes", e)
            }

            // 4. Charger et insérer les programmes (films)
            val movies = MovieRepository.getLiveOrCuratedMovies()
            var addedCount = 0

            for (movie in movies) {
                try {
                    val watchUri = Uri.parse("movieboxbridge://watch?title=" + Uri.encode(movie.title))

                    val program = PreviewProgram.Builder()
                        .setChannelId(channelId)
                        .setTitle(movie.title)
                        .setDescription(movie.overview)
                        .setPosterArtUri(Uri.parse(movie.posterUrl))
                        .setPosterArtAspectRatio(TvContractCompat.PreviewPrograms.ASPECT_RATIO_2_3)
                        .setType(TvContractCompat.PreviewPrograms.TYPE_MOVIE)
                        .setReleaseDate(movie.releaseYear)
                        .setDurationMillis(movie.durationMinutes * 60 * 1000)
                        .setIntentUri(watchUri)
                        .build()

                    val pUri = context.contentResolver.insert(
                        TvContractCompat.PreviewPrograms.CONTENT_URI,
                        program.toContentValues()
                    )
                    if (pUri != null) {
                        addedCount++
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Erreur ajout film ${movie.title}", e)
                }
            }

            Log.i(TAG, "Synchronisation terminée avec succès : $addedCount films ajoutés à la chaîne #$channelId")
            return@withContext SyncResult(
                success = true,
                channelId = channelId,
                moviesCount = addedCount,
                message = "Succès : $addedCount films ajoutés à la chaîne #$channelId !"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Échec global synchronisation", e)
            return@withContext SyncResult(
                success = false,
                channelId = -1L,
                moviesCount = 0,
                message = "Erreur : ${e.localizedMessage ?: e.message}"
            )
        }
    }
}
