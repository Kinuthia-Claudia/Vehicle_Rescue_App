package com.example.rshmad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    fun register(email: String, password: String, userType: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->

                    val userRef = database.child("users").child(authResult.user!!.uid)
                    val userData = mapOf("userType" to userType)
                    userRef.setValue(userData)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Success
                        }.addOnFailureListener {
                            _authState.value = AuthState.Error(it.message.orEmpty())
                        }
                }.addOnFailureListener {
                    _authState.value = AuthState.Error(it.message.orEmpty())
                }
        }
    }


    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _authState.value = AuthState.Success
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Error(it.message.orEmpty())
                }
        }
    }


    fun getUserType(onResult: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val userRef = database.child("users").child(userId)
        userRef.get()
            .addOnSuccessListener { snapshot ->
                val userType = snapshot.child("userType").getValue(String::class.java) ?: "unknown"
                onResult(userType)
            }
            .addOnFailureListener {
                onResult("unknown")
            }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
