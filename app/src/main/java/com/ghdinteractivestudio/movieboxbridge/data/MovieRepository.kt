package com.ghdinteractivestudio.movieboxbridge.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object MovieRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    // Base Curated Catalogue of Trending Films (Always Available Offline / Instant)
    private val curatedMovies = listOf(
        Movie(
            title = "Deadpool & Wolverine",
            overview = "Un Deadpool apathique travaille dans la vie civile quand le Tribunal des Variations Anachroniques le recrute pour sauver son univers aux côtés de Wolverine.",
            posterUrl = "https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
            releaseYear = "2024",
            durationMinutes = 128,
            rating = "8.2",
            genre = "Action / Comédie / Sci-Fi"
        ),
        Movie(
            title = "Dune : Deuxième Partie",
            overview = "Paul Atreides s'unit à Chani et aux Fremen tout en préparant sa revanche contre les conspirateurs qui ont détruit sa famille.",
            posterUrl = "https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
            releaseYear = "2024",
            durationMinutes = 166,
            rating = "8.6",
            genre = "Science-Fiction / Aventure"
        ),
        Movie(
            title = "Gladiator II",
            overview = "Des années après avoir été témoin de la mort de Maximus aux mains de son oncle, Lucius doit entrer dans le Colisée après que sa patrie ait été conquise.",
            posterUrl = "https://image.tmdb.org/t/p/w500/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg",
            releaseYear = "2024",
            durationMinutes = 148,
            rating = "8.0",
            genre = "Action / Drame / Historique"
        ),
        Movie(
            title = "Vice-Versa 2 (Inside Out 2)",
            overview = "De nouvelles émotions débarquent dans le quartier général de Riley, désormais adolescente, menées par l'Anxiété.",
            posterUrl = "https://image.tmdb.org/t/p/w500/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg",
            releaseYear = "2024",
            durationMinutes = 96,
            rating = "8.1",
            genre = "Animation / Famille"
        ),
        Movie(
            title = "Alien : Romulus",
            overview = "Alors qu'ils fouillent les profondeurs d'une station spatiale abandonnée, un groupe de jeunes colons se retrouve face à la forme de vie la plus terrifiante de l'univers.",
            posterUrl = "https://image.tmdb.org/t/p/w500/b33nnKl1GSFbao4l3fZDDqsMx0F.jpg",
            releaseYear = "2024",
            durationMinutes = 119,
            rating = "7.8",
            genre = "Horreur / Science-Fiction"
        ),
        Movie(
            title = "Venom : The Last Dance",
            overview = "Eddie et Venom sont en fuite. Traqués par leurs deux mondes respectifs, ils sont contraints de prendre une décision déchirante.",
            posterUrl = "https://image.tmdb.org/t/p/w500/aosm8Vh99loD2FuHQUbWJEx3Jqp.jpg",
            releaseYear = "2024",
            durationMinutes = 109,
            rating = "7.4",
            genre = "Action / Sci-Fi"
        ),
        Movie(
            title = "Vaiana 2 (Moana 2)",
            overview = "Après avoir reçu un appel inattendu de ses ancêtres navigateurs, Vaiana entreprend un périple vers les mers lointaines de l'Océanie.",
            posterUrl = "https://image.tmdb.org/t/p/w500/m0SbwFNCa9epW1X604Y9R8U2xM.jpg",
            releaseYear = "2024",
            durationMinutes = 100,
            rating = "7.9",
            genre = "Animation / Aventure"
        ),
        Movie(
            title = "Le Robot Sauvage (The Wild Robot)",
            overview = "Le voyage palpitant d'un robot naufragé sur une île déserte qui apprend à s'adapter à son environnement en nouant des liens avec les animaux.",
            posterUrl = "https://image.tmdb.org/t/p/w500/wTnV3PCVW5O92JMrZ1QID3Wk6sS.jpg",
            releaseYear = "2024",
            durationMinutes = 102,
            rating = "8.4",
            genre = "Animation / Science-Fiction"
        ),
        Movie(
            title = "Bad Boys: Ride or Die",
            overview = "Les flics les plus célèbres de Miami sont de retour pour un mélange explosif d'action et de comédie, mais cette fois avec un retournement de situation inattendu.",
            posterUrl = "https://image.tmdb.org/t/p/w500/nP6RliHjxsz4irTKsxe8FRhKZYl.jpg",
            releaseYear = "2024",
            durationMinutes = 115,
            rating = "7.5",
            genre = "Action / Comédie"
        ),
        Movie(
            title = "Oppenheimer",
            overview = "L'histoire de J. Robert Oppenheimer et de son rôle dans le développement de la bombe atomique au cours du projet Manhattan.",
            posterUrl = "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
            releaseYear = "2023",
            durationMinutes = 180,
            rating = "8.9",
            genre = "Drame / Historique / Biographie"
        ),
        Movie(
            title = "Twisters",
            overview = "Une ancienne chasseuse de tempêtes est ramenée sur les plaines ouvertes pour tester un nouveau système de détection face à des tornades sans précédent.",
            posterUrl = "https://image.tmdb.org/t/p/w500/b33nnKl1GSFbao4l3fZDDqsMx0F.jpg",
            releaseYear = "2024",
            durationMinutes = 122,
            rating = "7.3",
            genre = "Action / Aventure / Catastrophe"
        ),
        Movie(
            title = "Le Royaume de la Planète des Singes",
            overview = "Plusieurs générations après le règne de César, les singes sont l'espèce dominante et vivent en harmonie, tandis que les humains sont réduits à l'état sauvage.",
            posterUrl = "https://image.tmdb.org/t/p/w500/gKkl37BQuKTanygYQG1pyYgLVgf.jpg",
            releaseYear = "2024",
            durationMinutes = 145,
            rating = "7.6",
            genre = "Science-Fiction / Action"
        ),
        Movie(
            title = "Beetlejuice Beetlejuice",
            overview = "Après une tragédie familiale inattendue, trois générations de la famille Deetz reviennent à Winter River. Toujours hantée par Beetlejuice, la vie de Lydia bascule.",
            posterUrl = "https://image.tmdb.org/t/p/w500/kKgQzkUCUmgn05qNkWFL9wt9uuh.jpg",
            releaseYear = "2024",
            durationMinutes = 104,
            rating = "7.4",
            genre = "Comédie / Fantastique"
        ),
        Movie(
            title = "Sans un Bruit : Jour 1",
            overview = "Découvrez le jour où le monde est devenu silencieux lors de l'invasion de créatures ultra-sensibles aux bruits.",
            posterUrl = "https://image.tmdb.org/t/p/w500/yrpPYKijndM99GB39hENJIzPTH6.jpg",
            releaseYear = "2024",
            durationMinutes = 99,
            rating = "7.3",
            genre = "Horreur / Sci-Fi"
        ),
        Movie(
            title = "Godzilla x Kong : Le Nouvel Empire",
            overview = "Le tout-puissant Kong et le redoutable Godzilla doivent s'unir contre une menace colossale tapie au cœur de notre monde.",
            posterUrl = "https://image.tmdb.org/t/p/w500/tMefBSflR6PGQLv7WvFPpKLZkyk.jpg",
            releaseYear = "2024",
            durationMinutes = 115,
            rating = "7.5",
            genre = "Action / Sci-Fi / Monstres"
        ),
        Movie(
            title = "Furiosa : Une saga Mad Max",
            overview = "Alors que le monde s'écroule, la jeune Furiosa est arrachée à la Terre Verte des Mille Mères et tombe entre les mains d'une horde de motards.",
            posterUrl = "https://image.tmdb.org/t/p/w500/iADOJ8Zymht2JPMoy3R7xUMZqaC.jpg",
            releaseYear = "2024",
            durationMinutes = 148,
            rating = "7.8",
            genre = "Action / Aventure / Post-apocalyptique"
        ),
        Movie(
            title = "Smile 2",
            overview = "Sur le point d'entamer une tournée mondiale, la star de la pop Skye Riley commence à vivre des événements terrifiants et inexplicables.",
            posterUrl = "https://image.tmdb.org/t/p/w500/aE85MnPIvBzbvQfCslqjB94nknj.jpg",
            releaseYear = "2024",
            durationMinutes = 127,
            rating = "7.2",
            genre = "Horreur / Mystère"
        ),
        Movie(
            title = "The Batman",
            overview = "Lorsqu'un tueur s'en prend à l'élite de Gotham par une série de machinations sadiques, Bruce Wayne mène l'enquête dans les bas-fonds.",
            posterUrl = "https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg",
            releaseYear = "2022",
            durationMinutes = 176,
            rating = "8.2",
            genre = "Action / Policier / Thriller"
        ),
        Movie(
            title = "Interstellar",
            overview = "Une équipe d'explorateurs franchit un trou de ver récemment découvert afin de trouver une nouvelle planète habitable pour l'humanité.",
            posterUrl = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            releaseYear = "2014",
            durationMinutes = 169,
            rating = "8.8",
            genre = "Science-Fiction / Aventure"
        ),
        Movie(
            title = "John Wick : Chapitre 4",
            overview = "John Wick découvre un moyen de vaincre la Grande Table. Mais avant de gagner sa liberté, il doit affronter un nouvel ennemi aux alliances puissantes.",
            posterUrl = "https://image.tmdb.org/t/p/w500/vZloFAK7NKnMGKEslUsZloNXzbp.jpg",
            releaseYear = "2023",
            durationMinutes = 169,
            rating = "8.1",
            genre = "Action / Thriller"
        )
    )

    fun getMovies(): List<Movie> {
        return curatedMovies
    }

    suspend fun getLiveOrCuratedMovies(): List<Movie> = withContext(Dispatchers.IO) {
        // Tente de récupérer les tendances réelles en ligne si disponible, sinon retourne le catalogue
        try {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/trending/movie/week?api_key=e892c2a045ff8a87b649d034fe29910e&language=fr-FR")
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val json = JSONObject(body)
                    val results = json.optJSONArray("results")
                    if (results != null && results.length() > 0) {
                        val liveList = mutableListOf<Movie>()
                        for (i in 0 until results.length()) {
                            val item = results.getJSONObject(i)
                            val title = item.optString("title", item.optString("name", "Film"))
                            val overview = item.optString("overview", "Aucune description disponible.")
                            val posterPath = item.optString("poster_path", "")
                            val releaseDate = item.optString("release_date", "2024").take(4)
                            val vote = String.format("%.1f", item.optDouble("vote_average", 7.5))
                            if (posterPath.isNotBlank()) {
                                liveList.add(
                                    Movie(
                                        title = title,
                                        overview = overview,
                                        posterUrl = "https://image.tmdb.org/t/p/w500$posterPath",
                                        releaseYear = releaseDate,
                                        rating = vote
                                    )
                                )
                            }
                        }
                        if (liveList.isNotEmpty()) {
                            return@withContext liveList
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Repli gracieux sur le catalogue intégré de haute qualité
        }
        return@withContext curatedMovies
    }
}
