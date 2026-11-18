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
- [x] Service de gestion de chaîne (`MovieBoxChannelManager`) avec logo de chaîne personnalisé et publication de `PreviewPrograms`.
- [x] Récepteur système Android TV (`TvBootReceiver`) pour l'action `android.media.tv.action.INITIALIZE_PROGRAMS`.
- [x] Worker périodique WorkManager (`TvChannelSyncWorker`) pour actualisation automatique toutes les 12 heures.
- [x] Interface utilisateur TV Jetpack Compose (`HomeScreen`) conforme Dark Mode Obsidian.
- [x] Trampoline transparent (`TrampolineActivity`) pour lancer MovieBox en plein écran au clic télécommande.

## 3. Known Gaps & Technical Debts
- Nécessite la compilation Gradle et la validation sur le terminal réel via ADB.
- L'approbation utilisateur via le prompt système Android TV ou "Personnaliser les chaînes" peut être requise selon la configuration de HomaTV.

## 4. Remaining Backlog
- Compiler le projet avec `./gradlew assembleDebug`.
- Publier la release sur GitHub sous `Anisghd-lab/moviebox-tv-bridge` (selon la skill `github-apk-release`).
- Installer l'APK sur la TwinBox 4K via ADB (`192.168.1.36:5555`).
- Déclencher la synchronisation initiale et valider l'apparition de la chaîne sur le lanceur TV.
