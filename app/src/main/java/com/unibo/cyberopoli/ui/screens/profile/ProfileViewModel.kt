package com.unibo.cyberopoli.ui.screens.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unibo.cyberopoli.data.models.UserData

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<UserData?>()
    val user: MutableLiveData<UserData?> = _user

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        Log.d("ProfileViewModelAIUTOOO", "Inizializzazione ProfileViewModel")
        if (auth.currentUser != null) {
            Log.d("ProfileViewModelAIUTOOO", "Utente autenticato: ${auth.currentUser?.uid}")
            loadUserProfile()
        } else {
            _errorMessage.value = "Utente non autenticato."
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    Log.d("ProfileViewModelAIUTOOO", "Caricamento dati utente riuscito")
                    if (documentSnapshot.exists()) {
                        Log.d("ProfileViewModelAIUTOOO", "Documento utente esistente")
                        val userData = documentSnapshot.toObject(UserData::class.java)
                        if (userData != null) {
                            _user.value = userData
                            Log.d("ProfileViewModelAIUTOOO", "Dati utente: $userData")
                        } else {
                            Log.d("ProfileViewModelAIUTOOO", "Dati utente nulli")
                            _errorMessage.value = "I dati dell'utente non sono disponibili."
                        }
                    } else {
                        Log.d("ProfileViewModelAIUTOOO", "Documento utente non esistente")
                        _errorMessage.value = "Documento utente non esistente."
                    }
                }
                .addOnFailureListener { exception ->
                    _errorMessage.value = exception.message ?: "Errore nel caricamento dei dati dell'utente."
                }
        } else {
            _errorMessage.value = "Utente non autenticato."
        }
    }
}
