package com.unibo.cyberopoli.ui.screens.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.unibo.cyberopoli.data.models.Lobby
import com.unibo.cyberopoli.data.models.PlayerInfo


private const val LOBBIES = "lobbies"
private const val PLAYERS = "players"

class LobbyViewModel : ViewModel() {

    private val realtimeDB =
        Firebase.database("https://cyberopoli-default-rtdb.europe-west1.firebasedatabase.app/")

    private val _lobby = MutableLiveData<Lobby?>()
    val lobby: LiveData<Lobby?> = _lobby

    private var lobbyListener: ValueEventListener? = null

    fun observeLobby(lobbyId: String) {
        val lobbyRef = realtimeDB.getReference(LOBBIES).child(lobbyId)
        lobbyListener?.let { lobbyRef.removeEventListener(it) }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentLobby = snapshot.getValue(Lobby::class.java)
                _lobby.value = currentLobby
            }

            override fun onCancelled(error: DatabaseError) {
                _lobby.value = null
            }
        }
        lobbyRef.addValueEventListener(listener)
        lobbyListener = listener
    }

    fun joinLobby(lobbyId: String, playerName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid
        val playerInfo = PlayerInfo(name = playerName, ready = false, score = 50)
        val lobbyRef = realtimeDB.getReference(LOBBIES).child(lobbyId)

        lobbyRef.child(PLAYERS).child(userId).setValue(playerInfo).addOnSuccessListener {
            // Se la lobby non esisteva, la crea in automatico con questo player
            // Se esiste, aggiunge il giocatore
        }
    }

    fun toggleReady(lobbyId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid
        val playerRef = realtimeDB.getReference(LOBBIES).child(lobbyId).child(PLAYERS).child(userId)

        playerRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentPlayer =
                    currentData.getValue(PlayerInfo::class.java) ?: return Transaction.success(
                        currentData
                    )
                val newPlayer = currentPlayer.copy(ready = !currentPlayer.ready)
                currentData.value = newPlayer
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?
            ) {
            }
        })
    }

    fun leaveLobby(lobbyId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid
        val lobbyRef = realtimeDB.getReference(LOBBIES).child(lobbyId)

        lobbyRef.child(PLAYERS).child(userId).removeValue().addOnSuccessListener {
            lobbyRef.child(PLAYERS).get().addOnSuccessListener { snapshot ->
                if (snapshot.childrenCount == 0L) {
                    lobbyRef.removeValue()
                }
            }.addOnFailureListener {}
        }
    }

    override fun onCleared() {
        lobbyListener?.let {
            realtimeDB.getReference(LOBBIES).removeEventListener(it)
        }
        super.onCleared()
    }

    fun startGame(lobbyId: String) {
        realtimeDB.getReference(LOBBIES).child(lobbyId).child("status").setValue("in_progress")
    }
}
