package com.example.template2025.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.dataStore.AppDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val userType: String? = null
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val dataStore = AppDataStore(app)

    private val _auth = MutableStateFlow(AuthState())
    val auth: StateFlow<AuthState> = _auth.asStateFlow()

    init {
        observeLoginFlag()
    }

    private fun observeLoginFlag() {
        viewModelScope.launch {
            combine(
                dataStore.isLoggedInFlow,
                dataStore.userTypeFlow
            ) { loggedIn, userType ->
                AuthState(
                    isLoading = false,
                    isLoggedIn = loggedIn,
                    userType = userType
                )
            }
                .catch {/* log error si quieres */}
                .collect { state ->
                    _auth.value = state
                }
        }
    }

    fun login(userType: String = "guest") {
        viewModelScope.launch {
            dataStore.setLoggedIn(true)
            dataStore.setUserType(userType)
        }
    }
    fun logout() {
        viewModelScope.launch {
            dataStore.clearSession()
        }
    }
}