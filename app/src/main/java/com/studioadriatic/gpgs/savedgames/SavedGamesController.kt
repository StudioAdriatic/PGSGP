package com.studioadriatic.gpgs.savedgames

import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.SnapshotsClient
import com.google.android.gms.games.snapshot.Snapshot
import com.google.android.gms.games.snapshot.SnapshotMetadataChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException

class SavedGamesController(
    private val activity: Activity,
    private val savedGamesListener: SavedGamesListener
) {

    companion object {
        const val RC_SAVED_GAMES = 9009
    }

    private fun isSignedIn(): Boolean = GoogleSignIn.getLastSignedInAccount(activity) != null

    fun showSavedGamesUI(
        title: String,
        allowAddBtn: Boolean,
        allowDeleteBtn: Boolean,
        maxNumberOfSavedGamesToShow: Int
    ) {
        if (!isSignedIn()) return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val intent = PlayGames.getSnapshotsClient(activity).getSelectSnapshotIntent(
                    title,
                    allowAddBtn,
                    allowDeleteBtn,
                    maxNumberOfSavedGamesToShow
                ).await()
                activity.startActivityForResult(intent, RC_SAVED_GAMES)
            } catch (e: Exception) {
                Log.e("godot", "Failed to show saved games UI", e)
            }
        }
    }

    fun saveSnapshot(
        gameName: String,
        dataToSave: String,
        description: String
    ) {
        if (!isSignedIn()) {
            savedGamesListener.onSavedGameFailed()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val snapshotsClient = PlayGames.getSnapshotsClient(activity)
                val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
                
                val openResult = snapshotsClient.open(gameName, true, conflictResolutionPolicy).await()
                val snapshot = openResult.data
                
                if (snapshot != null) {
                    // Write data to snapshot
                    snapshot.snapshotContents.writeBytes(dataToSave.toByteArray())
                    
                    // Create metadata change
                    val metadataChange = SnapshotMetadataChange.Builder()
                        .setDescription(description)
                        .build()
                    
                    // Commit and close
                    snapshotsClient.commitAndClose(snapshot, metadataChange).await()
                    savedGamesListener.onSavedGameSuccess()
                } else {
                    savedGamesListener.onSavedGameFailed()
                }
            } catch (e: Exception) {
                Log.e("godot", "Failed to save snapshot: $gameName", e)
                savedGamesListener.onSavedGameFailed()
            }
        }
    }

    fun loadSnapshot(gameName: String) {
        if (!isSignedIn()) {
            savedGamesListener.onSavedGameLoadFailed()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val snapshotsClient = PlayGames.getSnapshotsClient(activity)
                val conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED
                
                val openResult = snapshotsClient.open(gameName, true, conflictResolutionPolicy).await()
                val snapshot = openResult.data
                
                if (snapshot != null) {
                    val data = snapshot.snapshotContents.readFully()
                    val dataString = String(data)
                    savedGamesListener.onSavedGameLoadSuccess(dataString)
                } else {
                    savedGamesListener.onSavedGameLoadFailed()
                }
            } catch (e: IOException) {
                Log.e("godot", "Error reading snapshot: $gameName", e)
                savedGamesListener.onSavedGameLoadFailed()
            } catch (e: Exception) {
                Log.e("godot", "Failed to load snapshot: $gameName", e)
                savedGamesListener.onSavedGameLoadFailed()
            }
        }
    }

    fun createNewSnapshot(currentSaveName: String) {
        savedGamesListener.onSavedGameCreateSnapshot(currentSaveName)
    }
}
