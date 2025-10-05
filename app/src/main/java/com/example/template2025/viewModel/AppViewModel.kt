package com.example.template2025.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.template2025.dataStore.AppDataStore
import com.example.template2025.model.AdminRepository
import com.example.template2025.model.LoginCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val userType: String? = null
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val dataStore = AppDataStore(app)
    private val adminRepository = AdminRepository()
    private val _auth = MutableStateFlow(AuthState())
    val auth: StateFlow<AuthState> = _auth.asStateFlow()

    init {
        observeLoginFlag()
    }

    private fun observeLoginFlag() {
        viewModelScope.launch {
            dataStore.isLoggedInFlow.collect { logged ->
                _auth.value = AuthState(
                    isLoading = false,
                    isLoggedIn = logged,
                    userType = dataStore.userTypeFlow.firstOrNull() ?: ""
                )
            }
        }
    }

    fun login(credentials: LoginCredentials, onResult: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            _auth.value = _auth.value.copy(isLoading = true)

            val success = when (credentials) {
                is LoginCredentials.Admin -> {
                    val result = adminRepository.login(credentials.username, credentials.password)
                    if (result.isSuccess) {
                        val res = result.getOrThrow()
                        dataStore.setLoggedIn(res.success)
                        dataStore.setUserType("admin")
                    } else {
                        dataStore.clearSession()
                    }
                    result.isSuccess && result.getOrThrow().success
                }
                is LoginCredentials.Guest -> true
            }

            if (success) {
                dataStore.setLoggedIn(true)
                dataStore.setUserType(
                    when(credentials) {
                        is LoginCredentials.Admin -> "admin"
                        is LoginCredentials.Guest -> "guest"
                    }
                )
            }

            _auth.value = _auth.value.copy(isLoading = false)
            onResult(success)
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.clearSession()
        }
    }
}