package com.ghdinteractivestudio.movieboxbridge.data

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val title: String,
    val overview: String,
    val posterUrl: String,
    val releaseYear: String = "2024",
    val durationMinutes: Int = 120,
    val rating: String = "8.5",
    val genre: String = "Action / Aventure"
)
