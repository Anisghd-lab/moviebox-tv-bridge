package com.ghdinteractivestudio.movieboxbridge.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.tvprovider.media.tv.ChannelLogoUtils
import androidx.tvprovider.media.tv.PreviewChannel
import androidx.tvprovider.media.tv.PreviewChannelHelper
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import com.ghdinteractivestudio.movieboxbridge.data.Movie
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
            val helper = PreviewChannelHelper(context)
            var channelId = getStoredChannelId(context)
            var existingChannel: PreviewChannel? = null

            // 1. Chercher si la chaîne existe déjà
            if (channelId != -1L) {
                try {
                    existingChannel = helper.getPreviewChannel(channelId)
                } catch (e: Exception) {
                    Log.w(TAG, "Impossible de charger la chaîne existante ID=$channelId", e)
                }
            }

            if (existingChannel == null) {
                try {
                    val channels = helper.allChannels
                    for (ch in channels) {
                        if (ch.displayName == CHANNEL_NAME) {
                            existingChannel = ch
                            channelId = ch.id
                            setStoredChannelId(context, channelId)
                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Erreur lecture des chaînes système", e)
                }
            }

            // 2. Création de la chaîne si elle n'existe pas
            if (existingChannel == null) {
                Log.d(TAG, "Création d'une nouvelle chaîne pour MovieBox...")
                val appLinkUri = Uri.parse(
                    "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LEANBACK_LAUNCHER;package=com.community.mbox.tv;component=com.community.mbox.tv/com.transsion.subroom.activity.SplashActivity;end"
                )

                val newChannel = PreviewChannel.Builder()
                    .setDisplayName(CHANNEL_NAME)
                    .setDescription(CHANNEL_DESCRIPTION)
                    .setAppLinkIntentUri(appLinkUri)
                    .build()

                channelId = helper.publishChannel(newChannel)
                setStoredChannelId(context, channelId)
                Log.i(TAG, "Chaîne MovieBox créée avec succès ! ID=$channelId")

                // Stocker le logo du canal
                try {
                    val logoBitmap = createChannelLogo()
                    ChannelLogoUtils.storeChannelLogo(context, channelId, logoBitmap)
                } catch (e: Exception) {
                    Log.w(TAG, "Erreur stockage du logo de la chaîne", e)
                }

                if (requestBrowsable) {
                    try {
                        TvContractCompat.requestChannelBrowsable(context, channelId)
                    } catch (e: Exception) {
                        Log.w(TAG, "Erreur demande autorisation browsable", e)
                    }
                }
            } else {
                Log.d(TAG, "Chaîne existante réutilisée ID=$channelId")
            }

            // 3. Charger les films (dynamiques ou catalogue de base)
            val movies = MovieRepository.getLiveOrCuratedMovies()

            // 4. Mettre à jour les programmes de la chaîne
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

                    helper.publishPreviewProgram(program)
                    addedCount++
                } catch (e: Exception) {
                    Log.w(TAG, "Erreur ajout du film ${movie.title}", e)
                }
            }

            Log.i(TAG, "Synchronisation terminée : $addedCount films ajoutés à la chaîne #$channelId")
            return@withContext SyncResult(
                success = true,
                channelId = channelId,
                moviesCount = addedCount,
                message = "$addedCount films synchronisés avec succès sur la chaîne #$channelId"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Échec global de la synchronisation de la chaîne MovieBox", e)
            return@withContext SyncResult(
                success = false,
                channelId = -1L,
                moviesCount = 0,
                message = "Erreur : ${e.localizedMessage ?: e.message}"
            )
        }
    }

    private fun createChannelLogo(): Bitmap {
        val bitmap = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Fond rond sombre Obsidian
        paint.color = Color.parseColor("#0B0D12")
        canvas.drawCircle(80f, 80f, 78f, paint)

        // Bordure Cyan
        paint.color = Color.parseColor("#00D2FF")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawCircle(80f, 80f, 76f, paint)

        // Icône Play MovieBox
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#438DFF")
        val path = android.graphics.Path().apply {
            moveTo(60f, 45f)
            lineTo(115f, 80f)
            lineTo(60f, 115f)
            close()
        }
        canvas.drawPath(path, paint)

        return bitmap
    }
}
