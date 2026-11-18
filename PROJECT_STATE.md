# PROJECT_STATE — MovieBox TV Bridge (TwinBox 4K)

## 1. Goals & Vision
- **Objectif** : Créer un pont compagnon Android TV Leanback pour l'application MovieBox (`com.community.mbox.tv`) sur TwinBox 4K (`com.nes.hometv` / HomaTV launcher).
- **Fonctionnalité clé** : Publier automatiquement les rangées de films populaires/tendances de MovieBox sous forme de chaîne native d'accueil Android TV (`TvContractCompat` / `PreviewProgram`), au même titre que YouTube, Prime Video et NETFLY.
- **DeepLink & Interaction** : Un clic sur une affiche depuis le lanceur TV ouvre instantanément MovieBox (`com.community.mbox.tv`) via `TrampolineActivity`.
- **Esthétique** : Interface native Jetpack Compose en Dark Mode Obsidian Glass exclusive (CodeStation Design System).

## 2. Current Development State
- [x] Diagnostic système TwinBox 4K via ADB (Launcher `com.nes.hometv` identifié).
- [x] Architecture du pont Android TV Leanback (`androidx.tvprovider:tvprovider:1.0.0`).
- [x] Implémentation du catalogue de films populaires (`MovieRepository`) avec affiches HD TMDB et descriptions françaises.
- [x] Service de gestion de chaîne (`MovieBoxChannelManager`) avec insertion directe dans `TvContractCompat`.
- [x] Récepteur système Android TV (`TvBootReceiver`) pour l'action `android.media.tv.action.INITIALIZE_PROGRAMS`.
- [x] Worker périodique WorkManager (`TvChannelSyncWorker`) pour actualisation automatique toutes les 12 heures.
- [x] Interface utilisateur TV Jetpack Compose (`HomeScreen`) conforme Dark Mode Obsidian.
- [x] Trampoline transparent (`TrampolineActivity`) pour lancer MovieBox en plein écran au clic télécommande.
- [x] Compilation Gradle réussie (`./gradlew assembleDebug`).
- [x] Dépôt GitHub créé et synchronisé : `https://github.com/Anisghd-lab/moviebox-tv-bridge`.
- [x] Release officielle GitHub publiée : `https://github.com/Anisghd-lab/moviebox-tv-bridge/releases/tag/v1.0.0`.
- [x] Déploiement et installation sur TwinBox 4K via ADB (`192.168.1.36:5555`).
- [x] Chaîne créée et synchronisée avec succès dans `TvContractCompat` (Chaîne `#15832`, 20 films insérés).

## 3. Known Gaps & Technical Debts
- Selon les versions de lanceurs Android TV / HomaTV, si la chaîne n'apparaît pas immédiatement au premier plan, l'utilisateur peut l'activer en un clic via "Personnaliser les chaînes" tout en bas de l'écran d'accueil ou en ouvrant l'application MovieBox TV Bridge.

## 4. Remaining Backlog
- [Optionnel] Ajout de plusieurs rangées thématiques (ex: Séries TV, Animation, Nouveautés 4K).
