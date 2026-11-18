package com.ghdinteractivestudio.movieboxbridge.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tvprovider.media.tv.TvContractCompat
import coil.compose.AsyncImage
import com.ghdinteractivestudio.movieboxbridge.data.Movie
import com.ghdinteractivestudio.movieboxbridge.data.MovieRepository
import com.ghdinteractivestudio.movieboxbridge.service.MovieBoxChannelManager
import kotlinx.coroutines.launch

// CodeStation Dark Obsidian Palette
val BgMidnightObsidian = Color(0xFF0B0D12)
val BgCardGlass = Color(0xD9171B24)
val BorderGlass = Color(0x33FFFFFF)
val AccentElectricBlue = Color(0xFF438DFF)
val AccentNeonCyan = Color(0xFF00D2FF)
val AccentEmerald = Color(0xFF10B981)
val TextPrimary = Color(0xFFF9FAFB)
val TextSecondary = Color(0xFF9CA3AF)

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var statusText by remember { mutableStateOf("Prêt pour la synchronisation") }
    var channelId by remember { mutableStateOf(MovieBoxChannelManager.getStoredChannelId(context)) }
    var isSyncing by remember { mutableStateOf(false) }
    var movies by remember { mutableStateOf(MovieRepository.getMovies()) }

    // Initial check or load
    LaunchedEffect(Unit) {
        val storedId = MovieBoxChannelManager.getStoredChannelId(context)
        channelId = storedId
        if (storedId == -1L) {
            statusText = "Aucune chaîne enregistrée. Cliquez sur 'Synchroniser'."
        } else {
            statusText = "Chaîne active ID #$storedId"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMidnightObsidian)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "MovieBox TV Bridge",
                        color = AccentNeonCyan,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Intégration Chaînes d'Accueil Android TV TwinBox • CodeStation Engine",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (channelId != -1L) AccentEmerald.copy(alpha = 0.2f) else AccentElectricBlue.copy(alpha = 0.2f))
                        .border(1.dp, if (channelId != -1L) AccentEmerald else AccentElectricBlue, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (channelId != -1L) "CHAÎNE ACTIVE (#$channelId)" else "NON INITIALISÉ",
                        color = if (channelId != -1L) AccentEmerald else AccentElectricBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Glass Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BgCardGlass)
                    .border(1.dp, BorderGlass, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ÉTAT DU SERVICE",
                        color = AccentNeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = statusText,
                        color = TextPrimary,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Buttons Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (!isSyncing) {
                                    isSyncing = true
                                    statusText = "Synchronisation des films en cours..."
                                    coroutineScope.launch {
                                        val res = MovieBoxChannelManager.syncChannel(context, requestBrowsable = true)
                                        isSyncing = false
                                        channelId = res.channelId
                                        statusText = res.message
                                        movies = MovieRepository.getMovies()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentElectricBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.focusable()
                        ) {
                            Text(if (isSyncing) "Synchronisation..." else "⚡ Synchroniser sur l'Accueil")
                        }

                        if (channelId != -1L) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        TvContractCompat.requestChannelBrowsable(context, channelId)
                                        Toast.makeText(context, "Demande envoyée au lanceur TV !", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(AccentNeonCyan, AccentElectricBlue))),
                                modifier = Modifier.focusable()
                            ) {
                                Text("📺 Forcer Affichage Accueil", color = TextPrimary)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage("com.community.mbox.tv")
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                } else {
                                    Toast.makeText(context, "MovieBox introuvable sur cette box", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.focusable()
                        ) {
                            Text("🎬 Ouvrir MovieBox", color = TextPrimary)
                        }
                    }
                }
            }

            // Preview Carousel of Movies
            Text(
                text = "APERÇU DES FILMS QUI VONT S'AFFICHER SUR VOTRE LANCEUR TV (${movies.size}) :",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(movies) { movie ->
                    MoviePreviewCard(movie = movie)
                }
            }
        }
    }
}

@Composable
fun MoviePreviewCard(movie: Movie) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BgCardGlass)
            .border(1.dp, BorderGlass, RoundedCornerShape(12.dp))
            .focusable()
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = movie.title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = movie.releaseYear,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
                Text(
                    text = "★ ${movie.rating}",
                    color = Color(0xFFFBBF24),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
