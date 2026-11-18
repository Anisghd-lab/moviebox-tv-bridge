package com.ghdinteractivestudio.movieboxbridge.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ghdinteractivestudio.movieboxbridge.service.MovieBoxChannelManager
import com.ghdinteractivestudio.movieboxbridge.service.TvChannelSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TvBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.i("TvBootReceiver", "Reçu action: $action")

        // 1. Planifier la tâche périodique WorkManager
        TvChannelSyncWorker.schedule(context)

        // 2. Synchronisation immédiate asynchrone
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.i("TvBootReceiver", "Démarrage synchronisation initiale chaîne TV...")
                MovieBoxChannelManager.syncChannel(context, requestBrowsable = true)
            } catch (e: Exception) {
                Log.e("TvBootReceiver", "Erreur synchronisation au boot", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
