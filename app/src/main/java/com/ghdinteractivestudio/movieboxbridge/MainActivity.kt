package com.ghdinteractivestudio.movieboxbridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ghdinteractivestudio.movieboxbridge.service.TvChannelSyncWorker
import com.ghdinteractivestudio.movieboxbridge.ui.HomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Planifie la synchronisation en arrière-plan
        TvChannelSyncWorker.schedule(applicationContext)

        setContent {
            HomeScreen()
        }
    }
}
