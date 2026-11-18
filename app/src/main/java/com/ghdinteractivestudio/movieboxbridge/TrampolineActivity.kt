package com.ghdinteractivestudio.movieboxbridge

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class TrampolineActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movieTitle = intent.data?.getQueryParameter("title") ?: "Film"

        Toast.makeText(
            applicationContext,
            "🎬 Lancement de MovieBox : $movieTitle",
            Toast.LENGTH_SHORT
        ).show()

        // Lancement direct de MovieBox
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage("com.community.mbox.tv")
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                launchIntent.putExtra("search_query", movieTitle)
                startActivity(launchIntent)
            } else {
                // Fallback Intent
                val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("oneroom://com.community.mbox.tv")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Impossible d'ouvrir MovieBox : ${e.message}", Toast.LENGTH_LONG).show()
        }

        finish()
    }
}
